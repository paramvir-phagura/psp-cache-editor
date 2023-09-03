package com.psp.cache

import javafx.beans.property.SimpleBooleanProperty
import com.runescape.cache.Cache
import com.runescape.cache.FileStore
import java.io.File

object CacheManager {

    var cache: Cache? = null
    var cacheDir: String? = null
    val cacheInitProp = SimpleBooleanProperty(false)

    fun load(dir: File): Boolean {
        return try {
            cache = Cache(FileStore.open(dir))
            cacheDir = dir.path.toString()
            cacheInitProp.value = true
            true
        } catch (e: Exception) {
            false
        }
    }

    fun clear() {
        cache = null
        cacheDir = null
    }
}
