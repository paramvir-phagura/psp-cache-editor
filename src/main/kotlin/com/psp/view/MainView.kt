package com.psp.view

import com.psp.controller.MainViewController
import com.psp.util.Constants
import javafx.beans.property.SimpleBooleanProperty
import javafx.geometry.Insets
import javafx.scene.control.Label
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.paint.Color
import javafx.scene.text.Font
import tornadofx.*

class MainView : View("psp-cache-editor") {

    private val controller: MainViewController by inject()

    val editorTabPane = TabPane()
    val statusLabel = Label()

    val cacheControlsEnabled = SimpleBooleanProperty(false)
    val spriteEditorEnabled = SimpleBooleanProperty(false)

    override val root = borderpane {
        top = vbox {
                menubar {
                    menu("File") {
                        item("Open").action {
                            controller.locateCache()
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
                        item("Model Editor") {
                            isDisable = true
                        }
                        item("Music Editor") {
                            isDisable = true
                        }
                    }
                }
            }

        center = editorTabPane

        bottom = vbox {
            // TODO Action button (e.g., open directory containing sprites for convenience)
            add(statusLabel)

            spacing = 10.0
            padding = Insets(5.0, 5.0, 5.0, 5.0)
        }
    }

    init {
        cacheControlsEnabled.addListener { _, _, nv ->
            spriteEditorEnabled.value = nv
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

    fun success(msg: String) {
        statusLabel.text = msg
        statusLabel.textFill = Color.GREEN
        statusLabel.font = Constants.boldFont
    }

    fun warn(msg: String) {
        statusLabel.text = msg
        statusLabel.textFill = Color.BLUE
        statusLabel.font = Constants.boldFont
    }

    fun error(msg: String) {
        statusLabel.text = msg
        statusLabel.textFill = Color.RED
    }
}
