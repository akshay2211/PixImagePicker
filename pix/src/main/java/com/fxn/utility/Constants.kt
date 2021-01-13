package com.fxn.utility

import android.provider.MediaStore

/**
 * Created by akshay on 06/04/18.
 */
object Constants {
    const val sScrollbarAnimDuration = 500
    var PROJECTION = arrayOf(
            MediaStore.Images.Media._ID,
            //MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            //MediaStore.Images.Media.BUCKET_ID,
            // MediaStore.Images.Media.DATE_TAKEN,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media.DATE_MODIFIED
    )
    var URI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    var ORDERBY = MediaStore.Images.Media.DATE_MODIFIED + " DESC"
    var IMAGE_VIDEO_PROJECTION = arrayOf(
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.PARENT,
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            MediaStore.Files.FileColumns.DATE_ADDED,
            MediaStore.Files.FileColumns.DATE_MODIFIED,
            MediaStore.Files.FileColumns.MEDIA_TYPE,
            MediaStore.Files.FileColumns.MIME_TYPE,
            MediaStore.Files.FileColumns.TITLE
    )
    var IMAGE_VIDEO_SELECTION = (MediaStore.Files.FileColumns.MEDIA_TYPE + "="
            + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
            + " OR "
            + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
            + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO)
    var IMAGE_SELECTION = (MediaStore.Files.FileColumns.MEDIA_TYPE + "="
            + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE)
    var IMAGE_VIDEO_URI = MediaStore.Files.getContentUri("external")
    var IMAGE_VIDEO_ORDERBY = MediaStore.Images.Media.DATE_MODIFIED + " DESC"
}