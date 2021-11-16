package com.example.imagetranslater.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.imagetranslater.R
import com.example.imagetranslater.adapter.AdapterLanguages
import com.example.imagetranslater.adapter.AdapterRecentLanguages
import com.example.imagetranslater.databinding.ActivityLanguagesBinding
import com.example.imagetranslater.interfaces.DeleteCallBack
import com.example.imagetranslater.interfaces.LoadingCallBack
import com.example.imagetranslater.utils.AnNot.ObjIntentKeys.LANGUAGE_CAMERA_SUPPORTED
import com.example.imagetranslater.utils.AnNot.ObjIntentKeys.LANGUAGE_ONLINE
import com.example.imagetranslater.utils.AnNot.ObjIntentKeys.SOURCE_LANGUAGE_LIST
import com.example.imagetranslater.utils.AnNot.ObjIntentKeys.WHICH_LANGUAGE_CODE
import com.example.imagetranslater.utils.AnNot.ObjIntentKeys.WHICH_LANGUAGE_NAME
import com.example.imagetranslater.utils.AnNot.ObjIntentKeys.WHICH_RECENT_LANGUAGE
import com.example.imagetranslater.utils.AnNot.ObjLists.funGetLanguagesListCameraSupported
import com.example.imagetranslater.utils.AnNot.ObjLists.funGetLanguagesListOffline
import com.example.imagetranslater.utils.AnNot.ObjLists.funGetLanguagesListOnline
import com.example.imagetranslater.utils.AppPreferences.funGetString
import com.example.imagetranslater.utils.Singleton.toastLong
import com.example.imagetranslater.viewmodel.ViewModelResultLanguages


class ActivityLanguages : AppCompatActivity(), DeleteCallBack, LoadingCallBack {
    private var sourceLanguagesList: String? = null
    private var recentLanguageKey: String? = null
    private var languageCodeKey: String? = null
    private var languageNameKey: String? = null
    private val viewView: ViewModelResultLanguages by viewModels()

    lateinit var binding: ActivityLanguagesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_languages)
        funRunCode()
    }

    private fun funRunCode() {
        sourceLanguagesList = intent.getStringExtra(SOURCE_LANGUAGE_LIST)
        recentLanguageKey = intent.getStringExtra(WHICH_RECENT_LANGUAGE)!!
        languageCodeKey = intent.getStringExtra(WHICH_LANGUAGE_CODE)!!
        languageNameKey = intent.getStringExtra(WHICH_LANGUAGE_NAME)!!

        funLoadLanguagesInRecyclerView(
            viewView,
            sourceLanguagesList!!,
            recentLanguageKey!!,
            languageCodeKey,
            languageNameKey
        )
        funRecentLanguagesInRecyclerView(
            recentLanguageKey,
            languageCodeKey,
            languageNameKey
        )
    }

    private fun funLoadLanguagesInRecyclerView(
        viewModel: ViewModelResultLanguages,
        sourceLanguagesList: String,
        recentLanguageKey: String,
        sourceLanguageCodeKey: String?,
        targetLanguageNameKey: String?
    ) {
        val lm = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.includedAllLang.recyclerViewAllLanguages.layoutManager = lm


        val adapterLanguages = AdapterLanguages(
            this,
            viewModel,
            list = when (sourceLanguagesList) {
                LANGUAGE_ONLINE -> {
                    funGetLanguagesListOnline()
                }
                LANGUAGE_CAMERA_SUPPORTED -> {
                    funGetLanguagesListCameraSupported()
                }
                else -> {
                    funGetLanguagesListOffline()
                }
            },
            recentLanguageKey,
            sourceLanguageCodeKey,
            targetLanguageNameKey
        )

        binding.includedAllLang.recyclerViewAllLanguages.adapter = adapterLanguages
        adapterLanguages.funSetCallBack(this)

        binding.searchViewLanguage.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                adapterLanguages.filter.filter(newText)
                return true
            }
        })
    }

    private fun funRecentLanguagesInRecyclerView(
        recentLanguageKey: String?,
        languageCodeKey: String?,
        languageNameKey: String?
    ) {

        val lm = LinearLayoutManager(this)
        lm.orientation = LinearLayoutManager.VERTICAL

        val selectedLanguage: String = funGetString(recentLanguageKey!!, "English")

        val adapterLanguages = AdapterRecentLanguages(
            context = this,
            viewView,
            selectedRecentLanguage = selectedLanguage,
            callBack = this,
            recentLanguageKey = recentLanguageKey,
            languageCodeKey = languageCodeKey,
            languageNameKey = languageNameKey
        )

        viewView.funGetAll().observe(this, {
            if (it.isNullOrEmpty()) {
                toastLong("no recent languages found")
            } else {
                adapterLanguages.submitList(it)
            }
        })

        binding.includedRecentLang.recyclerViewRecentLanguages.layoutManager = lm
        binding.includedRecentLang.recyclerViewRecentLanguages.adapter = adapterLanguages
    }


    override fun call(id: Int) {
        finish()
    }

    override fun dialogDismiss(value: Boolean) {
    }

}