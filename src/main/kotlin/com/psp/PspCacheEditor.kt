package com.psp

import com.psp.view.MainView
import com.psp.commons.serialize.JsonSerializer
import com.psp.util.Preferences
import javafx.scene.image.Image
import javafx.stage.Stage
import tornadofx.App
import tornadofx.importStylesheet

class PspCacheEditor : App(MainView::class) {

    private val configSerializer = JsonSerializer("${System.getProperty("user.dir")}/preferences.json")

    init {
        try {
            Preferences.instance = configSerializer.deserialize(Preferences::class.java)!!
            importStylesheet("/themes/dracula.css")
            importStylesheet("/themes/buttons.css")
        } catch (_: Exception) {
        }
    }

    override fun start(stage: Stage) {
        stage.icons.add(Image(this::class.java.getResource("/images/favicon.png")!!.toString()))
        super.start(stage)
    }

    override fun stop() {
        Preferences.instance.recentsArr = Preferences.instance.recents.value.toTypedArray()
        configSerializer.serialize(Preferences.instance)
    }
}
