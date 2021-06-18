package io.ak1.pix.models

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import java.util.*

/**
 * Created By Akshay Sharma on 17,June,2021
 * https://ak1.io
 */

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

@Parcelize
class Options : Parcelable {
    var ratio = Ratio.RATIO_AUTO
    var count = 1
    var spanCount = 4
    var path = "Pix/Camera"
    var isFrontFacing = false
    var videoDurationLimitInSeconds = 10
    var mode = Mode.All
    var flash = Flash.Auto
    var preSelectedUrls = ArrayList<Uri>()
}

enum class Mode {
    All, Picture, Video
}

enum class Flash {
    Disabled, On, Off, Auto
}

enum class Ratio {
    RATIO_4_3, RATIO_16_9, RATIO_AUTO
}

internal class ModelList(
    var list: ArrayList<Img> = ArrayList(),
    var selection: ArrayList<Img> = ArrayList()
)