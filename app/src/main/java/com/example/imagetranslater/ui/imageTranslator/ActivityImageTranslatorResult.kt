package com.example.imagetranslater.ui.imageTranslator

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.example.imagetranslater.R
import com.example.imagetranslater.api.OnlineTranslatorApi
import com.example.imagetranslater.databinding.ActivityImageTranslatorResultBinding
import com.example.imagetranslater.datasource.pinned.EntityPinned
import com.example.imagetranslater.datasource.recent.EntityRecent
import com.example.imagetranslater.interfaces.TranslatorCallBack
import com.example.imagetranslater.ui.ActivityLanguages
import com.example.imagetranslater.ui.translator.ActivityTranslator
import com.example.imagetranslater.utils.AnNot
import com.example.imagetranslater.utils.AnNot.Image.FROM_GALLERY
import com.example.imagetranslater.utils.AnNot.Image.translate
import com.example.imagetranslater.utils.AnNot.Image.uri
import com.example.imagetranslater.utils.AnNot.Image.vision
import com.example.imagetranslater.utils.AnNot.ObjIntentKeys.LANGUAGE_ONLINE
import com.example.imagetranslater.utils.AnNot.ObjPreferencesKeys.SOURCE_LANGUAGE_SELECTED_CODE_IMAGE_TRANSLATOR
import com.example.imagetranslater.utils.AnNot.ObjPreferencesKeys.SOURCE_LANGUAGE_SELECTED_CODE_TRANSLATOR
import com.example.imagetranslater.utils.AnNot.ObjPreferencesKeys.SOURCE_LANGUAGE_SELECTED_NAME_IMAGE_TRANSLATOR
import com.example.imagetranslater.utils.AnNot.ObjPreferencesKeys.SOURCE_LANGUAGE_SELECTED_NAME_TRANSLATOR
import com.example.imagetranslater.utils.AnNot.ObjPreferencesKeys.TARGET_LANGUAGE_SELECTED_CODE_IMAGE_TRANSLATOR
import com.example.imagetranslater.utils.AnNot.ObjPreferencesKeys.TARGET_LANGUAGE_SELECTED_CODE_TRANSLATOR
import com.example.imagetranslater.utils.AnNot.ObjPreferencesKeys.TARGET_LANGUAGE_SELECTED_NAME_IMAGE_TRANSLATOR
import com.example.imagetranslater.utils.AnNot.ObjPreferencesKeys.TARGET_LANGUAGE_SELECTED_NAME_TRANSLATOR
import com.example.imagetranslater.utils.AnNot.ObjPreferencesKeys.TARGET_RECENT_LANGUAGES_CODE_IMAGE_TRANSLATOR
import com.example.imagetranslater.utils.AnNot.ObjPreferencesKeys.TARGET_RECENT_LANGUAGE_SELECTED_IMAGE_TRANSLATOR
import com.example.imagetranslater.utils.AppPreferences.funAddString
import com.example.imagetranslater.utils.AppPreferences.funGetString
import com.example.imagetranslater.utils.OCR
import com.example.imagetranslater.utils.Singleton.createImage
import com.example.imagetranslater.utils.Singleton.funCopy
import com.example.imagetranslater.utils.Singleton.funLaunchLanguagesActivity
import com.example.imagetranslater.utils.Singleton.shareWithText
import com.example.imagetranslater.utils.Singleton.toastLong
import com.example.imagetranslater.viewmodel.VMPinned
import com.example.imagetranslater.viewmodel.VMRecent
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.*
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class ActivityImageTranslatorResult : AppCompatActivity(), TranslatorCallBack {

    private lateinit var onlineTranslatorApi: OnlineTranslatorApi
    private val translatedList: MutableList<String> = ArrayList()

    lateinit var rSB: StringBuilder
    private lateinit var sSB: StringBuilder
    private var textImagePath = String()
    private var shareImagePath = String()
    private var date = String()

    private val vmPinned: VMPinned by viewModels()
    private val vmRecent: VMRecent by viewModels()
    private lateinit var alertDialog: AlertDialog

    private lateinit var binding: ActivityImageTranslatorResultBinding

    lateinit var scop: CoroutineScope
//    lateinit var draw: DrawingConfig

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_image_translator_result)
        scop = CoroutineScope(Job())
        onlineTranslatorApi = OnlineTranslatorApi(this)

