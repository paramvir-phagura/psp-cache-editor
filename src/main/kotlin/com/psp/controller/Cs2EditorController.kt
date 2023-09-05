package com.psp.controller

import com.displee.cache.CacheLibrary
import com.displee.cache.ProgressListener
import com.psp.cs2.config.ScriptConfiguration
import com.psp.cs2.notifyChooseScriptId
import com.psp.cs2.ui.alert.Notification
import com.psp.cs2.ui.autocomplete.AutoCompleteItem
import com.psp.cs2.ui.autocomplete.AutoCompletePopup
import com.psp.cs2.ui.autocomplete.AutoCompleteUtils
import com.psp.cs2.ui.autocomplete.item.AutoCompleteArgument
import com.psp.cs2.ui.autocomplete.item.AutoCompleteFunction
import com.psp.cs2.ui.buildStyle
import com.psp.view.Cs2Editor
import com.psp.view.MainView
import com.runescape.cache.Cache
import com.runescape.cs2.*
import com.runescape.cs2.util.FunctionDatabase
import javafx.application.Platform
import javafx.scene.Node
import javafx.scene.control.ListCell
import javafx.scene.control.ListView
import javafx.scene.control.Tab
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.BorderPane
import javafx.stage.DirectoryChooser
import javafx.stage.Window
import javafx.util.Callback
import org.fxmisc.flowless.VirtualizedScrollPane
import org.fxmisc.richtext.CodeArea
import org.fxmisc.richtext.LineNumberFactory
import org.fxmisc.richtext.model.StyleSpans
import org.fxmisc.richtext.model.StyleSpansBuilder
import tornadofx.Controller
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

class Cs2EditorController : Controller() {

    private val mainView: MainView by inject()
    private val view: Cs2Editor by inject()
    private val cacheDir: String by param()

    private val cachedScripts = mutableMapOf<Int, String>()

    private var temporaryAssemblyPane: Node? = null

    lateinit var cacheLibrary: CacheLibrary
    private lateinit var scripts: IntArray

    private lateinit var scriptConfiguration: ScriptConfiguration
    private lateinit var opcodesDatabase: FunctionDatabase
    private lateinit var scriptsDatabase: FunctionDatabase

    private var currentScript: CS2? = null

