package com.example.imagetranslater.ui.imageTranslator

import android.graphics.Bitmap
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import com.example.imagetranslater.R
import com.example.imagetranslater.databinding.ActivityCropBinding
import com.example.imagetranslater.utils.AnNot.Image.translate
import com.example.imagetranslater.utils.AnNot.Image.uri
import com.theartofdev.edmodo.cropper.CropImageView
import java.io.File
import java.io.FileOutputStream


class ActivityCrop : AppCompatActivity() {
    private lateinit var imageViewCropped: CropImageView
    lateinit var binding: ActivityCropBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_crop)

        imageViewCropped = findViewById(R.id.cropImageView)

        imageViewCropped.setImageUriAsync(uri)
        binding.textViewCrop.setOnClickListener {
            imageViewCropped.getCroppedImageAsync()

        }
        imageViewCropped.setOnCropImageCompleteListener { _, result ->
            if (result.isSuccessful) {
                saveImage(result.bitmap)
                translate = true
                finish()
            }
        }
        binding.textViewRotate.setOnClickListener {
            imageViewCropped.rotateImage(90)
        }


    }

    private fun saveImage(bm: Bitmap) {
        val n = System.currentTimeMillis()
        val fName = "Original_crop-$n.jpeg"
        val file = File(getExternalFilesDir("")!!.absolutePath + fName)
        if (file.exists()) file.delete()
        try {
            val out = FileOutputStream(file)
            bm.setHasAlpha(true)
            bm.compress(Bitmap.CompressFormat.JPEG, 100, out)
            out.flush()
            out.close()
            uri = file.absolutePath.toString().toUri()
        } catch (e: Exception) {
            e.printStackTrace()

        }
    }



}