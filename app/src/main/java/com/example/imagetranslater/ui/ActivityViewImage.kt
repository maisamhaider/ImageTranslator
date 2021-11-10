package com.example.imagetranslater.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.imagetranslater.R
import com.example.imagetranslater.datasource.pinned.EntityPinned
import com.example.imagetranslater.ui.translator.ActivityTranslator
import com.example.imagetranslater.utils.AnNot.ObjIntentKeys.DATE
import com.example.imagetranslater.utils.AnNot.ObjIntentKeys.ID
import com.example.imagetranslater.utils.AnNot.ObjIntentKeys.IMAGE_ORIGINAL
import com.example.imagetranslater.utils.AnNot.ObjIntentKeys.IMAGE_RESULT
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
import com.example.imagetranslater.utils.AppPreferences.funAddString
import com.example.imagetranslater.utils.Singleton.funCopy
import com.example.imagetranslater.viewmodel.VMPinned
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ActivityViewImage : AppCompatActivity() {
    private lateinit var imageViewPin: ImageView
    private lateinit var imageView: ImageView
    private lateinit var imageView1: ImageView
    private lateinit var imageViewTranslate: ImageView
    private lateinit var btnCopy: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_image)
        imageViewPin = findViewById(R.id.imageViewPin)
        imageView = findViewById(R.id.imageView)
        imageView1 = findViewById(R.id.imageView1)
        imageViewTranslate = findViewById(R.id.imageViewTranslate)
        btnCopy = findViewById(R.id.btnCopy)

        val id = intent.extras!![ID].toString().toInt()
        val imageOriginal = intent.extras!![IMAGE_ORIGINAL].toString()
        val imageResult = intent.extras!![IMAGE_RESULT].toString()
        val sourceText = intent.extras!![SOURCE_TEXT].toString()
        val text = intent.extras!![TEXT].toString()
        val sourceLanguageCode = intent.extras!![SOURCE_LANGUAGE_NAME].toString()
        val targetLanguageCode = intent.extras!![TARGET_LANGUAGE_NAME].toString()
        val sourceLanguageName = intent.extras!![SOURCE_LANGUAGE_CODE].toString()
        val targetLanguageName = intent.extras!![TARGET_LANGUAGE_CODE].toString()
        val date = intent.extras!![DATE].toString()

        Glide.with(this).load(imageOriginal).into(imageView1)
        Glide.with(this).load(imageResult).into(imageView)

        val vmPinned = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[VMPinned::class.java]

        CoroutineScope(Dispatchers.IO).launch {
            if (vmPinned.entriesCount(text) > 0) {
                launch(Dispatchers.Main) {
                    imageViewPin.setBackgroundColor(Color.BLUE)
                }
            }
        }
        imageViewPin.setOnClickListener {
            val pin = EntityPinned(
                "Detected Language to $targetLanguageName",
                sourceText,
                text,
                sourceLanguageCode,
                targetLanguageCode,
                sourceLanguageName,
                targetLanguageName,
                imageOriginal,
                textImagePath = imageResult,
                date
            )
            CoroutineScope(Dispatchers.IO).launch {
                if (vmPinned.entriesCount(text) > 0) {
                    vmPinned.funDelete(imagePath = imageResult)
                    finish()
                } else {
                    vmPinned.funInsert(pin)
                    launch(Dispatchers.Main) {
                        Toast.makeText(
                            this@ActivityViewImage,
                            "inserted",
                            Toast.LENGTH_SHORT
                        ).show()
                        imageViewPin.setBackgroundColor(Color.GREEN)
                    }
                }

            }
        }


        btnCopy.setOnClickListener {
            funCopy(text)
        }

        imageViewTranslate.setOnClickListener {
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

        imageView1.setOnClickListener {
            if (imageView.isVisible) {
                imageView.visibility = View.INVISIBLE
            } else {
                imageView.visibility = View.VISIBLE
            }
        }
        imageView.setOnClickListener {
            if (imageView.isVisible) {
                imageView.visibility = View.INVISIBLE
            } else {
                imageView.visibility = View.VISIBLE

            }
        }
    }

}