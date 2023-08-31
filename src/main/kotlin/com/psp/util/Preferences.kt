package com.psp.util

import tornadofx.toObservable

class Preferences {

    var recentsArr = arrayOf<String>()

    @Transient
    val recents = lazy { recentsArr.toMutableList().toObservable() }
    var lastOpenedDir: String? = null

    fun addRecent(dir: String) {
        if (!recents.value.contains(dir)) {
            recents.value += dir
        }
        lastOpenedDir = dir
    }

    companion object {

        var instance = Preferences()
            internal set
    }
}
