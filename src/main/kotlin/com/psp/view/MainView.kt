package com.psp.view

import com.psp.controller.MainViewController
import com.psp.util.Constants
import com.psp.util.Preferences
import javafx.beans.property.SimpleBooleanProperty
import javafx.collections.ListChangeListener
import javafx.geometry.Insets
import javafx.scene.control.Label
import javafx.scene.control.MenuItem
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.paint.Color
import tornadofx.*
import java.io.File

class MainView : View("psp-cache-editor") {

    private val controller: MainViewController by inject()

    private val editorTabPane = TabPane()
    private val statusLabel = Label()

    val cacheControlsEnabled = SimpleBooleanProperty()
    val spriteEditorEnabled = SimpleBooleanProperty()
    val cs2EditorEnabled = SimpleBooleanProperty()

    override val root = borderpane {
        top = vbox {
            menubar {
                menu("File") {
                    item("Open").action {
                        controller.locateCache()
                    }
                    menu("Open Recent") {
                        fun refresh() {
                            items.removeAll()

                            Preferences.instance.recents.value.map {
                                val recentItem = MenuItem(it)
                                recentItem.action {
                                    if (controller.openCache(File(it))) {
                                        closeTabs()
                                    }
                                }
                                recentItem
                            }.forEach { this.items.add(it) }
                        }

                        Preferences.instance.recents.value.addListener(ListChangeListener {
                            refresh()
                        })
                        refresh()
                    }
                    separator()
                    item("About") {
                        isDisable = true
                    }
                    item("Settings") {
                        isDisable = true
                    }
                    item("Exit").action {
                        controller.exit()
                    }
                }

                menu("Edit") {
                    item("Sprite Editor") {
                        enableWhen(spriteEditorEnabled)
                        action {
                            controller.newSpriteEditorTab()
                        }
                    }
                    item("Cs2 Editor") {
                        enableWhen(cs2EditorEnabled)
                        action {
                            controller.newCs2EditorTab()
                        }
                    }
                    item("Model Editor") {
                        isDisable = true
                    }
                    item("Music Editor") {
                        isDisable = true
                    }
                }
            }
        }

        center = stackpane {
            add(editorTabPane)
            // TODO Change text based on state
            label("Get started by selecting an editor from the drop-down menu.") {
                styleClass += "h1"
                editorTabPane.tabs.addListener(ListChangeListener {
                    if (it.list.size == 0) {
                        show()
                    } else {
                        hide()
                    }
                })
            }
        }

        bottom = vbox {
            // TODO Action button (e.g., open directory containing sprites for convenience)
            add(statusLabel.apply {
                styleClass += "h2"
            })

            spacing = 10.0
            padding = Insets(5.0, 5.0, 5.0, 5.0)
        }
    }

    init {
        cacheControlsEnabled.addListener { _, _, nv ->
            spriteEditorEnabled.value = nv
            cs2EditorEnabled.value = nv
        }
    }

    override fun onBeforeShow() {
        controller
    }

    fun newTab(editor: Editor) = Tab().apply {
        textProperty().bind(editor.nameProp)
        add(editor)
        editorTabPane.tabs += this
        closableProperty().addListener { _, _, _ ->
            editor.close()
        }
        select()
    }

    fun closeTabs() {
        editorTabPane.tabs.clear()
    }

    fun success(msg: String) {
        statusLabel.text = msg
        statusLabel.textFill = Color.GREEN
    }

    fun warn(msg: String) {
        statusLabel.text = msg
        statusLabel.textFill = Color.BLUE
    }

    fun error(msg: String) {
        statusLabel.text = msg
        statusLabel.textFill = Color.RED
    }
}
