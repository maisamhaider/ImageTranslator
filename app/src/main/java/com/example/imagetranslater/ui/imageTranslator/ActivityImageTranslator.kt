package com.example.imagetranslater.ui.imageTranslator

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import com.example.imagetranslater.R
import com.example.imagetranslater.databinding.ActivityImageTranslatorBinding
import com.example.imagetranslater.ui.ActivityLanguages
import com.example.imagetranslater.utils.AnNot.ObjIntentKeys.LANGUAGE_CAMERA_SUPPORTED
import com.example.imagetranslater.utils.AnNot.ObjIntentKeys.LANGUAGE_ONLINE
import com.example.imagetranslater.utils.AnNot.ObjPreferencesKeys.SOURCE_LANGUAGE_SELECTED_CODE_IMAGE_TRANSLATOR
import com.example.imagetranslater.utils.AnNot.ObjPreferencesKeys.SOURCE_LANGUAGE_SELECTED_NAME_IMAGE_TRANSLATOR
import com.example.imagetranslater.utils.AnNot.ObjPreferencesKeys.SOURCE_RECENT_LANGUAGES_CODE_IMAGE_TRANSLATOR
import com.example.imagetranslater.utils.AnNot.ObjPreferencesKeys.SOURCE_RECENT_LANGUAGE_SELECTED_IMAGE_TRANSLATOR
import com.example.imagetranslater.utils.AnNot.ObjPreferencesKeys.TARGET_LANGUAGE_SELECTED_CODE_IMAGE_TRANSLATOR
import com.example.imagetranslater.utils.AnNot.ObjPreferencesKeys.TARGET_LANGUAGE_SELECTED_NAME_IMAGE_TRANSLATOR
import com.example.imagetranslater.utils.AnNot.ObjPreferencesKeys.TARGET_RECENT_LANGUAGES_CODE_IMAGE_TRANSLATOR
import com.example.imagetranslater.utils.AnNot.ObjPreferencesKeys.TARGET_RECENT_LANGUAGE_SELECTED_IMAGE_TRANSLATOR
import com.example.imagetranslater.utils.AnNot.imaeg.FROM_GALLERY
import com.example.imagetranslater.utils.AnNot.imaeg.uri
import com.example.imagetranslater.utils.AppPreferences.funGetString
import com.example.imagetranslater.utils.Singleton.funLaunchLanguagesActivity
import com.example.imagetranslater.utils.Singleton.toastLong
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class ActivityImageTranslator : AppCompatActivity(), LifecycleOwner {
    private var imageCapture: ImageCapture? = null
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService


    private lateinit var binding: ActivityImageTranslatorBinding

    companion object {
        private const val TAG = "CameraXBasic"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_image_translator)


        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }
        outputDirectory = getOutputDirectory()

        cameraExecutor = Executors.newSingleThreadExecutor()
        // Set up the listener for take photo button

        binding.textViewCapture.setOnClickListener {
            toastLong("Wait a moment")
            takePhoto()
        }

        binding.textViewOpenGallery.setOnClickListener {
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            intentLauncher.launch(photoPickerIntent)
        }
        binding.textViewSourceLang.setOnClickListener {
            funLaunchLanguagesActivity(
                LANGUAGE_CAMERA_SUPPORTED,
                SOURCE_RECENT_LANGUAGES_CODE_IMAGE_TRANSLATOR,
                 SOURCE_RECENT_LANGUAGE_SELECTED_IMAGE_TRANSLATOR,
                SOURCE_LANGUAGE_SELECTED_CODE_IMAGE_TRANSLATOR,
                SOURCE_LANGUAGE_SELECTED_NAME_IMAGE_TRANSLATOR,
                ActivityLanguages()
            )
        }
        binding.textViewTargetLang.setOnClickListener {
            funLaunchLanguagesActivity(
                LANGUAGE_ONLINE,
                TARGET_RECENT_LANGUAGES_CODE_IMAGE_TRANSLATOR,
                 TARGET_RECENT_LANGUAGE_SELECTED_IMAGE_TRANSLATOR,
                TARGET_LANGUAGE_SELECTED_CODE_IMAGE_TRANSLATOR,
                TARGET_LANGUAGE_SELECTED_NAME_IMAGE_TRANSLATOR,
                ActivityLanguages()
            )
        }
    }

    private val intentLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            if (result.resultCode == RESULT_OK && result.data!!.data != null) {
                val chosenImage = result.data!!.data
                uri = chosenImage!!
                FROM_GALLERY = true

//                methTakeResult(chosenImage!!)
                startActivity(
                    Intent(
                        this, ActivityImageTranslatorResult::class.java
                    )
                )
            } else {
            }

        }

    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case

        // Create time-stamped output file to hold the image
        val photoFile = File(
            outputDirectory, SimpleDateFormat(FILENAME_FORMAT, Locale.US)
                .format(System.currentTimeMillis()) + ".jpg"
        )

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // Set up image capture listener, which is triggered after photo has
        // been taken

        imageCapture!!.takePicture(outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    val msg = "Photo capture succeeded: $savedUri"
//                    methTakeResult(savedUri)
                    uri = savedUri
                    startActivity(
                        Intent(
                            this@ActivityImageTranslator,
                            ActivityImageTranslatorResult::class.java
                        )
                    )
                    Log.e(TAG, msg)
                }
            })
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        imageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
            // We request aspect ratio but no resolution to match preview config, but letting
            // CameraX optimize for whatever specific resolution best fits our use cases
            // Set initial target rotation, we will have to call this again if rotation changes
            // during the lifecycle of this use case
            .build()
        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()


                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    imageCapture,
                    preview
                )/*.cameraControl.setLinearZoom(0.4f)*/

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getOutputDirectory(): File {
        val mediaDir = getExternalFilesDirs(null).firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                finish()
            }
        }
    }



    override fun onResume() {
        super.onResume()
        binding.textViewSourceLang.text = funGetString(
            SOURCE_LANGUAGE_SELECTED_NAME_IMAGE_TRANSLATOR,
            "English"
        )
        binding.textViewTargetLang.text = funGetString(
            TARGET_LANGUAGE_SELECTED_NAME_IMAGE_TRANSLATOR,
            "English"
        )

    }


    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

}