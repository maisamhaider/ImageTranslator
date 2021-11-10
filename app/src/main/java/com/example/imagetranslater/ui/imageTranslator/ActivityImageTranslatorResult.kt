package com.example.imagetranslater.ui.imageTranslator

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.imagetranslater.R
import com.example.imagetranslater.api.OnlineTranslatorApi
import com.example.imagetranslater.datasource.pinned.EntityPinned
import com.example.imagetranslater.datasource.recent.EntityRecent
import com.example.imagetranslater.interfaces.TranslatorCallBack
import com.example.imagetranslater.utils.AnNot
import com.example.imagetranslater.utils.AnNot.ObjPreferencesKeys.SOURCE_LANGUAGE_SELECTED_CODE_IMAGE_TRANSLATOR
import com.example.imagetranslater.utils.AnNot.ObjPreferencesKeys.SOURCE_LANGUAGE_SELECTED_NAME_IMAGE_TRANSLATOR
import com.example.imagetranslater.utils.AnNot.ObjPreferencesKeys.TARGET_LANGUAGE_SELECTED_CODE_IMAGE_TRANSLATOR
import com.example.imagetranslater.utils.AnNot.ObjPreferencesKeys.TARGET_LANGUAGE_SELECTED_NAME_IMAGE_TRANSLATOR
import com.example.imagetranslater.utils.AnNot.imaeg.uri
import com.example.imagetranslater.utils.AnNot.imaeg.vision
import com.example.imagetranslater.utils.AppPreferences.funGetString
import com.example.imagetranslater.utils.Singleton.funCopy
import com.example.imagetranslater.viewmodel.VMPinned
import com.example.imagetranslater.viewmodel.VMRecent
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.*
import java.text.SimpleDateFormat
import java.util.concurrent.Executor
import java.util.concurrent.Executors


class ActivityImageTranslatorResult : AppCompatActivity(), TranslatorCallBack {

    private lateinit var imageViewPin: ImageView
    private lateinit var imageView: ImageView
    private lateinit var imageView1: ImageView
    private lateinit var btnCopy: Button
    private lateinit var onlineTranslatorApi: OnlineTranslatorApi
    private val textList: MutableList<String> = ArrayList()
    private var textList1: MutableList<String> = ArrayList()
    private val translatedList: MutableList<String> = ArrayList()

    private val rSB = StringBuffer()
    private val sSB = StringBuffer()
    private var textImagePath = String()
    private var date = String()

