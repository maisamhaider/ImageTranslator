package com.example.imagetranslater.ui.translator

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.imagetranslater.R
import com.example.imagetranslater.api.OnlineTranslatorApi
import com.example.imagetranslater.databinding.ActivityTranslatorBinding
import com.example.imagetranslater.interfaces.TranslatorCallBack
import com.example.imagetranslater.ui.ActivityLanguages
import com.example.imagetranslater.utils.AnNot.ObjIntentKeys.DATE
import com.example.imagetranslater.utils.AnNot.ObjIntentKeys.LANGUAGE_ONLINE
import com.example.imagetranslater.utils.AnNot.ObjIntentKeys.SOURCE_TEXT
import com.example.imagetranslater.utils.AnNot.ObjIntentKeys.TEXT
import com.example.imagetranslater.utils.AnNot.ObjPreferencesKeys.SOURCE_LANGUAGE_SELECTED_CODE_TRANSLATOR
import com.example.imagetranslater.utils.AnNot.ObjPreferencesKeys.SOURCE_LANGUAGE_SELECTED_NAME_TRANSLATOR
import com.example.imagetranslater.utils.AnNot.ObjPreferencesKeys.SOURCE_RECENT_LANGUAGES_CODE_TRANSLATOR
import com.example.imagetranslater.utils.AnNot.ObjPreferencesKeys.SOURCE_RECENT_LANGUAGE_SELECTED_TRANSLATOR
import com.example.imagetranslater.utils.AnNot.ObjPreferencesKeys.TARGET_LANGUAGE_SELECTED_CODE_TRANSLATOR
import com.example.imagetranslater.utils.AnNot.ObjPreferencesKeys.TARGET_LANGUAGE_SELECTED_NAME_TRANSLATOR
import com.example.imagetranslater.utils.AnNot.ObjPreferencesKeys.TARGET_RECENT_LANGUAGES_CODE_TRANSLATOR
import com.example.imagetranslater.utils.AnNot.ObjPreferencesKeys.TARGET_RECENT_LANGUAGE_SELECTED_TRANSLATOR
import com.example.imagetranslater.utils.AppPreferences.funGetString
import com.example.imagetranslater.utils.Singleton.funCopy
import com.example.imagetranslater.utils.Singleton.funLaunchLanguagesActivity
import com.example.imagetranslater.utils.Singleton.funShare
import com.example.imagetranslater.utils.Singleton.toastLong
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ActivityTranslator : AppCompatActivity(), TranslatorCallBack {
    private lateinit var onlineTranslatorApi: OnlineTranslatorApi
    var previousLanguage = ""
    var sourceText = ""
    var text = ""
    private var first = true

    lateinit var scope: CoroutineScope
    lateinit var binding: ActivityTranslatorBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_translator)
        onlineTranslatorApi = OnlineTranslatorApi(this)
        scope = CoroutineScope(Job())
        sourceText = intent.extras!![SOURCE_TEXT].toString()
        text = intent.extras!![TEXT].toString()
        val date = intent.extras!![DATE].toString()
        binding.editTextInput.setText(sourceText)
        binding.textViewResult.text = text

        binding.textViewSourceLang.setOnClickListener {
            funLaunchLanguagesActivity(
                LANGUAGE_ONLINE,
                SOURCE_RECENT_LANGUAGES_CODE_TRANSLATOR,
                SOURCE_RECENT_LANGUAGE_SELECTED_TRANSLATOR,
                SOURCE_LANGUAGE_SELECTED_CODE_TRANSLATOR,
                SOURCE_LANGUAGE_SELECTED_NAME_TRANSLATOR,
                ActivityLanguages()
            )
        }
        binding.textViewTargetLang.setOnClickListener {
            previousLanguage = funGetString(
                TARGET_LANGUAGE_SELECTED_NAME_TRANSLATOR, "English"
            )
            funLaunchLanguagesActivity(
                LANGUAGE_ONLINE,
                TARGET_RECENT_LANGUAGES_CODE_TRANSLATOR,
                TARGET_RECENT_LANGUAGE_SELECTED_TRANSLATOR,
                TARGET_LANGUAGE_SELECTED_CODE_TRANSLATOR,
                TARGET_LANGUAGE_SELECTED_NAME_TRANSLATOR,
                ActivityLanguages()
            )
        }
        binding.imageButtonCopy.setOnClickListener {
            funCopy(binding.editTextInput.text.toString())
        }
        binding.imageButtonShare.setOnClickListener {
            if (binding.editTextInput.text.toString().isNotBlank()) {
                funShare(binding.editTextInput.text.toString())
            }
        }
        binding.imageButtonCopy2.setOnClickListener {
            funCopy(binding.textViewResult.text.toString())
        }
        binding.imageButtonShare2.setOnClickListener {
            if (binding.textViewResult.text.toString().isNotBlank()) {
                funShare(binding.textViewResult.text.toString())
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.textViewSourceLang.text = funGetString(
            SOURCE_LANGUAGE_SELECTED_NAME_TRANSLATOR, "English"
        )
        val tar = funGetString(
            TARGET_LANGUAGE_SELECTED_NAME_TRANSLATOR, "English"
        )

        binding.textViewTargetLang.text = tar
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
                if (binding.editTextInput.text.toString().isNotBlank()) {
                    scope.launch(Dispatchers.IO) {
                        onlineTranslatorApi.execute(
                            binding.editTextInput.text.toString(), source, target,
                            this@ActivityTranslator
                        )
                    }

                }
            }
        }
        first = false
    }


    override fun call(result: String, source: String) {
        if (result.isNotBlank()) {
            binding.textViewResult.text = result
        }

    }

    override fun failure(messages: String) {
        toastLong("Translation failed")
    }
}