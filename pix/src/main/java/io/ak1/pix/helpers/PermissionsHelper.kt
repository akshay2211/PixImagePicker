package io.ak1.pix.helpers

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import io.ak1.pix.models.Mode
import io.ak1.pix.models.Options

/**
 * Created By Akshay Sharma on 17,June,2021
 * https://ak1.io
 */

private val REQUIRED_PERMISSIONS_IMAGES =
    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
        arrayOf(
            Manifest.permission.CAMERA
        )
    } else {
        arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }
private val REQUIRED_PERMISSIONS_VIDEO = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
    arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO
    )
} else {
    arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.RECORD_AUDIO
    )
}

fun ActivityResultLauncher<Array<String>>.permissionsFilter(
    fragmentActivity: FragmentActivity,
    options: Options,
    callback: () -> Unit
) {
    if (fragmentActivity.allPermissionsGranted(options.mode)) {
        callback()
    } else {
        this.launch(if (options.mode == Mode.Picture) REQUIRED_PERMISSIONS_IMAGES else REQUIRED_PERMISSIONS_VIDEO)
    }
}

private fun Activity.allPermissionsGranted(mode: Mode) =
    (if (mode == Mode.Picture) REQUIRED_PERMISSIONS_IMAGES else REQUIRED_PERMISSIONS_VIDEO).all {

        val check = ContextCompat.checkSelfPermission(
            this, it
        ) == PackageManager.PERMISSION_GRANTED
        check
    }