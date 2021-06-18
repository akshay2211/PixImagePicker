package io.ak1.pix.helpers

import android.content.res.Resources
import io.ak1.pix.R
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created By Akshay Sharma on 17,June,2021
 * https://ak1.io
 */

fun Resources.getDateDifference(calendar: Calendar): String {
    val d = calendar.time
    val lastMonth =
        Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -Calendar.DAY_OF_MONTH) }
    val lastWeek = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -7) }
    val recent = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -2) }
    return when {
        calendar.before(lastMonth) -> SimpleDateFormat("MMMM", Locale.getDefault()).format(d)
        calendar.after(lastMonth) && calendar.before(lastWeek) -> getString(R.string.pix_last_month)
        calendar.after(lastWeek) && calendar.before(recent) -> getString(R.string.pix_last_week)
        else -> getString(R.string.pix_recent)
    }
}