package com.example.imagetranslater.ui

import android.content.ContentResolver
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.example.imagetranslater.R
import com.example.imagetranslater.databinding.ActivityViewImageBinding
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
import com.example.imagetranslater.utils.AnNot.ObjRoomItems.RECENT
import com.example.imagetranslater.utils.AnNot.ObjRoomItems.TYPE
import com.example.imagetranslater.utils.AppPreferences.funAddString
import com.example.imagetranslater.utils.Singleton.funCopy
import com.example.imagetranslater.utils.Singleton.shareWithText
import com.example.imagetranslater.utils.Singleton.toastShort
import com.example.imagetranslater.viewmodel.VMPinned
import com.example.imagetranslater.viewmodel.VMRecent
import com.google.android.material.imageview.ShapeableImageView
import com.otaliastudios.zoom.ZoomLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException


class ActivityViewImage : AppCompatActivity() {

    private lateinit var imageViewVisibility: ShapeableImageView
    lateinit var texViewCrop: TextView
    private lateinit var texViewTranslateTo: TextView
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

    lateinit var imageOriginal: String
    lateinit var imageResult: String
    lateinit var sourceText: String
    lateinit var text: String
    lateinit var sourceLanguageCode: String
    lateinit var targetLanguageCode: String
    lateinit var sourceLanguageName: String
    lateinit var targetLanguageName: String
    lateinit var date: String

    lateinit var binding: ActivityViewImageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_image)
//        binding = ActivityViewImageBinding.bind(R.layout.activity_view_image)
        imageViewDelete = findViewById(R.id.imageViewDelete)
        imageViewPin = findViewById(R.id.imageViewPin)
        imageView = findViewById(R.id.imageView)
        imageView1 = findViewById(R.id.imageView1)
        imageViewTranslate = findViewById(R.id.imageViewTranslate)
        imageViewCopy = findViewById(R.id.imageViewCopy)
        imageViewMore = findViewById(R.id.imageViewMore)

        zoomLayout = findViewById(R.id.zoomLayout)
        viewMore = findViewById(R.id.viewMore)

        imageViewVisibility = findViewById(R.id.imageViewVisibility)
        texViewCrop = findViewById(R.id.texViewCrop)
        texViewTranslateTo = findViewById(R.id.texViewTranslateTo)
        texViewShowOriginalImage = findViewById(R.id.texViewShowOriginalImage)
        texViewShareImageWithTran = findViewById(R.id.texViewShareImageWithTran)

        val type = intent.extras!![TYPE].toString()
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

        val vmRecent: VMRecent by viewModels()
        val vmPinned: VMPinned by viewModels()

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

        imageView.setOnClickListener { viewVisibility(imageView) }
        imageView1.setOnClickListener { viewVisibility(imageView) }

        imageViewVisibility.setOnClickListener {
            if (imageView.isVisible) {
                imageView.visibility = View.INVISIBLE
                imageViewVisibility.setImageResource(R.drawable.ic_visible)
            } else {
                imageView.visibility = View.VISIBLE
                imageViewVisibility.setImageResource(R.drawable.ic_visibility_off)
            }
        }

        imageViewDelete.setOnClickListener {
//            File(imageOriginal).delete()
            if (type == RECENT) {
                vmRecent.funDelete(id)
            } else {
                vmPinned.funDelete(id)
            }
            val file = File(imageOriginal)
            if (!file.delete()) {
                toastShort("not deleted")
                if (file.exists()) {
                    if (!file.canonicalFile.delete()) {
                        toastShort("not deleted")
                        if (file.exists()) {
                            if (!applicationContext.deleteFile(file.name)) {
                                toastShort("not deleted")
                            } else {
                                toastShort("deleted3 : $imageOriginal")
                            }
                        }
                    } else {
                        toastShort("deleted2 : $imageOriginal")
                    }
                }
            } else {
                toastShort("deleted1 : $imageOriginal")
            }

            deleteFileFromMediaStore(contentResolver, file)


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

    private fun deleteFileFromMediaStore(contentResolver: ContentResolver, file: File) {
        val sdk = Build.VERSION.SDK_INT
        if (sdk >= Build.VERSION_CODES.HONEYCOMB) {
            val canonicalPath: String = try {
                file.canonicalPath
            } catch (e: IOException) {
                file.absolutePath
            }
            val uri = MediaStore.Files.getContentUri("external")
            val result = contentResolver.delete(
                uri,
                MediaStore.Files.FileColumns.DATA + "=?", arrayOf(canonicalPath)
            )
            if (result == 0) {
                val absolutePath = file.absolutePath
                if (absolutePath != canonicalPath) {
                    contentResolver.delete(
                        uri,
                        MediaStore.Files.FileColumns.DATA + "=?", arrayOf(absolutePath)
                    )
                }
            }
        }
    }

}