    init {
        view.rootPane.addEventHandler(KeyEvent.KEY_PRESSED) { e: KeyEvent ->
            if (e.isControlDown && e.code == KeyCode.N) {
                if (!this::cacheLibrary.isInitialized) {
                    return@addEventHandler
                }
                newScript(notifyChooseScriptId(cacheLibrary, cacheLibrary.index(SCRIPTS_INDEX).nextId()))
            }
        }
        view.saveMenuItem.setOnAction {
            compileScript()
        }
        view.buildMenuItem.setOnAction {
            packScript()
        }
        view.showAssemblyMenuItem.setOnAction {
            if (view.showAssemblyMenuItem.isSelected) {
                view.rootPane.right = temporaryAssemblyPane
                temporaryAssemblyPane = null
            } else {
                temporaryAssemblyPane = view.rootPane.right
                view.rootPane.right = null
            }
        }
        view.newMenuItem.setOnAction {
            newScript(notifyChooseScriptId(cacheLibrary, cacheLibrary.index(SCRIPTS_INDEX).nextId()))
        }
        view.scriptList.cellFactory = object : Callback<ListView<Int>, ListCell<Int>> {
            override fun call(param: ListView<Int>): ListCell<Int> {
                return object : ListCell<Int>() {
                    override fun updateItem(item: Int?, empty: Boolean) {
                        super.updateItem(item, empty)
                        if (item == null) {
                            return
                        }
                        super.setText("Script $item")
                    }
                }
            }
        }
        view.scriptList.selectionModel.selectedItemProperty().addListener { observable, oldValue, newValue ->
            if (newValue == null) {
                return@addListener
            }
            status("decompiling script $newValue")
            currentScript = readScript(newValue)
            if (currentScript == null) {
                //TODO Popup failed to read script
                status("ready")
                return@addListener
            }
            val hash = cacheLibrary.hashCode().toString() + " - " + newValue.toString().hashCode()
            for (tab in view.tabPane.tabs) {
                if (tab.properties["hash"] == hash) {
                    view.tabPane.selectionModel.select(tab)
                    return@addListener
                }
            }
            val script = decompileScript()
            status("ready")
            val codeArea = createCodeArea(script, true)
            val tab = Tab("Script $newValue", BorderPane(VirtualizedScrollPane(codeArea)))
            tab.properties["hash"] = hash
            tab.userData = currentScript
            view.tabPane.tabs.add(tab)
            view.tabPane.selectionModel.selectLast()
            refreshAssemblyCode()
            compileScript()
        }
        view.searchField.textProperty().addListener { observable, oldValue, newValue ->
            if (newValue == null) {
                return@addListener
            }
            search(newValue)
        }
        view.tabPane.selectionModel.selectedItemProperty().addListener { observable, oldValue, newValue ->
            if (oldValue != null) {
                val codeArea =
                    ((oldValue.content as BorderPane).center as VirtualizedScrollPane<MainCodeArea>).content as MainCodeArea
                codeArea.autoCompletePopup?.hide()
            }
            if (newValue == null || newValue.userData == null) {
                if (newValue == null) {
                    replaceAssemblyCode("")
                }
                return@addListener
            }
            currentScript = newValue.userData as CS2
            refreshAssemblyCode()
        }
        view.assemblyCodePane.center = BorderPane(VirtualizedScrollPane(createCodeArea("", editable = false)))

        //Disable assembly pane by default
        view.showAssemblyMenuItem.selectedProperty().set(false)
        view.showAssemblyMenuItem.onAction.handle(null)

        //init singleton
        AutoCompleteUtils

        openCache(File(cacheDir))
    }

