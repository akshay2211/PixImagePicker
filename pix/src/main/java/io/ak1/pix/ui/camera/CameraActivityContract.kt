package io.ak1.pix.ui.camera

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract
import io.ak1.pix.models.Options
import io.ak1.pix.ui.PixActivity

/**
 * tracksynq-android
 *
 * Created by iad.guno on 06 December, 2021
 * Copyright © 2020 Quantum Inventions. All rights reserved.
 */

/**
 * Camera Activity Result Contract
 *
 * From https://proandroiddev.com/is-onactivityresult-deprecated-in-activity-results-api-lets-deep-dive-into-it-302d5cf6edd
 */
class CameraActivityContract (private val options: Options? = null) :
    ActivityResultContract<String, CameraActivityContract.CameraActivityResult?>() {

    data class CameraActivityResult(val id: String, val imageUriList: List<Uri>?)

    override fun createIntent(context: Context, input: String): Intent {
        return Intent(context, PixActivity::class.java).apply {
            putExtra(PixActivity.ID, input)
            putExtra(PixActivity.OPTIONS, options)
        }
    }

    override fun parseResult(
        resultCode: Int,
        intent: Intent?
    ): CameraActivityResult? {
        val id = intent?.getStringExtra(PixActivity.ID)
        val data = intent?.getStringArrayListExtra(PixActivity.IMAGE_URI_LIST)
        return if (resultCode == Activity.RESULT_OK && !id.isNullOrBlank() && data != null) {
            CameraActivityResult(id, data.toList().map {
                Uri.parse(it)
            })
        } else {
            null
        }
    }
}