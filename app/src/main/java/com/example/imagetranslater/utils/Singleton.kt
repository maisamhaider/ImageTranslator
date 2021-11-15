package com.example.imagetranslater.utils

import android.app.Activity
import android.content.*
import android.content.ClipData
import android.content.ClipDescription.MIMETYPE_TEXT_PLAIN
import android.content.Context.CLIPBOARD_SERVICE
import android.graphics.Bitmap
import android.net.Uri
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.Toast
import androidx.core.view.drawToBitmap
import java.io.*
import java.util.*


object Singleton : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null
    const val SDCARD1 = "/ext_card/"
    const val SDCARD2 = "/mnt/sdcard/external_sd/"
    const val SDCARD3 = "/storage/extSdCard/"
    const val SDCARD4 = "/mnt/extSdCard/"
    const val SDCARD5 = "/mnt/external_sd/"
    const val SDCARD6 = "/storage/sdcard1/"
    fun Context.initTTS() {
        if (tts == null) {
            tts = TextToSpeech(this, this@Singleton)
        }

        val engines = tts!!.engines
        var engine = tts!!.defaultEngine
        if (!engine.contains("google")) {
            engines.forEach {
                if (it.name.contains("google")) {
                    engine = it.name
                }
            }
        }
        tts = TextToSpeech(this, this@Singleton, engine)
    }

    fun Context.toastLong(messages: String) {
        Toast.makeText(this, messages, Toast.LENGTH_LONG).show()
    }

    fun Context.toastShort(messages: String) {
        Toast.makeText(this, messages, Toast.LENGTH_SHORT).show()
    }

    fun Context.funShare(text: String) {
        val intentShare = Intent()
        intentShare.action = Intent.ACTION_SEND
        intentShare.type = "text/plain"
        intentShare.putExtra(Intent.EXTRA_TEXT, text)
        startActivity(Intent.createChooser(intentShare, "Share"));

//        startActivity(intentShare)
    }


    fun Context.shareWithText(view: View) {
        val bitmap = view.drawToBitmap()

        val n = System.currentTimeMillis()
        val fName = "Image-$n.png"
        val file = File(getExternalFilesDir("")!!.absolutePath + fName)
        if (file.exists()) file.delete()
        try {
            val out = FileOutputStream(file)
            bitmap.setHasAlpha(true)
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, out)
            out.flush()
            out.close()

            val share = Intent(Intent.ACTION_SEND)
            share.type = "image/*"

            share.putExtra(Intent.EXTRA_STREAM, Uri.parse(file.absolutePath))
            startActivity(Intent.createChooser(share, "Share image using"));
//            file.delete()
        } catch (e: Exception) {
            e.printStackTrace()
        }


    }


    fun Context.funCopy(text: String) {
        val clipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clipData: ClipData = ClipData.newPlainText("", text)
        clipboardManager.setPrimaryClip(clipData)
    }


    fun Context.funTextToSpeech(text: String, code: String) {

        if (!tts!!.isSpeaking) {
            tts!!.setSpeechRate(1f)
            val lng = Locale.getAvailableLocales()
            val l = if (lng.contains(Locale.forLanguageTag(code))) {
                Locale.forLanguageTag(code)
            } else {
                Locale.US
            }
            tts!!.setSpeechRate(1f)
            tts!!.language = l
            tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
        }


    }

    fun Context.ttsShutdown() {
        if (tts!!.isSpeaking) {
            tts!!.stop()
        }
    }


    fun Context.funPaste(): String {
        val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?
        var pasteData = ""

        // If it does contain data, decide if you can handle the data.

        // If it does contain data, decide if you can handle the data.
        if (!clipboard!!.hasPrimaryClip()) {
        } else if (!clipboard.primaryClipDescription!!.hasMimeType(MIMETYPE_TEXT_PLAIN)) {
            // since the clipboard has data but it is not plain text
        } else {
            //since the clipboard contains plain text.
            val item = clipboard.primaryClip!!.getItemAt(0)

            // Gets the clipboard as text.
            pasteData = item.text.toString()
        }
        return pasteData
    }


    fun Context.funLaunchLanguagesActivity(
        sourceLanguagesList: String,
        recentLanguagesCodeList: String,
        recentLanguagesKey: String,
        recentLanguageKey: String,
        sourceLanguageCodeKey: String?,
        targetLanguageNameKey: String?,
        activity: Activity
    ) {
        val intent = Intent(this, activity::class.java)
        intent.putExtra(
            AnNot.ObjIntentKeys.WHICH_RECENT_LANGUAGE_CODE_LIST,
            recentLanguagesCodeList
        )
        intent.putExtra(AnNot.ObjIntentKeys.WHICH_LANGUAGE_CODE, sourceLanguageCodeKey)
        intent.putExtra(AnNot.ObjIntentKeys.WHICH_LANGUAGE_CODE, sourceLanguageCodeKey)
        intent.putExtra(AnNot.ObjIntentKeys.WHICH_LANGUAGE_NAME, targetLanguageNameKey)
        intent.putExtra(AnNot.ObjIntentKeys.WHICH_RECENT_LANGUAGE, recentLanguageKey)
        intent.putExtra(AnNot.ObjIntentKeys.WHICH_RECENT_LANGUAGE_LIST, recentLanguagesKey)
        intent.putExtra(AnNot.ObjIntentKeys.SOURCE_LANGUAGE_LIST, sourceLanguagesList)
        startActivity(intent)
    }

    override fun onInit(p0: Int) {
    }





}