package com.psp.view

import com.psp.controller.Cs2EditorController
import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.scene.layout.BorderPane
import tornadofx.insets
import tornadofx.listview
import tornadofx.paddingAll
import tornadofx.textarea

class Cs2Editor : Editor("Cs2 Editor") {

    private val controller: Cs2EditorController by inject(params = params)

    val saveMenuItem: MenuItem by fxid()
    val buildMenuItem: MenuItem by fxid()
    val showAssemblyMenuItem: CheckMenuItem by fxid()
    val newMenuItem: MenuItem by fxid()
    val searchField: TextField by fxid()
    val rootPane: BorderPane by fxid()
    val statusLabel: Label by fxid()
    val scriptList: ListView<Int> by fxid()
    val mainCodePane: BorderPane by fxid()
    val tabPane: TabPane by fxid()
    val compilePane: BorderPane by fxid()
    val compileArea: TextArea by fxid()
    val assemblyCodePane: BorderPane by fxid()

    var selectedScriptId: Int? = null

    override val root: BorderPane by fxml("/fxml/Cs2Editor.fxml")

    override fun onTabSelected() {
        controller
    }
}
