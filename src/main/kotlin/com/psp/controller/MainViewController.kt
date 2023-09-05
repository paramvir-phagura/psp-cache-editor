package com.psp.controller

import com.psp.cache.CacheManager
import com.psp.util.Preferences
import com.psp.view.Cs2Editor
import com.psp.view.MainView
import com.psp.view.SpriteEditor
import com.psp.view.Welcome
import javafx.application.Platform
import javafx.event.EventHandler
import javafx.stage.FileChooser
import tornadofx.Controller
import tornadofx.chooseDirectory
import tornadofx.error
import java.io.File

class MainViewController : Controller() {

    private val view: MainView by inject()

    init {
        view.cacheControlsEnabled.bind(CacheManager.cacheInitProp)

        newWelcomeTab()
    }

    fun locateCache(): Boolean {
        val fileChooser = FileChooser()
        fileChooser.title = "Select a directory"
        fileChooser.initialDirectory

        val cacheDir = chooseDirectory(
            "Select a directory",
            File(Preferences.instance.lastOpenedDir ?: System.getProperty("user.home")),
            owner = view.currentStage
        )
        if (cacheDir != null) {
            return openCache(cacheDir)
        }
        return false
    }

    fun openCache(cacheDir: File): Boolean {
        return if (CacheManager.cacheDir != null && CacheManager.cacheDir == cacheDir.path.toString()) {
            view.error("Cache already open!")
            false
        } else if (!CacheManager.load(cacheDir)) {
            error("Error loading cache.", owner = view.currentStage)
            view.error("Error loading cache.")
            false
        } else {
            Preferences.instance.addRecent(cacheDir.path.toString())
            view.success(
                """
                    Successfully loaded cache. Use the "Tools" drop-down menu to select a different editor.
                """.trimIndent()
            )
            true
        }
    }

    fun newWelcomeTab() {
        val tab = find<Welcome>("recents" to Preferences.instance.recents.value)
        view.newTab(tab)
    }

    fun newSpriteEditorTab() {
        if (view.spriteEditorEnabled.value) {
            view.newTab(find<SpriteEditor>("cache" to CacheManager.cache!!)).onClosed = EventHandler {
                view.spriteEditorEnabled.value = true
            }
            view.spriteEditorEnabled.value = false
        }
    }

    fun newCs2EditorTab() {
        if (view.cs2EditorEnabled.value) {
            view.newTab(find<Cs2Editor>("cacheDir" to CacheManager.cacheDir!!)).onClosed = EventHandler {
                view.cs2EditorEnabled.value = true
            }
            view.cs2EditorEnabled.value = false
        }
    }

    fun exit() {
        Platform.exit()
    }
}
