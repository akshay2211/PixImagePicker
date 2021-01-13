package com.fxn.utility

/**
 * Created by sangcomz on 09/04/2017.
 */
class RegexUtil {
    fun checkGif(path: String): Boolean {
        return path.matches(GIF_PATTERN.toRegex())
    }

    companion object {
        private const val GIF_PATTERN = "(.+?)\\.gif$"
    }
}