    lateinit var vmPinned: VMPinned
    lateinit var vmRecent: VMRecent
    private lateinit var alertDialog: AlertDialog

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_translator_result)
        dialogLoading()
        vmRecent = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[VMRecent::class.java]
        vmPinned = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[VMPinned::class.java]

        onlineTranslatorApi = OnlineTranslatorApi(this)

        imageViewPin = findViewById(R.id.imageViewPin)
        imageView = findViewById(R.id.imageView)
        imageView1 = findViewById(R.id.imageView1)
        btnCopy = findViewById(R.id.btnCopy)

        Glide.with(this).load(uri).into(imageView1)


        imageViewPin.setOnClickListener {
            val sourceName = funGetString(
                AnNot.ObjPreferencesKeys.SOURCE_LANGUAGE_SELECTED_NAME_IMAGE_TRANSLATOR,
                "English"
            )
            val targetName = funGetString(
                AnNot.ObjPreferencesKeys.TARGET_LANGUAGE_SELECTED_NAME_IMAGE_TRANSLATOR,
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
                "Detected Language to $targetName",
                sSB.toString(),
                rSB.toString(),
                sourceName,
                targetName,
                sourceCode,
                targetCODE,
                uri.toString(),
                textImagePath = textImagePath,
                date
            )
            CoroutineScope(Dispatchers.IO).launch {
                if (vmPinned.entriesCount(rSB.toString()) > 0) {
                    imageViewPin.setBackgroundColor(Color.TRANSPARENT)
                    vmPinned.funDelete(textImagePath)
                } else {
                    vmPinned.funInsert(pin)
                    launch(Dispatchers.Main) {
                        Toast.makeText(
                            this@ActivityImageTranslatorResult,
                            "inserted",
                            Toast.LENGTH_SHORT
                        ).show()
                        imageViewPin.setBackgroundColor(Color.GREEN)
                    }
                }
            }
        }
        btnCopy.setOnClickListener {
            funCopy(rSB.toString())
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
        val v: Executor = Executors.newSingleThreadExecutor()
        v.execute {
            methTakeResult(uri)
        }

    }


    private fun ocr(uri: Uri, text: Text): Bitmap {
        val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ImageDecoder.decodeBitmap(
                ImageDecoder.createSource(this.contentResolver, uri)
            )
        } else {
            MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
        }

        val items: List<Text.TextBlock> = text.textBlocks
        var myItem: Text.TextBlock?
        val blocks: MutableList<Text.TextBlock> = ArrayList()

        for (i in items.indices) {
            myItem = items[i]
            //Add All TextBlocks to the `blocks` List
            blocks.add(myItem)
        }

        //Create the Canvas object,
        //Which ever way you do image that is ScreenShot for example, you
        //need the views Height and Width to draw recatngles
        //because the API detects the position of Text on the View
        //So Dimesnions are important for Draw method to draw at that Text
        //Location
        val canvas: Canvas?
        val tempBitmap: Bitmap =
            Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        tempBitmap.setHasAlpha(true)
        tempBitmap.density = DisplayMetrics.DENSITY_HIGH
        canvas = Canvas(tempBitmap)

        // Break the text into multiple lines and draw each one according to its own bounding box.
        var counter = 1
        blocks.forEach { textBlock ->
            val textLines: MutableList<Text.Line> = textBlock.lines
            //loop Through each `Line`
            textLines.forEach { currentLine ->
                currentLine.elements
                val textPaint = Paint()
                val rectPaint = Paint()
                rectPaint.style = Paint.Style.FILL
                rectPaint.color = Color.WHITE
//                rectPaint.alpha = Color.alpha(50)
////                    val rect = RectF(currentLine.boundingBox)
                val rect = currentLine.boundingBox
                canvas.drawRect(rect!!, rectPaint)

                textPaint.color = Color.BLACK
                textPaint.typeface = Typeface.MONOSPACE
//                    textPaint.getTextBounds(it.text, 0, length, rect)
//                textPaint.measureText(translatedList[counter - 1])
                val fm = textPaint.fontMetrics
//                textPaint.textSize = 50f  //set text size
                var size = currentLine.boundingBox!!.height().toFloat() //set text size
//                textPaint.textSize = currentLine.boundingBox!!.height().toFloat() //set text size
                if (size > 80) {
                    size = 80f
                }
                textPaint.textSize = size //set text size
                textPaint.textAlign = Paint.Align.CENTER
                textPaint.getFontMetrics(fm)


                try {
                    canvas.drawText(
                        translatedList[counter - 1],
                        currentLine.boundingBox!!.exactCenterX(),
                        currentLine.boundingBox!!.exactCenterY() - (fm.ascent + fm.descent) / 2,
                        textPaint
                    )
                } catch (e: Exception) {

                }


                counter++
            }

        }

        return tempBitmap
    }

    private fun methTakeResult(image: Uri) {
        val source = funGetString(
            SOURCE_LANGUAGE_SELECTED_CODE_IMAGE_TRANSLATOR,
            "en"
        )
        val target = funGetString(
            TARGET_LANGUAGE_SELECTED_CODE_IMAGE_TRANSLATOR,
            "en"
        )
        uri = image
        val image1 = InputImage.fromFilePath(this, image)

        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        recognizer.process(image1)
            .addOnSuccessListener { visionText ->
                if (visionText.text.isNotBlank()) {
                    vision = visionText

                    val items: List<Text.TextBlock> = vision.textBlocks
                    var myItem: Text.TextBlock?
                    val blocks: MutableList<Text.TextBlock> = ArrayList()

                    for (i in items.indices) {
                        myItem = items[i]
                        blocks.add(myItem)
                    }
                    val sb = StringBuilder()
                    blocks.forEach { textBlock ->
                        //loop Through each `Line`
                        textBlock.lines.forEach { currentLine ->
                            textList.add(currentLine.text)
                            sSB.append(currentLine.text)
                            sb.append(currentLine.text + "///\n ")
                            Log.e("Size", currentLine.text)
                        }
                    }
                    Log.e("Size", "textList: ${textList.size}")
                    textList1.add(sb.toString())
                    Log.e("Size", "textList1: ${textList1.size}")


                    onlineTranslatorApi.execute(textList1[0], source, target, this)

                } else {
                }

            }.addOnFailureListener { }
    }


    override fun call(result: String, source: String) {
        date = SimpleDateFormat("DD,MMM,yyyy h:mm am").format(System.currentTimeMillis())
        if (translatedList.isNotEmpty()) translatedList.clear()
        if (result.isNotEmpty()) {
            val v: Executor = Executors.newSingleThreadExecutor()
            val handler = Handler(Looper.getMainLooper())
            v.execute {
                val array = result.split("///")

                array.forEach {
                    rSB.append(it)
                    Log.e("Size", it)
                    translatedList.add(it)
                }
                Log.e("Size", "translatedList: ${translatedList.size}")
//
                val bit = ocr(uri, vision)
                saveImage(bit)

                handler.post {
                    imageView.setImageBitmap(bit)
//                imageView.setBackgroundColor(ContextCompat.getColor(this, R.color.black1))
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

                    val entity = EntityRecent(
                        "Detected Language to $targetName",
                        sSB.toString(),
                        rSB.toString(),
                        sourceName,
                        targetName,
                        sourceCode,
                        targetCODE,
                        uri.toString(),
                        textImagePath, date
                    )
                    vmRecent.funInsert(entity)
                    if (alertDialog.isShowing) {
                        alertDialog.dismiss()
                    }
                }
            }
        }
    }

    override fun failure(messages: String) {
    }

    private fun saveImage(bm: Bitmap) {
        val n = System.currentTimeMillis()
        val fName = "Image-$n.png"
        val file = File(getExternalFilesDir("")!!.absolutePath + fName)
        if (file.exists()) file.delete()
        try {
            val out = FileOutputStream(file)
            bm.setHasAlpha(true)
            bm.compress(Bitmap.CompressFormat.PNG, 0, out)
            out.flush()
            out.close()
            textImagePath = file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun dialogLoading() {
        val builder = AlertDialog.Builder(this)
            .setTitle("Loading").setMessage("Wait a moment.Please").setCancelable(false)
        alertDialog = builder.create()
        alertDialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (alertDialog.isShowing) {
            alertDialog.dismiss()
        }
    }


}



