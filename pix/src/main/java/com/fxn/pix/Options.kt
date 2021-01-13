package com.fxn.pix

import java.io.Serializable
import java.util.*

class Options private constructor() : Serializable {
    var count = 1
    var requestCodeHere = 0
    var spanCount = 4
    var path = "Pix/Camera"
    val height = 0
    val width = 0
    var isFrontfacing = false
    var videoDurationLimitinSeconds = 40
    var isExcludeVideos = false
    var preSelectedUrls = ArrayList<String>()

    @ScreenOrientation
    var screenOrientation = SCREEN_ORIENTATION_UNSPECIFIED

    fun setVideoDurationLimitinSeconds(videoDurationLimitinSececonds: Int): Options {
        videoDurationLimitinSeconds = videoDurationLimitinSececonds
        return this
    }

    fun setPreSelectedUrls(preSelectedUrls: ArrayList<String>): Options {
        this.preSelectedUrls = preSelectedUrls
        return this
    }

    fun setExcludeVideos(excludeVideos: Boolean): Options {
        isExcludeVideos = excludeVideos
        return this
    }

    fun setFrontfacing(frontfacing: Boolean): Options {
        isFrontfacing = frontfacing
        return this
    }

    fun setCount(count: Int): Options {
        this.count = count
        return this
    }

    fun getRequestCode(): Int {
        if (requestCodeHere == 0) {
            throw NullPointerException("requestCode in Options class is null")
        }
        return requestCodeHere
    }

    fun setRequestCode(requestcode: Int): Options {
        requestCodeHere = requestcode
        return this
    }

    fun setPath(path: String): Options {
        this.path = path
        return this
    }

    fun setScreenOrientation(@ScreenOrientation screenOrientation: Int): Options {
        this.screenOrientation = screenOrientation
        return this
    }

    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    annotation class ScreenOrientation

    fun setSpanCount(spanCount: Int): Options {
        this.spanCount = spanCount
        require(!(spanCount < 1 && spanCount > 5)) { "span count can not be set below 0 or more than 5" }
        return this
    }

    companion object {
        const val SCREEN_ORIENTATION_UNSET = -2
        const val SCREEN_ORIENTATION_UNSPECIFIED = -1
        const val SCREEN_ORIENTATION_LANDSCAPE = 0
        const val SCREEN_ORIENTATION_PORTRAIT = 1
        const val SCREEN_ORIENTATION_USER = 2
        const val SCREEN_ORIENTATION_BEHIND = 3
        const val SCREEN_ORIENTATION_SENSOR = 4
        const val SCREEN_ORIENTATION_NOSENSOR = 5
        const val SCREEN_ORIENTATION_SENSOR_LANDSCAPE = 6
        const val SCREEN_ORIENTATION_SENSOR_PORTRAIT = 7
        const val SCREEN_ORIENTATION_REVERSE_LANDSCAPE = 8
        const val SCREEN_ORIENTATION_REVERSE_PORTRAIT = 9
        const val SCREEN_ORIENTATION_FULL_SENSOR = 10
        const val SCREEN_ORIENTATION_USER_LANDSCAPE = 11
        const val SCREEN_ORIENTATION_USER_PORTRAIT = 12
        const val SCREEN_ORIENTATION_FULL_USER = 13
        const val SCREEN_ORIENTATION_LOCKED = 14
        fun init(): Options {
            return Options()
        }
    }
}