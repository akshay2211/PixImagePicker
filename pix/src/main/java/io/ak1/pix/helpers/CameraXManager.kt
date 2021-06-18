package io.ak1.pix.helpers

import android.annotation.SuppressLint
import android.net.Uri
import android.util.DisplayMetrics
import android.util.Log
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import io.ak1.pix.databinding.FragmentPixCameraBinding
import io.ak1.pix.models.Flash
import io.ak1.pix.models.Mode
import io.ak1.pix.models.Options
import io.ak1.pix.models.Ratio
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
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
    private val executor = ContextCompat.getMainExecutor(requireActivity)
    private var lensFacing: Int = CameraSelector.LENS_FACING_BACK
    var imageCapture: ImageCapture? = null
    var videoCapture: VideoCapture? = null
    private var useCases = ArrayList<UseCase>()
    private var preview: Preview? = null
    private var cameraProvider: ProcessCameraProvider? = null

    // Select back camera as a default
    private var cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA


    /** Initialize CameraX, and prepare to bind the camera use cases  */
    fun setUpCamera(binding: FragmentPixCameraBinding) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireActivity)
        cameraProviderFuture.addListener({
            // CameraProvider
            cameraProvider = cameraProviderFuture.get()
            // Build and bind the camera use cases
            bindCameraUseCases(binding)
        }, ContextCompat.getMainExecutor(requireActivity))
    }

    /** Declare and bind preview, capture and analysis use cases */
    fun bindCameraUseCases(binding: FragmentPixCameraBinding) {
        // Get screen metrics used to setup camera for full screen resolution
        val metrics = DisplayMetrics().also { previewView.display.getRealMetrics(it) }
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
        Log.e("options", "-> ${options.mode.name}")
        when (options.mode) {
            Mode.Picture -> {
                imageCapture = ImageCapture.Builder().apply {
                    Log.e("flash mode", "->   ${options.flash.name}")
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
                videoCapture = createVideoCaptureUseCase(screenAspectRatio)
                useCases.add(videoCapture!!)
            }
            else -> {
                Log.e("options", "-> imageCapture")
                imageCapture = ImageCapture.Builder().apply {
                    Log.e("flash mode", "->   ${options.flash.name}")
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
                videoCapture = createVideoCaptureUseCase(screenAspectRatio)
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
            if (camera.cameraInfo.hasFlashUnit() && options.flash != Flash.Disabled) {
                binding.gridLayout.controlsLayout.flashButton.show()
                binding.setDrawableIconForFlash(options)
            }
            // Attach the viewfinder's surface provider to preview use case
            preview?.setSurfaceProvider(previewView.surfaceProvider)
        } catch (exc: Exception) {
            Log.e(TAG, "Use case binding failed", exc)
        }
    }


    @SuppressLint("RestrictedApi")
    private fun createVideoCaptureUseCase(screenAspectRatio: Int): VideoCapture {
        val builder = VideoCapture.Builder().apply {
            //setTargetRotation(previewView.display.rotation)
            // setAudioRecordSource()
            //setAudioSource(MediaRecorder.AudioSource
        }.setTargetAspectRatio(screenAspectRatio)
        return builder.build()
    }


    /*private fun createImageAnalyserUseCase(): ImageAnalysis {
        val imageAnalysis = ImageAnalysis.Builder()
            .setTargetResolution(Size(1280, 720))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        imageAnalysis.setAnalyzer(executor, {
            //image as a param
            //val rotationDegrees = image.imageInfo.rotationDegrees
            // insert your code here.
        })
        return imageAnalysis
    }

    private fun createPreviewUseCase(): Preview {
        return Preview.Builder().apply {
        }
            .build()
            .also {
                it.setSurfaceProvider(previewView.surfaceProvider)

            }
    }

    private fun createImageCaptureUseCase(options: Options): ImageCapture {
        //reference
        //https://developer.android.com/reference/androidx/camera/extensions/AutoImageCaptureExtender

        val builder = ImageCapture.Builder().apply {
            // setTargetRotation(previewView.display.rotation)
            Log.e("flash mode", "->   ${options.flash.name}")
            setFlashMode(
                when (options.flash) {
                    Flash.Auto -> ImageCapture.FLASH_MODE_AUTO
                    Flash.Off -> ImageCapture.FLASH_MODE_OFF
                    Flash.On -> ImageCapture.FLASH_MODE_ON
                    else -> ImageCapture.FLASH_MODE_AUTO
                }
            )

        }
        /**
         *
        // Create an Extender object which can be used to apply extension
        // configurations.
        val bokehImageCapture = BokehImageCaptureExtender.create(builder)
        if (bokehImageCapture.isExtensionAvailable(cameraSelector)) {
        // Enable the extension if available.
        bokehImageCapture.enableExtension(cameraSelector)
        }
        val hdrImageCaptureExtender = HdrImageCaptureExtender.create(builder)
        if (hdrImageCaptureExtender.isExtensionAvailable(cameraSelector)) {
        // Enable the extension if available.
        hdrImageCaptureExtender.enableExtension(cameraSelector)
        }
        val nightImageCaptureExtender = NightImageCaptureExtender.create(builder)
        if (nightImageCaptureExtender.isExtensionAvailable(cameraSelector)) {
        // Enable the extension if available.
        nightImageCaptureExtender.enableExtension(cameraSelector)
        }

         */

        return builder
            // .setTargetRotation(view.display.rotation)
            .build()

    }*/

    fun takePhoto(callback: (Uri, String?) -> Unit) {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create time-stamped output file to hold the image
        val photoFile = File(
            getOutputDirectory(),
            SimpleDateFormat(
                FILENAME_FORMAT, Locale.US
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
                    Log.e("TAG", "Photo capture failed: ${exc.message}", exc)
                    callback(Uri.EMPTY, exc.message)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    val msg = "Photo capture succeeded: $savedUri"
                    Toast.makeText(requireActivity, msg, Toast.LENGTH_SHORT).show()
                    Log.d("TAG", msg)
                    callback(savedUri, null)
                }
            })
    }

    @SuppressLint("RestrictedApi", "MissingPermission")
    fun takeVideo(callback: (Uri, String?) -> Unit) {
        val videoFile = File(
            getOutputDirectory(),
            SimpleDateFormat(
                FILENAME_FORMAT, Locale.US
            ).format(System.currentTimeMillis()) + ".mp4"
        )
        videoCapture?.startRecording(
            VideoCapture.OutputFileOptions.Builder(videoFile).build(),
            executor,
            object : VideoCapture.OnVideoSavedCallback {
                override fun onVideoSaved(outputFileResults: VideoCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(videoFile)
                    val msg = "Photo capture succeeded: $savedUri"
                    Log.e("TAG", msg)
                    Log.e("savedUri", "${outputFileResults.savedUri}")
                    outputFileResults.savedUri
                    callback(outputFileResults.savedUri ?: Uri.EMPTY, null)
                }

                override fun onError(videoCaptureError: Int, message: String, cause: Throwable?) {
                    Log.e("TAG", "video Capture failed: $message", cause)
                    callback(Uri.EMPTY, message)
                }
            })
        //videoCapture?.stopRecording()
    }

    private fun getOutputDirectory(): File {
        val mediaDir = requireActivity.externalMediaDirs.firstOrNull()?.let {
            File(it, options.path).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else requireActivity.filesDir
    }

    /** Returns true if the device has an available back camera. False otherwise */
    private fun hasBackCamera(): Boolean {
        return cameraProvider?.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA) ?: false
    }

    /** Returns true if the device has an available front camera. False otherwise */
    private fun hasFrontCamera(): Boolean {
        return cameraProvider?.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA) ?: false
    }

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