package com.psp.view

import com.psp.controller.MainViewController
import com.psp.controller.WelcomeController
import com.psp.util.Constants
import javafx.collections.ObservableList
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.layout.VBox
import tornadofx.*
import java.io.File

class Welcome : Editor("Welcome!") {

    private val mainController: MainViewController by inject()
    private val controller: WelcomeController by inject()
    private val recents: ObservableList<String> by param()

    override val root = VBox().apply {
        label(
            """
            To get started, press the "Locate Cache" button below to point to the directory where your cache resides.
        """.trimIndent()
        ) {
            isWrapText = true
            maxWidth = 400.0
            font = Constants.font
        }

        button("Locate Cache").action {
            if (mainController.locateCache()) {
                mainController.newSpriteEditorTab()
            }
        }

        titledpane("Recently opened") {
            listview(recents) {
                onMouseClicked = EventHandler {
                    if (it.clickCount == 2 && selectedItem != null) {
                        if (mainController.openCache(File(selectedItem!!))) {
                            mainController.newSpriteEditorTab()
                        }
                    }
                }
            }
            maxWidth = 400.0
            maxHeight = 200.0
        }

        hbox {
            button("Clear") {
                action {
                    controller.clearRecents()
                }
            }
            maxWidth = 400.0
            alignment = Pos.CENTER_RIGHT
        }

        minWidth = 800.0
        minHeight = 600.0
        alignment = Pos.CENTER
        spacing = 10.0
    }
}