//        draw = DrawingConfig()

        binding.imageViewPin.setOnClickListener {
            val sourceName = funGetString(
                SOURCE_LANGUAGE_SELECTED_NAME_IMAGE_TRANSLATOR,
                "English"
            )
            val targetName = funGetString(
                TARGET_LANGUAGE_SELECTED_NAME_IMAGE_TRANSLATOR,
                "English"
            )
            val sourceCode = funGetString(
                SOURCE_LANGUAGE_SELECTED_CODE_IMAGE_TRANSLATOR,
                "en"
            )
            val targetCODE = funGetString(
                TARGET_LANGUAGE_SELECTED_CODE_IMAGE_TRANSLATOR,
                "en"
            )
            val pin = EntityPinned(
                "$sourceName to $targetName",
                sSB.toString(),
                rSB.toString(),
                sourceName,
                targetName,
                sourceCode,
                targetCODE,
                uri.toString(),
                textImagePath = textImagePath,
                shareImagePath,
                date
            )
            scop.launch(Dispatchers.IO) {
                if (vmPinned.entriesCount(rSB.toString()) > 0) {
                    binding.imageViewPin.setBackgroundColor(Color.TRANSPARENT)
                    vmPinned.funDelete(textImagePath)
                    cancel()
                } else {
                    vmPinned.funInsert(pin)
                    launch(Dispatchers.Main) {
                        Toast.makeText(
                            this@ActivityImageTranslatorResult,
                            "inserted",
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.imageViewPin.setBackgroundColor(Color.GREEN)
                    }
                    cancel()
                }
            }
        }
        binding.imageViewCopy.setOnClickListener {
            funCopy(rSB.toString())
            toastLong("copied")
        }
        binding.imageViewCrop.setOnClickListener {
            startActivity(Intent(this, ActivityCrop::class.java))
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
        binding.viewMore.texViewCrop.setOnClickListener {
            startActivity(Intent(this, ActivityCrop::class.java))
        }
        binding.viewMore.texViewTranslateTo.setOnClickListener {
            translate = true
            funLaunchLanguagesActivity(
                LANGUAGE_ONLINE,
                TARGET_RECENT_LANGUAGES_CODE_IMAGE_TRANSLATOR,
                TARGET_RECENT_LANGUAGE_SELECTED_IMAGE_TRANSLATOR,
                TARGET_LANGUAGE_SELECTED_CODE_IMAGE_TRANSLATOR,
                TARGET_LANGUAGE_SELECTED_NAME_IMAGE_TRANSLATOR,
                ActivityLanguages()
            )
        }
        binding.viewMore.texViewShowOriginalImage.setOnClickListener {
            binding.viewImages.imageView.visibility = View.INVISIBLE
        }
        binding.viewMore.texViewShareImageWithTran.setOnClickListener {
            translate = false
            shareWithText(shareImagePath)
        }


        binding.imageViewTranslate.setOnClickListener {
            translate = false
            toTranslateIntent()
        }
        binding.zoomLayout.setOnClickListener {
            viewVisibility(binding.viewImages.imageView)
        }
        binding.viewImages.imageView.setOnClickListener {
            viewVisibility(binding.viewImages.imageView)
        }
        binding.viewImages.imageView1.setOnClickListener {
            viewVisibility(binding.viewImages.imageView)
        }
        binding.imageViewVisibility.setOnClickListener {
            if (binding.viewImages.imageView.isVisible) {
                binding.viewImages.imageView.visibility = View.INVISIBLE
                binding.imageViewVisibility.setImageResource(R.drawable.ic_visible)
            } else {
                binding.viewImages.imageView.visibility = View.VISIBLE
                binding.imageViewVisibility.setImageResource(R.drawable.ic_visibility_off)
            }
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
            putExtra(AnNot.ObjIntentKeys.SOURCE_TEXT, sSB.toString())
            putExtra(AnNot.ObjIntentKeys.DATE, date)
            putExtra(AnNot.ObjIntentKeys.TEXT, rSB.toString())
        })
        val sourceName = funGetString(
            SOURCE_LANGUAGE_SELECTED_NAME_IMAGE_TRANSLATOR,
            "English"
        )
        val targetName = funGetString(
            TARGET_LANGUAGE_SELECTED_NAME_IMAGE_TRANSLATOR,
            "English"
        )
        val sourceCode = funGetString(
            SOURCE_LANGUAGE_SELECTED_CODE_IMAGE_TRANSLATOR,
            "en"
        )
        val targetCODE = funGetString(
            TARGET_LANGUAGE_SELECTED_CODE_IMAGE_TRANSLATOR,
            "en"
        )
        funAddString(SOURCE_LANGUAGE_SELECTED_NAME_TRANSLATOR, sourceName)
        funAddString(TARGET_LANGUAGE_SELECTED_NAME_TRANSLATOR, targetName)
        funAddString(SOURCE_LANGUAGE_SELECTED_CODE_TRANSLATOR, sourceCode)
        funAddString(TARGET_LANGUAGE_SELECTED_CODE_TRANSLATOR, targetCODE)
    }


    private fun methTakeResult() {
        val source = funGetString(
            SOURCE_LANGUAGE_SELECTED_CODE_IMAGE_TRANSLATOR,
            "en"
        )
        val target = funGetString(
            TARGET_LANGUAGE_SELECTED_CODE_IMAGE_TRANSLATOR,
            "en"
        )
        val u: Uri = if (!FROM_GALLERY) {
            if (!uri.path!!.contains("file:///")) {
                Uri.fromFile(File(uri.path.toString()))
            } else {
                uri
            }
        } else {
            uri
        }
        uri = u

        scop.launch(Dispatchers.Main) {
            Glide.with(this@ActivityImageTranslatorResult).load(u)
                .into(binding.viewImages.imageView1)
        }

        val inputImage = InputImage.fromFilePath(this, u)

        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        recognizer.process(inputImage)
            .addOnSuccessListener { visionText ->
                if (visionText.text.isNotBlank()) {
                    vision = visionText

                    val sb = StringBuilder()
                    sSB = StringBuilder()

                    vision.textBlocks.forEach { textBlock ->
                        //loop Through each `Line`
                        textBlock.lines.forEach { currentLine ->
                            sSB.append(currentLine.text)
                            sb.append(currentLine.text + "///\n ")
                        }
                    }
                    if (sb.toString().isNotBlank()) {
                        scop.launch(Dispatchers.IO) {
                            onlineTranslatorApi.execute(
                                sb.toString(),
                                source,
                                target,
                                this@ActivityImageTranslatorResult
                            )
                        }
                    } else {


                    }

                } else {
                    scop.launch(Dispatchers.Main)
                    {
                        toastLong("No text found")
                        if (alertDialog.isShowing) {
                            alertDialog.dismiss()
                        }
                    }

                }

            }.addOnFailureListener {
                scop.launch(Dispatchers.Main)
                {
                    toastLong("Failed to recognized text")
                    if (alertDialog.isShowing) {
                        alertDialog.dismiss()
                    }
                }

            }

    }


    @SuppressLint("SimpleDateFormat")
    override fun call(result: String, source: String) {
        date =
            SimpleDateFormat(getString(R.string.dd_MMM_yyyy_h_mm_a)).format(System.currentTimeMillis())
        rSB = StringBuilder()
        if (translatedList.isNotEmpty()) translatedList.clear()
        if (result.isNotEmpty()) {
            scop.launch(Dispatchers.IO) {
                val array = result.split("///")

                array.forEach {
                    rSB.append(it)
                    translatedList.add(it)
                }

                val bit = OCR().runOcr(this@ActivityImageTranslatorResult, vision, translatedList)

                val sourceName = funGetString(
                    SOURCE_LANGUAGE_SELECTED_NAME_IMAGE_TRANSLATOR,
                    "English"
                )
                val targetName = funGetString(
                    TARGET_LANGUAGE_SELECTED_NAME_IMAGE_TRANSLATOR,
                    "English"
                )
                val sourceCode = funGetString(
                    SOURCE_LANGUAGE_SELECTED_CODE_IMAGE_TRANSLATOR,
                    "en"
                )
                val targetCODE = funGetString(
                    TARGET_LANGUAGE_SELECTED_CODE_IMAGE_TRANSLATOR,
                    "en"
                )
                scop.launch(Dispatchers.Main) {
                    try {
                        binding.clRecordSave.visibility = View.VISIBLE
                        binding.viewImages.imageView.visibility = View.VISIBLE
                        Glide.with(this@ActivityImageTranslatorResult).load(bit)
                            .into(binding.viewImages.imageView)
                    } catch (e: Exception) {
                        e.stackTrace
                    }

                    try {
                        if (alertDialog.isShowing) {
                            alertDialog.dismiss()
                        }
                        textImagePath = saveImage(bit) // result image
                        shareImagePath =
                            createImage(binding.viewImages.clImagesMain)//share able image

                    } catch (e: Exception) {
                        e.stackTrace
                    }

                    delay(3000)

                    val entity = EntityRecent(
                        "$sourceName to $targetName",
                        sSB.toString(),
                        rSB.toString(),
                        sourceName,
                        targetName,
                        sourceCode,
                        targetCODE,
                        imagePath = uri.toString(),
                        textImagePath,
                        shareImagePath,
                        date

                    )
                    vmRecent.funInsert(entity)

                    binding.mTextViewDataSaving.text = getString(R.string.saved)
                    delay(2000)

                    binding.clRecordSave.visibility = View.GONE
                }


            }
        }
    }

    override fun failure(messages: String) {
    }

    private suspend fun saveImage(bm: Bitmap): String = withContext(Dispatchers.IO) {

        val time = SimpleDateFormat(
            "EEE-dd-yyyy h:mm s am",
            Locale.getDefault()
        ).format(System.currentTimeMillis())

        val fName = "Image-$time.png"
        val file = File(getExternalFilesDir("")!!.absolutePath, fName)
        if (file.exists()) file.delete()
        try {
            val out = FileOutputStream(file)
            bm.setHasAlpha(true)
            bm.compress(Bitmap.CompressFormat.PNG, 0, out)
            out.flush()
            out.close()

            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            textImagePath
        }

    }


    private fun dialogLoading() {
        val builder = AlertDialog.Builder(this)
            .setTitle(getString(R.string.loading))
            .setMessage(getString(R.string.wait_a_moment_please)).setCancelable(false)

        alertDialog = builder.create()
        alertDialog.show()
    }

    override fun onResume() {
        super.onResume()
        scop.launch(Dispatchers.IO) {
            if (translate) {
                scop.launch(Dispatchers.Main) {
                    dialogLoading()
                }
                methTakeResult()

                translate = false
            }
            this.cancel()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            if (alertDialog.isShowing) {
                alertDialog.dismiss()
            }
        } catch (e: Exception) {
            e.stackTrace
        }


    }

    override fun onBackPressed() {
        if (binding.clRecordSave.isVisible) {
            MaterialAlertDialogBuilder(this).setTitle(getString(R.string.alert))
                .setMessage(getString(R.string.history_is_saving))
                .setPositiveButton(getString(R.string.back)) { dialog, _ ->
                    run {
                        dialog.cancel()
                        finish()
                    }
                }.setNegativeButton(
                    getString(R.string.wait)
                ) { dialog, _ -> dialog.cancel() }.show()
        } else super.onBackPressed()
    }


}



