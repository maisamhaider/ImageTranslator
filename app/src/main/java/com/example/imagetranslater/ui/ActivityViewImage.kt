package com.example.imagetranslater.ui

import android.content.Intent
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.PointF
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
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
import com.example.imagetranslater.utils.Singleton.shareWithText
import com.example.imagetranslater.viewmodel.VMPinned
import com.otaliastudios.zoom.ZoomLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File


class ActivityViewImage : AppCompatActivity() {

    lateinit var texViewCrop: TextView
    lateinit var texViewTranslateTo: TextView
    lateinit var texViewShowOriginalImage: TextView
    lateinit var texViewShareImageWithTran: TextView
    lateinit var viewMore: View
    lateinit var zoomLayout: ZoomLayout
    private lateinit var imageViewMore: ImageView
    lateinit var imageViewDelete: ImageView


    private lateinit var imageViewPin: ImageView
    private lateinit var imageView: ImageView
    private lateinit var imageView1: ImageView
    private lateinit var imageViewTranslate: ImageView
    private lateinit var imageViewCopy: ImageView

    var matrix: Matrix = Matrix()
    var savedMatrix: Matrix = Matrix()

    // We can be in one of these 3 states
    val NONE = 0
    val DRAG = 1
    val ZOOM = 2
    var mode = NONE

    // Remember some things for zooming
    var start = PointF()
    var mid = PointF()
    var oldDist = 1f
    var savedItemClicked: String? = null

    lateinit var imageOriginal: String
    lateinit var imageResult: String
    lateinit var sourceText: String
    lateinit var text: String
    lateinit var sourceLanguageCode: String
    lateinit var targetLanguageCode: String
    lateinit var sourceLanguageName: String
    lateinit var targetLanguageName: String
    lateinit var date: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_image)
        imageViewDelete = findViewById(R.id.imageViewDelete)
        imageViewPin = findViewById(R.id.imageViewPin)
        imageView = findViewById(R.id.imageView)
        imageView1 = findViewById(R.id.imageView1)
        imageViewTranslate = findViewById(R.id.imageViewTranslate)
        imageViewCopy = findViewById(R.id.imageViewCopy)
        imageViewMore = findViewById(R.id.imageViewMore)

        zoomLayout = findViewById(R.id.zoomLayout)
        viewMore = findViewById(R.id.viewMore)

        texViewCrop = findViewById(R.id.texViewCrop)
        texViewTranslateTo = findViewById(R.id.texViewTranslateTo)
        texViewShowOriginalImage = findViewById(R.id.texViewShowOriginalImage)
        texViewShareImageWithTran = findViewById(R.id.texViewShareImageWithTran)

        val id = intent.extras!![ID].toString().toInt()
        imageOriginal = intent.extras!![IMAGE_ORIGINAL].toString()
        imageResult = intent.extras!![IMAGE_RESULT].toString()
        sourceText = intent.extras!![SOURCE_TEXT].toString()
        text = intent.extras!![TEXT].toString()
        sourceLanguageCode = intent.extras!![SOURCE_LANGUAGE_NAME].toString()
        targetLanguageCode = intent.extras!![TARGET_LANGUAGE_NAME].toString()
        sourceLanguageName = intent.extras!![SOURCE_LANGUAGE_CODE].toString()
        targetLanguageName = intent.extras!![TARGET_LANGUAGE_CODE].toString()
        date = intent.extras!![DATE].toString()

        Glide.with(this).load(imageOriginal).into(imageView1)
        Glide.with(this).load(imageResult).into(imageView)

        val vmPinned = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[VMPinned::class.java]

        CoroutineScope(Dispatchers.IO).launch {
            if (vmPinned.entriesCount(text) > 0) {
                launch(Dispatchers.Main) {
                    imageViewPin.setBackgroundColor(Color.GREEN)
                }
            }
        }
        imageViewPin.setOnClickListener {
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
                date
            )
            CoroutineScope(Dispatchers.IO).launch {
                if (vmPinned.entriesCount(text) > 0) {
                    vmPinned.funDelete(imagePath = imageResult)
                    launch(Dispatchers.Main) {
                        imageViewPin.setBackgroundColor(Color.TRANSPARENT)
                    }

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


        imageViewCopy.setOnClickListener {
            funCopy(text)
        }
        imageViewMore.setOnClickListener {
            if (viewMore.isVisible) {
                viewMore.visibility = View.GONE
            } else {
                viewMore.visibility = View.VISIBLE
            }

        }
        viewMore.setOnClickListener {
            viewMore.visibility = View.GONE
        }
        texViewCrop.visibility = View.GONE
        texViewShowOriginalImage.visibility = View.GONE

        texViewTranslateTo.setOnClickListener {
            toTranslateIntent()
        }
        imageViewTranslate.setOnClickListener {
            toTranslateIntent()
        }

        texViewShareImageWithTran.setOnClickListener {
            shareWithText(zoomLayout)
        }
        imageView1.setOnClickListener {
            viewVisibility(imageView)
        }
        imageView.setOnClickListener {
            viewVisibility(imageView)
        }
        imageViewDelete.setOnClickListener {
//            File(imageOriginal).delete()
            val file = File(imageOriginal)
            file.delete()
            if (file.exists()) {
                file.canonicalFile.delete()
                if (file.exists()) {
                    applicationContext.deleteFile(file.name)
                }
            }
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


}