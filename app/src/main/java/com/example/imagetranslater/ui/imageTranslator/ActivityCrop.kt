package com.example.imagetranslater.ui.imageTranslator

import android.content.Intent
import android.graphics.*
import android.os.Bundle
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.example.imagetranslater.R
import com.example.imagetranslater.utils.AnNot
import com.example.imagetranslater.utils.AnNot.imaeg.vision
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView


class ActivityCrop : AppCompatActivity() {
    private lateinit var imageViewCropped: CropImageView
    private lateinit var textViewCrop: TextView
    private lateinit var textViewRotate: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crop)
        imageViewCropped = findViewById(R.id.cropImageView)
        textViewCrop = findViewById(R.id.textViewCrop)
        textViewRotate = findViewById(R.id.textViewRotate)

        val imageUri = intent.getStringExtra(AnNot.ObjIntentKeys.IMAGE_URI)
        imageViewCropped.setImageUriAsync(imageUri!!.toUri())
        textViewCrop.setOnClickListener {
            imageViewCropped.getCroppedImageAsync()

        }
        imageViewCropped.setOnCropImageCompleteListener { _, result ->
            val bit = result.bitmap
            if (result.isSuccessful) {
                methTakeResult(bit)
            } else {

            }
        }
        textViewRotate.setOnClickListener {
            imageViewCropped.rotateImage(90)
        }

    }

    private fun methTakeResult(bitmap: Bitmap) {
//        uri = bitmap
        val image = InputImage.fromBitmap(bitmap, 0)

        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        val result = recognizer.process(image)
            .addOnSuccessListener { visionText ->
                if (visionText.text.isNotBlank()) {
                    vision = visionText

                    startActivity(
                        Intent(
                            this@ActivityCrop, ActivityImageTranslatorResult::class.java
                        ).apply {
                            putExtra(
                                AnNot.ObjIntentKeys.TEXT_SOURCE,
                                visionText.textBlocks[0].boundingBox
                            )
                        })
                } else {
                }
                finish()

            }.addOnFailureListener { e -> }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                val resultUri = result.uri
//                imageViewCropped.setImageURI(resultUri)
            }
        }
    }

}