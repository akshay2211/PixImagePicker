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
package io.ak1.pix.utility

import android.provider.MediaStore

/**
 * Created By Akshay Sharma on 17,June,2021
 * https://ak1.io
 */
internal const val TAG = "Pix logs"
internal const val S_SCROLLBAR_ANIM_DURATION = 500
internal val IMAGE_VIDEO_PROJECTION = arrayOf(
    MediaStore.Files.FileColumns._ID,
    MediaStore.Files.FileColumns.PARENT,
    MediaStore.Files.FileColumns.DISPLAY_NAME,
    MediaStore.Files.FileColumns.DATE_ADDED,
    MediaStore.Files.FileColumns.DATE_MODIFIED,
    MediaStore.Files.FileColumns.MEDIA_TYPE,
    MediaStore.Files.FileColumns.MIME_TYPE,
    MediaStore.Files.FileColumns.TITLE
)
internal const val IMAGE_VIDEO_SELECTION = (
    MediaStore.Files.FileColumns.MEDIA_TYPE + "=" +
        MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE +
        " OR " +
        MediaStore.Files.FileColumns.MEDIA_TYPE + "=" +
        MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO
    )
internal const val VIDEO_SELECTION = (
    MediaStore.Files.FileColumns.MEDIA_TYPE + "=" +
        MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO
    )
internal const val IMAGE_SELECTION = (
    MediaStore.Files.FileColumns.MEDIA_TYPE + "=" +
        MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
    )
internal val IMAGE_VIDEO_URI = MediaStore.Files.getContentUri("external")!!
internal const val IMAGE_VIDEO_ORDER_BY = MediaStore.Images.Media.DATE_MODIFIED + " DESC"
var width = 0
const val ARG_PARAM_PIX = "param_pix"
internal const val ARG_PARAM_PIX_KEY = "param_pix_key"
