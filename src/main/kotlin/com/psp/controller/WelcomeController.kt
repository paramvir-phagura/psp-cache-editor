package com.psp.controller

import com.psp.util.Preferences
import com.psp.view.Welcome
import tornadofx.Controller

class WelcomeController : Controller() {

    private val view: Welcome by inject()

    fun clearRecents() {
        Preferences.instance.recents.value.clear()
    }
}
