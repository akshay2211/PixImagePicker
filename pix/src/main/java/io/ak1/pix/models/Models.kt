package io.ak1.pix.models

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel

import kotlinx.parcelize.Parcelize
import java.util.*

/**
 * Created By Akshay Sharma on 17,June,2021
 * https://ak1.io
 */
@SuppressLint("ParcelCreator")
@Parcelize
data class Img(
    var headerDate: String = "",
    var contentUrl: Uri = Uri.EMPTY,
    var scrollerDate: String = "",
    var mediaType: Int = 1
) : Parcelable {
    @IgnoredOnParcel
    var selected = false

    @IgnoredOnParcel
    var position = 0
}

@SuppressLint("ParcelCreator")
@Parcelize
class Options : Parcelable {
    var ratio = Ratio.RATIO_AUTO
    var count = 1
    var spanCount = 4
    var path = "Pix/Camera"
    var isFrontFacing = false
    var mode = Mode.All
    var flash = Flash.Auto
    var preSelectedUrls = ArrayList<Uri>()
    var videoOptions : VideoOptions = VideoOptions()
}

@Parcelize
enum class Mode : Parcelable {
    All, Picture, Video
}
@SuppressLint("ParcelCreator")
@Parcelize
class VideoOptions : Parcelable {
    var videoBitrate : Int? = null
    var audioBitrate : Int? = null
    var videoFrameRate : Int? = null
    var videoDurationLimitInSeconds = 10
}

@Parcelize
enum class Flash : Parcelable {
    Disabled, On, Off, Auto
}

@Parcelize
enum class Ratio : Parcelable {
    RATIO_4_3, RATIO_16_9, RATIO_AUTO
}

internal class ModelList(
    var list: ArrayList<Img> = ArrayList(),
    var selection: ArrayList<Img> = ArrayList()
)