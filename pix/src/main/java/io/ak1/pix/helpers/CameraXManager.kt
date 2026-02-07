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

import android.annotation.SuppressLint
import android.content.ContentValues
import android.net.Uri
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.UseCase
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.FallbackStrategy
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import io.ak1.pix.models.Flash
import io.ak1.pix.models.Mode
import io.ak1.pix.models.Options
import io.ak1.pix.models.Ratio
import io.ak1.pix.utility.PixBindings
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * Created By Akshay Sharma on 17,June,2021
 * https://ak1.io
 */

class CameraXManager(
    private val previewView: PreviewView,
    private val requireActivity: FragmentActivity,
    private val options: Options,
) {
    var recording: Recording? = null
    private val executor = ContextCompat.getMainExecutor(requireActivity)
    private var lensFacing: Int = CameraSelector.LENS_FACING_BACK
    var imageCapture: ImageCapture? = null
    var videoCapture: VideoCapture<Recorder>? = null
    private var useCases = ArrayList<UseCase>()
    private var preview: Preview? = null
    private var cameraProvider: ProcessCameraProvider? = null

    // Select back camera as a default
    private var cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    /** Initialize CameraX, and prepare to bind the camera use cases  */
    fun setUpCamera(binding: PixBindings) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireActivity)
        cameraProviderFuture.addListener({
            // CameraProvider
            cameraProvider = cameraProviderFuture.get()
            // Build and bind the camera use cases
            bindCameraUseCases(binding)
        }, ContextCompat.getMainExecutor(requireActivity))
    }

    /** Declare and bind preview, capture and analysis use cases */
    fun bindCameraUseCases(binding: PixBindings) {
        // Check if view is correctly attached to window, stop binding otherwise
        val display = previewView.display ?: return

        // Get screen metrics used to setup camera for full screen resolution
        val metrics = DisplayMetrics().also { display.getRealMetrics(it) }
        Log.d(TAG, "Screen metrics: ${metrics.widthPixels} x ${metrics.heightPixels}")

        val screenAspectRatio = when (options.ratio) {
            Ratio.RATIO_AUTO -> aspectRatio(metrics.widthPixels, metrics.heightPixels)
            Ratio.RATIO_4_3 -> AspectRatio.RATIO_4_3
            Ratio.RATIO_16_9 -> AspectRatio.RATIO_16_9
        }
        Log.d(TAG, "Preview aspect ratio: $screenAspectRatio")

        val rotation = previewView.display.rotation

        // CameraProvider
        val cameraProvider = cameraProvider
            ?: throw IllegalStateException("Camera initialization failed.")
        useCases.clear()

        // Select lensFacing depending on the available cameras
        lensFacing = when {
            hasBackCamera() and !options.isFrontFacing -> CameraSelector.LENS_FACING_BACK
            hasFrontCamera() and options.isFrontFacing -> CameraSelector.LENS_FACING_FRONT
            else -> throw IllegalStateException("Back and front camera are unavailable")
        }
        // CameraSelector
        cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()

        // Preview
        preview = Preview.Builder()
            // We request aspect ratio but no resolution
            .setTargetAspectRatio(screenAspectRatio)
            // Set initial target rotation
            .setTargetRotation(rotation)
            .build()
        useCases.add(preview!!)
        // ImageCapture
        when (options.mode) {
            Mode.Picture -> {
                imageCapture = ImageCapture.Builder().apply {
                    setFlashMode(
                        when (options.flash) {
                            Flash.Auto -> ImageCapture.FLASH_MODE_AUTO
                            Flash.Off -> ImageCapture.FLASH_MODE_OFF
                            Flash.On -> ImageCapture.FLASH_MODE_ON
                            else -> ImageCapture.FLASH_MODE_AUTO
                        }
                    )
                }
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                    // We request aspect ratio but no resolution to match preview config, but letting
                    // CameraX optimize for whatever specific resolution best fits our use cases
                    .setTargetAspectRatio(screenAspectRatio)
                    // Set initial target rotation, we will have to call this again if rotation changes
                    // during the lifecycle of this use case
                    .setTargetRotation(rotation)
                    .build()
                useCases.add(imageCapture!!)
            }

            Mode.Video -> {
                videoCapture = createVideoCaptureUseCase(
                    screenAspectRatio,
                    options.videoOptions.videoBitrate,
                    options.videoOptions.audioBitrate,
                    options.videoOptions.videoFrameRate
                )
                useCases.add(videoCapture!!)
            }

            else -> {
                imageCapture = ImageCapture.Builder().apply {
                    setFlashMode(
                        when (options.flash) {
                            Flash.Auto -> ImageCapture.FLASH_MODE_AUTO
                            Flash.Off -> ImageCapture.FLASH_MODE_OFF
                            Flash.On -> ImageCapture.FLASH_MODE_ON
                            else -> ImageCapture.FLASH_MODE_AUTO
                        }
                    )
                }
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                    // We request aspect ratio but no resolution to match preview config, but letting
                    // CameraX optimize for whatever specific resolution best fits our use cases
                    .setTargetAspectRatio(screenAspectRatio)
                    // Set initial target rotation, we will have to call this again if rotation changes
                    // during the lifecycle of this use case
                    .setTargetRotation(rotation)
                    .build()
                useCases.add(imageCapture!!)
                videoCapture = createVideoCaptureUseCase(
                    screenAspectRatio,
                    options.videoOptions.videoBitrate,
                    options.videoOptions.audioBitrate,
                    options.videoOptions.videoFrameRate
                )
                useCases.add(videoCapture!!)
            }
        }

        // Must unbind the use-cases before rebinding them
        cameraProvider.unbindAll()

        try {
            // A variable number of use-cases can be passed here -
            // camera provides access to CameraControl & CameraInfo
            val camera = cameraProvider.bindToLifecycle(
                requireActivity,
                cameraSelector,
                *useCases.toTypedArray()
            )
            if (camera.cameraInfo.hasFlashUnit()) {
                binding.controlsLayout.flashButton.show()
                binding.setDrawableIconForFlash(options)
            }
            if (options.flash == Flash.Disabled) {
                binding.controlsLayout.flashButton.hide()
            }
            // Attach the viewfinder's surface provider to preview use case
            preview?.surfaceProvider = previewView.surfaceProvider
        } catch (exc: Exception) {
            Log.e(TAG, "Use case binding failed", exc)
        }
    }

    @SuppressLint("RestrictedApi")
    private fun createVideoCaptureUseCase(
        screenAspectRatio: Int,
        videoBitrate: Int?,
        audioBitrate: Int?,
        videoFrameRate: Int?
    ): VideoCapture<Recorder> {
        val qualitySelector = QualitySelector.fromOrderedList(
            listOf(Quality.UHD, Quality.FHD, Quality.HD, Quality.SD),
            FallbackStrategy.lowerQualityOrHigherThan(Quality.SD)
        )

        val recorder = Recorder.Builder()
            .setExecutor(executor)
            .setQualitySelector(qualitySelector)
            .setAspectRatio(screenAspectRatio).apply {
                videoBitrate?.let { this.setTargetVideoEncodingBitRate(it) }
            }
            .build()

        val videoCapture = VideoCapture.withOutput(recorder)
        return videoCapture
    }

    fun takePhoto(callback: (Uri, String?) -> Unit) {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create time-stamped output file to hold the image
        val photoFile = File(
            getOutputDirectory(),
            SimpleDateFormat(
                FILENAME_FORMAT,
                Locale.US
            ).format(System.currentTimeMillis()) + ".jpg"
        )

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireActivity),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                    callback(Uri.EMPTY, exc.message)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    val msg = "Photo capture succeeded: $savedUri"
                    // Toast.makeText(requireActivity, msg, Toast.LENGTH_SHORT).show()
                    Log.d("TAG", msg)
                    requireActivity.scanPhoto(photoFile) {
                        output.savedUri?.let {
                            callback(it, null)
                        }
                    }
                }
            }
        )
    }

    @SuppressLint("RestrictedApi", "MissingPermission")
    fun takeVideo(callback: (Uri, String?) -> Unit) {
        // Create MediaStoreOutputOptions for our recorder
        val name = "Pix-recording-" +
            SimpleDateFormat(FILENAME_FORMAT, Locale.US)
                .format(System.currentTimeMillis()) + ".mp4"
        val contentValues = ContentValues().apply {
            put(MediaStore.Video.Media.DISPLAY_NAME, name)
        }
        val mediaStoreOutput = MediaStoreOutputOptions.Builder(
            requireActivity.contentResolver,
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        )
            .setContentValues(contentValues)
            .build()

        if (videoCapture == null) return
        recording = null
        recording = videoCapture!!.output
            .prepareRecording(requireActivity, mediaStoreOutput)
            .withAudioEnabled()
            .start(executor) { vre ->
                when (vre) {
                    is VideoRecordEvent.Start -> {
                        Log.d(TAG, "Recording started")
                    }

                    is VideoRecordEvent.Pause -> {
                        Log.d(TAG, "Recording stopped")
                    }

                    is VideoRecordEvent.Resume -> {
                    }

                    is VideoRecordEvent.Finalize -> {
                        callback(vre.outputResults.outputUri, null)
                    }
                }
            }
    }

    private fun getOutputDirectory(): File {
        val mediaDir = requireActivity.externalMediaDirs.firstOrNull()?.let {
            File(it, options.path).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists()) {
            mediaDir
        } else {
            requireActivity.filesDir
        }
    }

    /** Returns true if the device has an available back camera. False otherwise */
    private fun hasBackCamera(): Boolean = cameraProvider?.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA) ?: false

    /** Returns true if the device has an available front camera. False otherwise */
    private fun hasFrontCamera(): Boolean = cameraProvider?.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA) ?: false

    private fun aspectRatio(width: Int, height: Int): Int {
        val previewRatio = max(width, height).toDouble() / min(width, height)
        if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)) {
            return AspectRatio.RATIO_4_3
        }
        return AspectRatio.RATIO_16_9
    }

    companion object {
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val TAG = "CameraXBasic"
        private const val RATIO_4_3_VALUE = 4.0 / 3.0
        private const val RATIO_16_9_VALUE = 16.0 / 9.0
    }
}