    private fun openCache(f: File? = null) {
        var mehFile = f
        if (mehFile == null) {
            val chooser = DirectoryChooser()
            mehFile = chooser.showDialog(mainWindow()) ?: return
        }
        view.scriptList.isDisable = true
        tornadofx.runAsync {
            try {
                cacheLibrary = CacheLibrary(mehFile.absolutePath, listener = object : ProgressListener {
                    override fun notify(progress: Double, message: String?) {
                        if (message == null) {
                            return
                        }
                    }
                })
                if (!cacheLibrary.exists(SCRIPTS_INDEX)) {
                    Platform.runLater {
                        Notification.error("Can't find any scripts in the cache you trying to load.")
                        clearCache()
                    }
                    return@runAsync
                } else if (cacheLibrary.isRS3()) {
                    Platform.runLater {
                        Notification.error("RS3 caches are not supported.")
                        clearCache()
                    }
                }
                loadScripts()
                createScriptConfigurations()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun loadScripts() {
        status("Populating scripts...")
        val ids = cacheLibrary.index(SCRIPTS_INDEX).archiveIds()
        scripts = ids.copyOf()
        search(view.searchField.text)
    }

    private fun search(text: String) {
        val list = arrayListOf<Int>()
        for (i in scripts) {
            try {
                if (text.startsWith("op_")) {
                    val data = cacheLibrary.data(SCRIPTS_INDEX, i)
                    val script = CS2Reader.readCS2ScriptNewFormat(
                        data,
                        i,
                        scriptConfiguration.unscrambled,
                        scriptConfiguration.disableSwitches,
                        scriptConfiguration.disableLongs
                    )
                    val opcode = text.replace("op_", "").toInt()
                    for (instruction in script.instructions) {
                        if (instruction.opcode == opcode) {
                            list.add(i)
                            break
                        }
                    }
                } else if (text.isNotEmpty() && !i.toString().startsWith(text)) {
                    val cached = cachedScripts[i]
                    if (::scriptConfiguration.isInitialized && cached != null && cached.contains(text)) {
                        list.add(i)
                    }
                } else {
                    list.add(i)
                }
            } catch (t: NumberFormatException) {
                list.add(i)
            }
        }
        view.scriptList.items.clear()
        view.scriptList.items.addAll(list)
    }

    private fun createCodeArea(
        initialText: String, showLineNumbers: Boolean = false, editable: Boolean = true
    ): MainCodeArea {
        val codeArea = MainCodeArea(editable)
        if (showLineNumbers) {
            codeArea.paragraphGraphicFactory = LineNumberFactory.get(codeArea)
        }
        codeArea.buildStyle()
        val whiteSpace = Pattern.compile("^\\s+")
        codeArea.addEventHandler(KeyEvent.KEY_PRESSED) { e: KeyEvent ->
            if (e.isControlDown && e.code == KeyCode.S) {
                compileScript()
            } else if (e.isControlDown && e.code == KeyCode.D) {
                packScript()
            } else if (e.code == KeyCode.ENTER) {
                val caretPosition = codeArea.caretPosition
                val currentParagraph = codeArea.currentParagraph
                val m0 = whiteSpace.matcher(codeArea.getParagraph(currentParagraph - 1).segments[0])
                if (m0.find()) {
                    Platform.runLater { codeArea.insertText(caretPosition, m0.group()) }
                }
            } else if (e.isShiftDown && e.code == KeyCode.TAB) {
                if (codeArea.text.substring(codeArea.caretPosition - 1, codeArea.caretPosition) == "\t") {
                    codeArea.deletePreviousChar()
                }
            }
        }
        codeArea.isEditable = editable
        codeArea.replaceText(0, 0, initialText)
        return codeArea
    }

    private fun createScriptConfigurations() {
        status("guessing script configuration...")
        val scriptConfiguration = guessConfiguration()
        if (scriptConfiguration == null) {
            Platform.runLater {
                Notification.error("Unable to find correct script configuration for this cache.")
            }
            clearCache()
            return
        }
        println("Using config: $scriptConfiguration")
        this.scriptConfiguration = scriptConfiguration
        opcodesDatabase = FunctionDatabase(
            javaClass.getResourceAsStream(scriptConfiguration.opcodeDatabase), false, scriptConfiguration.scrambled
        )
        status("generating script signatures...")
        scriptsDatabase = generateScriptsDatabase(scriptConfiguration)
        status("generating auto complete items...")
        generateAutoCompleteItems()
        status("caching all scripts...")
        cacheAllScripts()
        status("ready")
        Platform.runLater {
            view.scriptList.isDisable = false
            view.newMenuItem.isDisable = false
            view.saveMenuItem.isDisable = false
            view.buildMenuItem.isDisable = false
        }
    }

    private fun guessConfiguration(): ScriptConfiguration? {
        val testUnit = { config: ScriptConfiguration ->
            Boolean
            val ids = cacheLibrary.index(SCRIPTS_INDEX).archiveIds()
            var error = 0
            for (id in ids) {
                val data = cacheLibrary.data(SCRIPTS_INDEX, id)
                try {
                    CS2Reader.readCS2ScriptNewFormat(
                        data, id, config.unscrambled, config.disableSwitches, config.disableLongs
                    )
                } catch (e: Throwable) {
                    error++
                    if (error >= 2) {
                        break
                    }
                }
            }
            println("config: ${config.version} $error")
            error < 2
        }
        var configuration: ScriptConfiguration? = null
        if (cacheLibrary.isOSRS()) {
            val configurations = arrayOf(
                ScriptConfiguration(154, "/cs2/opcode/database/osrs.ini", false, true),
                ScriptConfiguration(176, "/cs2/opcode/database/osrs.ini", false, true),
                ScriptConfiguration(179, "/cs2/opcode/database/osrs.ini", false, true)
            )
            for (i in configurations) {
                if (testUnit(i)) {
                    configuration = i
                }
            }
        } else {
            val configurations = arrayOf(
                //< 500
                ScriptConfiguration(464, "/cs2/opcode/database/rs2_new.ini", true, true),
                //>= 500 && < 643
                ScriptConfiguration(667, "/cs2/opcode/database/rs2_old.ini", false, true),
                //>= 643
                ScriptConfiguration(667, "/cs2/opcode/database/rs2_new.ini", false, false),
                //718
                ScriptConfiguration(718, "/cs2/opcode/database/rs2_new.ini", false, false) //TODO Fix 718
            )
            for (i in configurations) {
                if (testUnit(i)) {
                    configuration = i
                }
            }
        }
        return configuration ?: ScriptConfiguration(718, "/cs2/opcode/database/rs2_new.ini", false, false)
    }

    private fun generateScriptsDatabase(configuration: ScriptConfiguration, loop: Int = 6): FunctionDatabase {
        val opcodesDatabase = FunctionDatabase(
            javaClass.getResourceAsStream(configuration.opcodeDatabase), false, configuration.scrambled
        )
        val scriptsDatabase = FunctionDatabase()
        val ids = cacheLibrary.index(SCRIPTS_INDEX).archiveIds()
        for (l in 0 until loop) {
            for (id in ids) {
                val data = cacheLibrary.data(SCRIPTS_INDEX, id)
                try {
                    val script = CS2Reader.readCS2ScriptNewFormat(
                        data, id, configuration.unscrambled, configuration.disableSwitches, configuration.disableLongs
                    )
                    val decompiler = CS2Decompiler(script, opcodesDatabase, scriptsDatabase)
                    try {
                        decompiler.decompile()
                    } catch (ex: Throwable) {

                    }
                    val function = decompiler.function
                    if (function.returnType == CS2Type.UNKNOWN) {
                        continue
                    }
                    val info = scriptsDatabase.getInfo(id)
                    info.name = function.name
                    if (info.returnType === CS2Type.UNKNOWN) {
                        info.returnType = function.returnType
                    }
                    for (a in function.argumentLocals.indices) {
                        info.argumentTypes[a] = function.argumentLocals[a].type
                        info.argumentNames[a] = function.argumentLocals[a].name
                    }
                } catch (e: Exception) {
                    Platform.runLater {
                        mainView.error(e.message ?: "Error parsing script.")
                    }
                }
            }
        }
        var successCount = 0
        val writer = StringWriter()
        for (id in ids) {
            val info = scriptsDatabase.getInfo(id)
            if (info?.getReturnType() == null /* || info.getReturnType() == CS2Type.UNKNOWN*/) {
                continue
            }
            if (info.returnType != CS2Type.UNKNOWN) {
                successCount++
            }
            if (info.getReturnType().isStructure) {
                writer.write("$id ${info.getName()} {${info.getReturnType().toString().replace(" ".toRegex(), "")}}")
            } else {
                writer.write("$id ${info.getName()} ${info.getReturnType()}")
            }
            for (a in info.getArgumentTypes().indices) {
                writer.write(" ${info.getArgumentTypes()[a]} ${info.getArgumentNames()[a]}")
            }
            writer.write("\r\n")
        }
        println("Generated $successCount/${ids.size} script signatures.")
        val signatures = writer.toString()
        return FunctionDatabase(signatures, true, null)
    }

    private fun generateAutoCompleteItems() {
        AutoCompleteUtils.dynamicItems.clear()
        AutoCompleteUtils.clearDynamicChildren()
        val text = javaClass.getResource(scriptConfiguration.opcodeDatabase).readText()
        for (line in text.lines()) {
            if (line.isEmpty() || line.startsWith(" ") || line.startsWith("//") || line.startsWith("#")) {
                continue
            }
            val split = line.split(" ")
            val opcode = split[0].toInt()
            if (!scriptConfiguration.scrambled.containsKey(opcode)) {
                continue
            }
            var list: MutableList<AutoCompleteItem>? = AutoCompleteUtils.dynamicItems
            if (FlowBlocksGenerator.isObjectOpcode(opcode) || FlowBlocksGenerator.isObjectWidgetOpcode(opcode)) {
                list = AutoCompleteUtils.getObject(CS2Type.WIDGET_PTR, true)?.dynamicChildren
            }
            val name = split[1]
            val returnTypes = if (split[2].contains("|")) {
                val multiReturn = split[2].split("\\|".toRegex())
                Array(multiReturn.size) {
                    CS2Type.forDesc(multiReturn[it])
                }
            } else {
                arrayOf(CS2Type.forDesc(split[2]))
            }
            val argSize = (split.size - 2) / 2
            val argTypes = Array(argSize) {
                val index = 3 + (it * 2)
                CS2Type.forDesc(split[index])
            }
            val argNames = Array(argSize) {
                val index = 3 + (it * 2)
                split[index + 1]
            }
            val function = AutoCompleteFunction(name, returnTypes[0], Array(argSize) {
                AutoCompleteArgument(argNames[it], argTypes[it])
            })
            if (list != null && list.firstOrNull { it.name == name } == null) {
                list.add(function)
            }
        }
    }

    private fun readScript(id: Int): CS2? {
        val data = cacheLibrary.data(SCRIPTS_INDEX, id)
        return CS2Reader.readCS2ScriptNewFormat(
            data,
            id,
            scriptConfiguration.unscrambled,
            scriptConfiguration.disableSwitches,
            scriptConfiguration.disableLongs
        )
    }

    private fun cacheAllScripts() {
        if (!::scriptConfiguration.isInitialized) {
            return
        }
        cachedScripts.clear()
        for (i in scripts) {
            try {
                val decompiled = decompileScript(i)
                cachedScripts[i] = decompiled
            } catch (e: java.lang.Exception) {

            }
        }
    }

    private fun decompileScript(): String {
        val script = currentScript ?: return ""
        val decompiler = CS2Decompiler(script, opcodesDatabase, scriptsDatabase)
        try {
            decompiler.decompile()
        } catch (t: Throwable) {
            t.printStackTrace()
        }
        decompiler.optimize()
        val printer = CodePrinter()
        decompiler.function.print(printer)
        return printer.toString()
    }

    private fun decompileScript(id: Int): String {
        val data = cacheLibrary.data(SCRIPTS_INDEX, id)
        val script = CS2Reader.readCS2ScriptNewFormat(
            data,
            id,
            scriptConfiguration.unscrambled,
            scriptConfiguration.disableSwitches,
            scriptConfiguration.disableLongs
        )
        val decompiler = CS2Decompiler(script, opcodesDatabase, scriptsDatabase)
        try {
            decompiler.decompile()
        } catch (t: Throwable) {
            //t.printStackTrace()
        }
        decompiler.optimize()
        val printer = CodePrinter()
        decompiler.function.print(printer)
        return printer.toString()
    }

    private fun compileScript() {
        val script = currentScript ?: return
        val activeCodeArea = activeCodeArea()
        try {
            val parser = CS2ScriptParser.parse(activeCodeArea.text, opcodesDatabase, scriptsDatabase)
            activeCodeArea.autoCompletePopup?.init(parser)
            refreshAssemblyCode()
            printConsoleMessage("Compiled script ${script.scriptID}.")
        } catch (t: Throwable) {
            t.printStackTrace()
            printConsoleMessage(t.message)
        }
    }

    private fun newScript(newId: Int?) {
        if (newId == null) {
            return
        }
        val function = CS2ScriptParser.parse("void script_$newId() {\n\treturn;\n}", opcodesDatabase, scriptsDatabase)
        val compiler = CS2Compiler(
            function,
            scriptConfiguration.scrambled,
            scriptConfiguration.disableSwitches,
            scriptConfiguration.disableLongs
        )
        val compiled = compiler.compile(null) ?: throw Error("Failed to compile.")
        cacheLibrary.put(SCRIPTS_INDEX, newId, compiled)

        if (!cacheLibrary.index(SCRIPTS_INDEX).update()) {
            Notification.error("Failed to create new script with id $newId.")
        } else {
            Notification.info("A new script has been created with id $newId.")
            loadScripts()
        }
    }

    private fun packScript() {
        val script = currentScript ?: return
        val activeCodeArea = activeCodeArea()
        try {
            val function = CS2ScriptParser.parse(activeCodeArea.text, opcodesDatabase, scriptsDatabase)
            val compiler = CS2Compiler(
                function,
                scriptConfiguration.scrambled,
                scriptConfiguration.disableSwitches,
                scriptConfiguration.disableLongs
            )
            val compiled = compiler.compile(null) ?: throw Error("Failed to compile.")
            cacheLibrary.put(SCRIPTS_INDEX, script.scriptID, compiled)
            activeCodeArea.autoCompletePopup?.init(function)
            if (cacheLibrary.index(SCRIPTS_INDEX).update()) {
                printConsoleMessage("Packed script ${script.scriptID} successfully.")
            } else {
                printConsoleMessage("Failed to pack script ${script.scriptID}.")
            }
        } catch (t: Throwable) {
            t.printStackTrace()
            printConsoleMessage(t.message)
        }
    }

    private fun printConsoleMessage(line: String?) {
        view.compileArea.text =
            timeFormat.format(Date.from(Instant.now())) + " -> " + line + System.lineSeparator() + view.compileArea.text
    }

    private fun refreshAssemblyCode() {
        try {
            val parser = CS2ScriptParser.parse(activeCodeArea().text, opcodesDatabase, scriptsDatabase)
            val compiler = CS2Compiler(
                parser,
                scriptConfiguration.scrambled,
                scriptConfiguration.disableSwitches,
                scriptConfiguration.disableLongs
            )
            val stringWriter = StringWriter()
            val writer = PrintWriter(stringWriter)
            compiler.compile(writer)
            replaceAssemblyCode(stringWriter.toString())
        } catch (e: Exception) {
            //do nothing
            //replaceAssemblyCode("Failed to generate assembly code.")
        }
    }

    private fun replaceAssemblyCode(code: String) {
        val assemblyCodeArea =
            ((view.assemblyCodePane.center as BorderPane).center as VirtualizedScrollPane<CodeArea>).content as CodeArea
        assemblyCodeArea.replaceText(0, assemblyCodeArea.length, code)
    }

    private fun clearCache() {
        view.tabPane.tabs.clear()
        view.scriptList.items.clear()
        view.scriptList.isDisable = true
        view.newMenuItem.isDisable = true
        view.saveMenuItem.isDisable = true
        view.buildMenuItem.isDisable = true
    }

    private fun status(status: String) {
        Platform.runLater {
            view.statusLabel.text = "Status: $status"
        }
    }

    private fun activeCodeArea(): MainCodeArea {
        return ((view.tabPane.selectionModel.selectedItem.content as BorderPane).center as VirtualizedScrollPane<CodeArea>).content as MainCodeArea
    }

    fun mainWindow(): Window {
        return view.mainCodePane.scene.window
    }

    companion object {

        const val SCRIPTS_INDEX = 12

        var timeFormat = SimpleDateFormat("HH:mm:ss")
        val VAR_LIST = mutableListOf<String>()
        val KEYWORDS = arrayOf(
            "string",
            "string[]",
            "boolean",
            "break",
            "case",
            "char",
            "continue",
            "default",
            "do",
            "else",
            "for",
            "goto",
            "if",
            "int",
            "int[]",
            "long",
            "long[]",
            "return",
            "switch",
            "this",
            "void",
            "while",
            "true",
            "false",
            "null"
        )
        private val BI_CLASSES = arrayOf(
            CS2Type.FONTMETRICS,
            CS2Type.SPRITE,
            CS2Type.MODEL,
            CS2Type.MIDI,
            CS2Type.DATAMAP,
            CS2Type.ATTRIBUTEMAP,
            CS2Type.CONTAINER,
            CS2Type.WIDGET_PTR,
            CS2Type.LOCATION,
            CS2Type.ITEM,
            CS2Type.COLOR,
            CS2Type.IDENTIKIT,
            CS2Type.ANIM,
            CS2Type.MAPID,
            CS2Type.GRAPHIC,
            CS2Type.SKILL,
            CS2Type.NPCDEF,
            CS2Type.QCPHRASE,
            CS2Type.CHATCAT,
            CS2Type.TEXTURE,
            CS2Type.STANCE,
            CS2Type.SPELL,
            CS2Type.CATEGORY,
            CS2Type.SOUNDEFFECT,
            CS2Type.CALLBACK
        )

        private val KEYWORD_PATTERN = "\\b(" + java.lang.String.join(
            "|", *KEYWORDS.map { it.replace("[", "\\[").replace("]", "\\]") }.toTypedArray()
        ) + ")\\b"
        private var VAR_PATTERN = "\\b(" + java.lang.String.join("|", *VAR_LIST.toTypedArray()) + ")\\b"
        private val BICLASS_PATTERN = "\\b(" + java.lang.String.join(
            "|", *BI_CLASSES.map { it.name.replace("[", "\\[").replace("]", "\\]") }.toTypedArray()
        ) + ")\\b"

        private const val PAREN_PATTERN = "\\(|\\)"
        private const val BRACE_PATTERN = "\\{|\\}"
        private const val BRACKET_PATTERN = "\\[|\\]"
        private const val SEMICOLON_PATTERN = "\\;"
        private const val STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\""
        private const val NUMBER_PATTERN = "\\b\\d+\\b"
        private const val COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/"
        private const val COLOR_PATTERN = "0x.{3,6}\\b"

        //TODO Fix these highlightings
        private val CS2_CALL_PATTERN = "\\bscript_\\d+(.*)\\b"
        private val CS2_HOOK_PATTERN = "\\b&script_\\d+(.*)"

        fun computeHighlighting(text: String): StyleSpans<Collection<String>>? {
            VAR_PATTERN = "\\b(" + java.lang.String.join("|", *VAR_LIST.toTypedArray()) + ")\\b"
            val pattern =
                Pattern.compile("(?<KEYWORD>$KEYWORD_PATTERN)|(?<BICLASS>$BICLASS_PATTERN)|(?<NUMBER>$NUMBER_PATTERN)|(?<PAREN>$PAREN_PATTERN)|(?<BRACE>$BRACE_PATTERN)|(?<BRACKET>$BRACKET_PATTERN)|(?<SEMICOLON>$SEMICOLON_PATTERN)|(?<STRING>$STRING_PATTERN)|(?<COMMENT>$COMMENT_PATTERN)|(?<COLOR>$COLOR_PATTERN)|(?<VAR>$VAR_PATTERN)"/*|(?<CS2CALL>$CS2_CALL_PATTERN)|(?<CS2HOOK>$CS2_HOOK_PATTERN)"*/)
            val matcher: Matcher = pattern.matcher(text)
            var lastKwEnd = 0
            val spansBuilder = StyleSpansBuilder<Collection<String>>()
            while (matcher.find()) {
                val styleClass = when {
                    matcher.group("KEYWORD") != null -> "keyword"
                    matcher.group("PAREN") != null -> "paren"
                    matcher.group("BRACE") != null -> "brace"
                    matcher.group("BRACKET") != null -> "bracket"
                    matcher.group("SEMICOLON") != null -> "semicolon"
                    matcher.group("STRING") != null -> "string"
                    matcher.group("NUMBER") != null -> "number"
                    matcher.group("COMMENT") != null -> "comment"
                    matcher.group("BICLASS") != null -> "biclass"/*matcher.group("CS2CALL") != null -> "cs2-call"
                    matcher.group("CS2HOOK") != null -> "cs2-hook"*/
                    matcher.group("COLOR") != null -> "color"
                    matcher.group("VAR") != null -> "var"
                    else -> null
                }
                if (styleClass == null) {
                    continue
                }
                spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd)
                spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start())
                lastKwEnd = matcher.end()
            }
            spansBuilder.add(Collections.emptyList(), text.length - lastKwEnd)
            return spansBuilder.create()
        }

    }

    private class MainCodeArea(autoComplete: Boolean = true) : CodeArea() {
        val autoCompletePopup: AutoCompletePopup? = if (autoComplete) AutoCompletePopup(this) else null
    }
}
