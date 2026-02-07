/*
 * Copyright (C) 2026 Akshay Sharma
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.ak1.pix.helpers

import android.content.res.Resources
import io.ak1.pix.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

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
