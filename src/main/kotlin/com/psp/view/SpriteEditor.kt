package com.psp.view

import com.psp.controller.SpriteEditorController
import javafx.beans.property.SimpleBooleanProperty
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.ListView
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.MouseEvent
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Priority
import tornadofx.*
import javax.imageio.ImageIO

class SpriteEditor : Editor("Sprite Editor") {

    private val controller: SpriteEditorController by inject(params = params)

    val spritesList: ListView<Int>
    val searchField = TextField()
    val detailsArea = TextArea()
    val framesList = ListView<Int>()
    val spriteView = ImageView()

    val isSpriteSelected = SimpleBooleanProperty()
    val isFrameSelected = SimpleBooleanProperty()
    var selectedSpriteId: Int? = null
    var selectedFrameId: Int? = null

    override val root = BorderPane().apply {
        spritesList = listview {
            minHeight = 550.0
        }
        left = vbox {
            spritesList.apply {
                vgrow = Priority.ALWAYS
            }

            hbox {
                searchField.hgrow = Priority.ALWAYS
                add(searchField)
                imageview(SpriteEditor::class.java.getResource("/clear.png")?.toString()) {
                    addEventHandler(MouseEvent.MOUSE_CLICKED) {
                        controller.clearSearchField()
                        it.consume()
                    }
                }

                spacing = 5.0
            }
            add(spritesList)

            spacing = 10.0
        }

        center = vbox {
            hbox {
                vbox {
                    button("Add") {
                        prefWidth = 250.0
                        enableWhen(isSpriteSelected)
                        action {
                            controller.addFrame()
                        }
                    }
                    button("Remove") {
                        prefWidth = 250.0
                        enableWhen(isFrameSelected)
                        action {
                            controller.removeFrame()
                        }
                    }
                    button("Replace") {
                        prefWidth = 250.0
                        enableWhen(isFrameSelected)
                        action {
                            controller.replaceFrame()
                        }
                    }
                    button("Save") {
                        prefWidth = 250.0
                        enableWhen(isFrameSelected)
                        action {
                            controller.saveFrame()
                        }
                    }

                    spacing = 10.0
                }

                framesList.apply {
                    maxHeight = 140.0
                }
                add(framesList)

                spacing = 10.0
            }

            scrollpane {
                add(spriteView)

                maxHeight = 200.0
                vgrow = Priority.ALWAYS
            }

            detailsArea.isEditable = false
            detailsArea.isWrapText = false
            add(detailsArea)

            maxWidth = 400.0
            spacing = 10.0
            padding = Insets(10.0, 10.0, 10.0, 10.0)
            alignment = Pos.CENTER
        }

        padding = Insets(10.0, 10.0, 10.0, 10.0)
    }

    override fun onTabSelected() {
        controller
    }
}
