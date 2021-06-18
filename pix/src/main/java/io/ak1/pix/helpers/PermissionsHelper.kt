package io.ak1.pix.helpers

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import io.ak1.pix.models.Mode
import io.ak1.pix.models.Options

/**
 * Created By Akshay Sharma on 17,June,2021
 * https://ak1.io
 */

// TODO: 18/06/21 remove WRITE_EXTERNAL_STORAGE check for api > 30
private val REQUIRED_PERMISSIONS_IMAGES =
    arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
private val REQUIRED_PERMISSIONS_VIDEO =
    arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.RECORD_AUDIO
    )

fun ActivityResultLauncher<Array<String>>.permissionsFilter(
    fragmentActivity: FragmentActivity,
    options: Options,
    callback: () -> Unit
) {
    if (fragmentActivity.allPermissionsGranted(options.mode)) {
        callback()
    } else {
        Log.e("are we ", "launching permissions again??  ${options.mode.name}")
        this.launch(if (options.mode == Mode.Picture) REQUIRED_PERMISSIONS_IMAGES else REQUIRED_PERMISSIONS_VIDEO)
    }
}

private fun Activity.allPermissionsGranted(mode: Mode) =
    (if (mode == Mode.Picture) REQUIRED_PERMISSIONS_IMAGES else REQUIRED_PERMISSIONS_VIDEO).all {

        val check = ContextCompat.checkSelfPermission(
            this, it
        ) == PackageManager.PERMISSION_GRANTED
        Log.e("Permissions", "->  $it isGranted $check")
        check
    }