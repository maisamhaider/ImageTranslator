package com.example.imagetranslater.ui.translator

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.imagetranslater.R
import com.example.imagetranslater.api.OnlineTranslatorApi
import com.example.imagetranslater.interfaces.TranslatorCallBack
import com.example.imagetranslater.ui.ActivityLanguages
import com.example.imagetranslater.utils.AnNot.ObjIntentKeys.DATE
import com.example.imagetranslater.utils.AnNot.ObjIntentKeys.LANGUAGE_ONLINE
import com.example.imagetranslater.utils.AnNot.ObjIntentKeys.SOURCE_TEXT
import com.example.imagetranslater.utils.AnNot.ObjIntentKeys.TEXT
 import com.example.imagetranslater.utils.AnNot.ObjPreferencesKeys.SOURCE_LANGUAGE_SELECTED_CODE_TRANSLATOR
import com.example.imagetranslater.utils.AnNot.ObjPreferencesKeys.SOURCE_LANGUAGE_SELECTED_NAME_TRANSLATOR
import com.example.imagetranslater.utils.AnNot.ObjPreferencesKeys.SOURCE_RECENT_LANGUAGES_CODE_TRANSLATOR
import com.example.imagetranslater.utils.AnNot.ObjPreferencesKeys.SOURCE_RECENT_LANGUAGES_TRANSLATOR
import com.example.imagetranslater.utils.AnNot.ObjPreferencesKeys.SOURCE_RECENT_LANGUAGE_SELECTED_TRANSLATOR
import com.example.imagetranslater.utils.AnNot.ObjPreferencesKeys.TARGET_LANGUAGE_SELECTED_CODE_TRANSLATOR
import com.example.imagetranslater.utils.AnNot.ObjPreferencesKeys.TARGET_LANGUAGE_SELECTED_NAME_TRANSLATOR
import com.example.imagetranslater.utils.AnNot.ObjPreferencesKeys.TARGET_RECENT_LANGUAGES_CODE_TRANSLATOR
import com.example.imagetranslater.utils.AnNot.ObjPreferencesKeys.TARGET_RECENT_LANGUAGES_TRANSLATOR
import com.example.imagetranslater.utils.AnNot.ObjPreferencesKeys.TARGET_RECENT_LANGUAGE_SELECTED_TRANSLATOR
import com.example.imagetranslater.utils.AppPreferences.funGetString
import com.example.imagetranslater.utils.Singleton.funCopy
import com.example.imagetranslater.utils.Singleton.funLaunchLanguagesActivity
import com.example.imagetranslater.utils.Singleton.funShare
import com.example.imagetranslater.utils.Singleton.toastLong

class ActivityTranslator : AppCompatActivity(), TranslatorCallBack {
    private lateinit var textViewSourceLang: TextView
    private lateinit var textViewTargetLang: TextView
    private lateinit var onlineTranslatorApi: OnlineTranslatorApi
    private lateinit var editTextInput: EditText
    private lateinit var textViewResult: TextView
    var previousLanguage = ""
    var sourceText = ""
    var text = ""
    private var first = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_translator)

        onlineTranslatorApi = OnlineTranslatorApi(this)


        textViewSourceLang = findViewById(R.id.textViewSourceLang)
        textViewTargetLang = findViewById(R.id.textViewTargetLang)
        editTextInput = findViewById(R.id.editTextInput)
        textViewResult = findViewById(R.id.textViewResult)
        val imageButtonCopy = findViewById<ImageButton>(R.id.imageButtonCopy)
        val imageButtonShare = findViewById<ImageButton>(R.id.imageButtonShare)
//        val imageButtonEdit = findViewById<ImageButton>(R.id.imageButtonEdit)
        val imageButtonCopy2 = findViewById<ImageButton>(R.id.imageButtonCopy2)
        val imageButtonShare2 = findViewById<ImageButton>(R.id.imageButtonShare2)

        sourceText = intent.extras!![SOURCE_TEXT].toString()
        text = intent.extras!![TEXT].toString()
        val date = intent.extras!![DATE].toString()
        editTextInput.setText(sourceText)
        textViewResult.text = text

        textViewSourceLang.setOnClickListener {
            funLaunchLanguagesActivity(
                LANGUAGE_ONLINE,
                SOURCE_RECENT_LANGUAGES_CODE_TRANSLATOR,
                SOURCE_RECENT_LANGUAGES_TRANSLATOR,
                SOURCE_RECENT_LANGUAGE_SELECTED_TRANSLATOR,
                SOURCE_LANGUAGE_SELECTED_CODE_TRANSLATOR,
                SOURCE_LANGUAGE_SELECTED_NAME_TRANSLATOR,
                ActivityLanguages()
            )
        }
        textViewTargetLang.setOnClickListener {
            previousLanguage = funGetString(
                TARGET_LANGUAGE_SELECTED_NAME_TRANSLATOR, "English"
            )
            funLaunchLanguagesActivity(
                LANGUAGE_ONLINE,
                TARGET_RECENT_LANGUAGES_CODE_TRANSLATOR,
                TARGET_RECENT_LANGUAGES_TRANSLATOR,
                TARGET_RECENT_LANGUAGE_SELECTED_TRANSLATOR,
                TARGET_LANGUAGE_SELECTED_CODE_TRANSLATOR,
                TARGET_LANGUAGE_SELECTED_NAME_TRANSLATOR,
                ActivityLanguages()
            )
        }
        imageButtonCopy.setOnClickListener {
            funCopy(editTextInput.text.toString())
        }
        imageButtonShare.setOnClickListener {
            if (editTextInput.text.toString().isNotBlank()) {
                funShare(editTextInput.text.toString())
            }
        }
        imageButtonCopy2.setOnClickListener {
            funCopy(textViewResult.text.toString())
        }
        imageButtonShare2.setOnClickListener {
            if (textViewResult.text.toString().isNotBlank()) {
                funShare(textViewResult.text.toString())
            }
        }
    }

    override fun onResume() {
        super.onResume()
        textViewSourceLang.text = funGetString(
            SOURCE_LANGUAGE_SELECTED_NAME_TRANSLATOR, "English"
        )
        val tar = funGetString(
            TARGET_LANGUAGE_SELECTED_NAME_TRANSLATOR, "English"
        )

        textViewTargetLang.text = tar
        if (!first) {
            if (previousLanguage != tar) {
                val source = funGetString(
                    SOURCE_LANGUAGE_SELECTED_CODE_TRANSLATOR,
                    "en"
                )
                val target = funGetString(
                    TARGET_LANGUAGE_SELECTED_CODE_TRANSLATOR,
                    "en"
                )
                if (editTextInput.text.toString().isNotBlank()) {
                    onlineTranslatorApi.execute(
                        editTextInput.text.toString(), source, target,
                        this
                    )
                }
            }
        }
        first = false
    }


    override fun call(result: String, source: String) {
        if (result.isNotBlank()) {
            textViewResult.text = result
        }

    }

    override fun failure(messages: String) {
        toastLong("Translation failed")
    }
}