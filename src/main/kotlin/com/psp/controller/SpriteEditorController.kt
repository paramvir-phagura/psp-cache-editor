package com.psp.controller

import com.psp.util.Preferences
import com.psp.view.MainView
import com.psp.view.SpriteEditor
import javafx.beans.binding.Bindings.select
import javafx.embed.swing.SwingFXUtils
import javafx.stage.FileChooser
import net.openrs.cache.Cache
import net.openrs.cache.Container
import net.openrs.cache.sprite.Sprite
import tornadofx.*
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.util.stream.Collectors
import javax.imageio.ImageIO

class SpriteEditorController : Controller() {

    private val mainView: MainView by inject()
    private val view: SpriteEditor by inject()

    private val cache: Cache by param()
    private val sprite
        get() = Sprite.get(cache, view.selectedSpriteId!!)
    private var frame
        get() = sprite.images[view.selectedFrameId!!]
        set(value) {
            sprite.images[view.selectedFrameId!!] = value
        }
    val emptySprite
        get() =
            try {
                SwingFXUtils.toFXImage(ImageIO.read(this::class.java.getResourceAsStream("/empty.png")), null)
            } catch(e: Exception) {
                e.printStackTrace()
                null
            }

    init {
        view.framesList.selectionModel.selectedItemProperty().addListener { _, _, nv ->
            if (nv == null) {
                return@addListener
            }

            val images = Sprite.get(cache, view.spritesList.selectedItem!!).images
            if (nv <= images.size) {
                val image = SwingFXUtils.toFXImage(images[nv], null)
                if (image != null) {
                    view.spriteView.image = image
                }
            } else {
                mainView.error("Error loading frame.")
                view.framesList.selectionModel.clearSelection()
            }
        }

        view.spritesList.selectionModel.selectedItemProperty().addListener { _, _, nv ->
            if (nv == null) {
                return@addListener
            }

            var frameIdx = 0
            view.framesList.items = Sprite.get(cache, nv).images.stream().map {
                frameIdx++
            }.collect(Collectors.toList()).asObservable()
            if (view.framesList.items.size > 0) {
                view.framesList.selectionModel.selectFirst()
            } else {
                view.spriteView.image = emptySprite
            }

//            view.detailsArea.text =
        }
        populateSpritesView()
    }

    private fun populateSpritesView() {
        for (i in 0..<cache.getFileCount(8)) {
            view.spritesList.items += i
        }
    }

    fun addFrame() {
        val addPath = chooseImage()

        if (addPath.isNotEmpty()) {
            val add = ImageIO.read(addPath.first())
            sprite.images.add(add)
            pack()
        }
    }

    fun removeFrame() {
        sprite.images.removeAt(view.selectedFrameId!!)
        pack()
    }

    fun replaceFrame() {
        val replacementPath = chooseImage()

        if (replacementPath.isNotEmpty()) {
            val replacement = ImageIO.read(replacementPath.first())
            frame = replacement
            pack()
        }
    }

    fun saveFrame() {
        val dir = Paths.get("${System.getProperty("user.home")}/psp-cache-editor/sprites")
        val path = dir.resolve(Paths.get("${sprite.id}-${view.selectedFrameId}.png"))
        if (!Files.exists(path)) {
            if (!Files.exists(dir)) {
                Files.createDirectory(dir)
            }
            val file = Files.createFile(path).toFile()
            ImageIO.write(frame, "PNG", file)
            mainView.success("Sprite frame saved to $path")
        } else {
            Files.delete(path)
            saveFrame()
        }
    }

    private fun pack() {
        try {
            cache.write(8, sprite.id, Container(sprite.container.type, sprite.encode(), sprite.container.version))
            mainView.success("Packed to cache!")
        } catch (e: Exception) {
            e.printStackTrace()
            error("Unable to pack sprite!", e.message, owner = view.currentStage, title = "Error!")
            mainView.error("Error packing to cache!")
        } finally {
            refresh()
        }
    }

    private fun chooseImage(): List<File> {
        val file = chooseFile(
            title = "Choose an Image",
            filters = arrayOf(FileChooser.ExtensionFilter(".png, .jpg", listOf("*.png", "*.jpg"))),
            initialDirectory = File(Preferences.instance.lastOpenedDir ?: System.getProperty("user.home")),
            owner = view.currentStage
        )
        if (file.isNotEmpty()) {
            Preferences.instance.lastOpenedDir = file.first().parent
        }
        return file
    }

    private fun refresh() {
        if (view.selectedSpriteId != null) {
            val selectedSpriteIdx = view.selectedSpriteId
            val selectedFrameIdx = view.selectedFrameId

            view.spritesList.selectionModel.select(null)
            view.spritesList.selectionModel.select(selectedSpriteIdx)
            if (view.selectedFrameId != null) {
                view.framesList.selectionModel.select(selectedFrameIdx)
            }
        }
    }
}
