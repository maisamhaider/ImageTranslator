package com.example.imagetranslater.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.example.imagetranslater.R
import com.example.imagetranslater.databinding.ActivityViewImageBinding
import com.example.imagetranslater.datasource.pinned.EntityPinned
import com.example.imagetranslater.ui.imageTranslator.ActivityImageTranslatorResult
import com.example.imagetranslater.ui.translator.ActivityTranslator
import com.example.imagetranslater.utils.AnNot
import com.example.imagetranslater.utils.AnNot.ObjIntentKeys.DATE
import com.example.imagetranslater.utils.AnNot.ObjIntentKeys.ID
import com.example.imagetranslater.utils.AnNot.ObjIntentKeys.IMAGE_ORIGINAL
import com.example.imagetranslater.utils.AnNot.ObjIntentKeys.IMAGE_RESULT
import com.example.imagetranslater.utils.AnNot.ObjIntentKeys.SHARE_IMAGE_RESULT
import com.example.imagetranslater.utils.AnNot.ObjIntentKeys.SOURCE_LANGUAGE_CODE
import com.example.imagetranslater.utils.AnNot.ObjIntentKeys.SOURCE_LANGUAGE_NAME
import com.example.imagetranslater.utils.AnNot.ObjIntentKeys.SOURCE_TEXT
import com.example.imagetranslater.utils.AnNot.ObjIntentKeys.TARGET_LANGUAGE_CODE
import com.example.imagetranslater.utils.AnNot.ObjIntentKeys.TARGET_LANGUAGE_NAME
import com.example.imagetranslater.utils.AnNot.ObjIntentKeys.TEXT
import com.example.imagetranslater.utils.AnNot.ObjPreferencesKeys.SOURCE_LANGUAGE_SELECTED_CODE_TRANSLATOR
import com.example.imagetranslater.utils.AnNot.ObjPreferencesKeys.SOURCE_LANGUAGE_SELECTED_NAME_TRANSLATOR
import com.example.imagetranslater.utils.AnNot.ObjPreferencesKeys.TARGET_LANGUAGE_SELECTED_CODE_TRANSLATOR
import com.example.imagetranslater.utils.AnNot.ObjPreferencesKeys.TARGET_LANGUAGE_SELECTED_NAME_TRANSLATOR
import com.example.imagetranslater.utils.AnNot.ObjRoomItems.RECENT
import com.example.imagetranslater.utils.AnNot.ObjRoomItems.TYPE
import com.example.imagetranslater.utils.AppPreferences.funAddString
import com.example.imagetranslater.utils.Singleton.funCopy
import com.example.imagetranslater.utils.Singleton.funLaunchLanguagesActivity
import com.example.imagetranslater.utils.Singleton.shareWithText
import com.example.imagetranslater.viewmodel.VMPinned
import com.example.imagetranslater.viewmodel.VMRecent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File


class ActivityViewImage : AppCompatActivity() {
    lateinit var imageOriginal: String
    lateinit var imageResult: String
    lateinit var shareImagePath: String
    lateinit var sourceText: String
    lateinit var text: String
    lateinit var sourceLanguageCode: String
    lateinit var targetLanguageCode: String
    lateinit var sourceLanguageName: String
    lateinit var targetLanguageName: String
    lateinit var date: String

    lateinit var binding: ActivityViewImageBinding

    companion object {
        @JvmStatic
        var translateTo = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_image)

        val type = intent.extras!![TYPE].toString()
        val id = intent.extras!![ID].toString().toInt()
        imageOriginal = intent.extras!![IMAGE_ORIGINAL].toString()
        imageResult = intent.extras!![IMAGE_RESULT].toString()
        shareImagePath = intent.extras!![SHARE_IMAGE_RESULT].toString()
        sourceText = intent.extras!![SOURCE_TEXT].toString()
        text = intent.extras!![TEXT].toString()
        sourceLanguageCode = intent.extras!![SOURCE_LANGUAGE_NAME].toString()
        targetLanguageCode = intent.extras!![TARGET_LANGUAGE_NAME].toString()
        sourceLanguageName = intent.extras!![SOURCE_LANGUAGE_CODE].toString()
        targetLanguageName = intent.extras!![TARGET_LANGUAGE_CODE].toString()
        date = intent.extras!![DATE].toString()

        Glide.with(this).load(imageOriginal).into(binding.includedImages.imageView1)
        Glide.with(this).load(imageResult).into(binding.includedImages.imageView)

        val vmRecent: VMRecent by viewModels()
        val vmPinned: VMPinned by viewModels()

        CoroutineScope(Dispatchers.IO).launch {
            if (vmPinned.entriesCount(text) > 0) {
                launch(Dispatchers.Main) {
                    binding.imageViewPin.setBackgroundColor(Color.GREEN)
                }
            }
        }
        binding.imageViewPin.setOnClickListener {
            val pin = EntityPinned(
                "$sourceLanguageName to $targetLanguageName",
                sourceText,
                text,
                sourceLanguageCode,
                targetLanguageCode,
                sourceLanguageName,
                targetLanguageName,
                imageOriginal,
                textImagePath = imageResult,
                shareImagePath,
                date
            )
            CoroutineScope(Dispatchers.IO).launch {
                if (vmPinned.entriesCount(text) > 0) {
                    vmPinned.funDelete(imagePath = imageResult)
                    launch(Dispatchers.Main) {
                        binding.imageViewPin.setBackgroundColor(Color.TRANSPARENT)
                    }

                } else {
                    vmPinned.funInsert(pin)
                    launch(Dispatchers.Main) {
                        Toast.makeText(
                            this@ActivityViewImage,
                            "inserted",
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.imageViewPin.setBackgroundColor(Color.GREEN)
                    }
                }

            }
        }


        binding.imageViewCopy.setOnClickListener {
            funCopy(text)
        }
        binding.imageViewMore.setOnClickListener {
            if (binding.viewMore.clMore.isVisible) {
                binding.viewMore.clMore.visibility = View.GONE
            } else {
                binding.viewMore.clMore.visibility = View.VISIBLE
            }

        }
        binding.viewMore.clMore.setOnClickListener {
            binding.viewMore.clMore.visibility = View.GONE
        }
        binding.viewMore.texViewCrop.visibility = View.GONE
        binding.viewMore.texViewShowOriginalImage.visibility = View.GONE

        binding.viewMore.texViewTranslateTo.setOnClickListener {
            translateTo = true
            funLaunchLanguagesActivity(
                AnNot.ObjIntentKeys.LANGUAGE_ONLINE,
                AnNot.ObjPreferencesKeys.TARGET_RECENT_LANGUAGES_CODE_IMAGE_TRANSLATOR,
                AnNot.ObjPreferencesKeys.TARGET_RECENT_LANGUAGE_SELECTED_IMAGE_TRANSLATOR,
                AnNot.ObjPreferencesKeys.TARGET_LANGUAGE_SELECTED_CODE_IMAGE_TRANSLATOR,
                AnNot.ObjPreferencesKeys.TARGET_LANGUAGE_SELECTED_NAME_IMAGE_TRANSLATOR,
                ActivityLanguages()
            )
        }
        binding.imageViewTranslate.setOnClickListener {
            toTranslateIntent()
        }

        binding.viewMore.texViewShareImageWithTran.setOnClickListener {
            Log.e("share_image", "share_image=========> $shareImagePath")
            shareWithText(shareImagePath)
        }

        binding.includedImages.imageView.setOnClickListener { viewVisibility(binding.includedImages.imageView) }
        binding.includedImages.imageView1.setOnClickListener { viewVisibility(binding.includedImages.imageView) }

        binding.imageViewVisibility.setOnClickListener {
            if (binding.includedImages.imageView.isVisible) {
                binding.includedImages.imageView.visibility = View.INVISIBLE

                binding.imageViewVisibility.setImageResource(R.drawable.ic_visible)
            } else {
                binding.includedImages.imageView.visibility = View.VISIBLE
                binding.imageViewVisibility.setImageResource(R.drawable.ic_visibility_off)
            }
        }

        binding.imageViewDelete.setOnClickListener {
            val image = imageOriginal.substring(7, imageOriginal.lastIndex + 1)
            Log.e("File", "File=============>$image")
            Log.e("File", "File=============>$imageResult")
            if (type == RECENT) {
                vmRecent.funDelete(id)
            } else {
                vmPinned.funDelete(id)
            }
            //oFile = Original File
            val oFile = File(image)
            if (oFile.exists()) oFile.delete()

            //rFile = Result File
            val rFile = File(imageResult)
            if (rFile.exists()) rFile.delete()

            //sFile = share File
            val sFile = File(shareImagePath)
            if (sFile.exists()) sFile.delete()
            finish()
        }

    }

    private fun viewVisibility(view: View) {
        if (view.isVisible) {
            view.visibility = View.INVISIBLE
        } else {
            view.visibility = View.VISIBLE
        }
    }

    private fun toTranslateIntent() {
        startActivity(Intent(this, ActivityTranslator::class.java).apply {
            putExtra(SOURCE_TEXT, sourceText)
            putExtra(DATE, date)
            putExtra(TEXT, text)
        })

        funAddString(SOURCE_LANGUAGE_SELECTED_NAME_TRANSLATOR, sourceLanguageName)
        funAddString(TARGET_LANGUAGE_SELECTED_NAME_TRANSLATOR, targetLanguageName)
        funAddString(SOURCE_LANGUAGE_SELECTED_CODE_TRANSLATOR, sourceLanguageCode)
        funAddString(TARGET_LANGUAGE_SELECTED_CODE_TRANSLATOR, targetLanguageCode)
    }


    override fun onResume() {
        super.onResume()

        if (translateTo) {
            AnNot.Image.uri = imageOriginal.toUri()
            startActivity(Intent(this, ActivityImageTranslatorResult::class.java))
            translateTo = false
            finish()
        }

    }

}