package com.example.imagetranslater.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.imagetranslater.utils.AnNot.ObjIntentKeys.LANGUAGE_CAMERA_SUPPORTED
import com.example.imagetranslater.utils.AnNot.ObjIntentKeys.LANGUAGE_ONLINE
import com.example.imagetranslater.utils.AnNot.ObjIntentKeys.SOURCE_LANGUAGE_LIST
import com.example.imagetranslater.utils.AnNot.ObjIntentKeys.WHICH_LANGUAGE_CODE
import com.example.imagetranslater.utils.AnNot.ObjIntentKeys.WHICH_LANGUAGE_NAME
import com.example.imagetranslater.utils.AnNot.ObjIntentKeys.WHICH_RECENT_LANGUAGE
import com.example.imagetranslater.utils.AnNot.ObjIntentKeys.WHICH_RECENT_LANGUAGE_CODE_LIST
import com.example.imagetranslater.utils.AnNot.ObjIntentKeys.WHICH_RECENT_LANGUAGE_LIST
import com.example.imagetranslater.utils.AnNot.ObjLists.funGetLanguagesListCameraSupported
import com.example.imagetranslater.utils.AnNot.ObjLists.funGetLanguagesListOffline
import com.example.imagetranslater.utils.AnNot.ObjLists.funGetLanguagesListOnline
import com.example.imagetranslater.utils.AppPreferences.funGetString
import com.example.imagetranslater.R
import com.example.imagetranslater.utils.Singleton.toastLong
import com.example.imagetranslater.adapter.AdapterLanguages
import com.example.imagetranslater.adapter.AdapterRecentLanguages
import com.example.imagetranslater.viewmodel.ViewModelResultLanguages
import com.example.speechtotexttranslator.interfeces.CallBack
import com.example.speechtotexttranslator.interfeces.LoadingCallBack


class ActivityLanguages : AppCompatActivity(), CallBack, LoadingCallBack {
    private var sourceLanguagesList: String? = null
    private var recentLanguagesCodeList: String? = null
    private var recentLanguagesKey: String? = null
    private var recentLanguageKey: String? = null
    private var languageCodeKey: String? = null
    private var languageNameKey: String? = null
    lateinit var viewView: ViewModelResultLanguages

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_languages)
        funRunCode()
    }

    private fun funRunCode() {
        //class object
//        viewView = ViewModelProvider(
//            this,
//            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
//        )[ViewModelResultLanguages::class.java]
        viewView = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[ViewModelResultLanguages::class.java]

        sourceLanguagesList = intent.getStringExtra(SOURCE_LANGUAGE_LIST)
        recentLanguagesCodeList = intent.getStringExtra(WHICH_RECENT_LANGUAGE_CODE_LIST)
        recentLanguagesKey = intent.getStringExtra(WHICH_RECENT_LANGUAGE_LIST)!!
        recentLanguageKey = intent.getStringExtra(WHICH_RECENT_LANGUAGE)!!
        languageCodeKey = intent.getStringExtra(WHICH_LANGUAGE_CODE)!!
        languageNameKey = intent.getStringExtra(WHICH_LANGUAGE_NAME)!!

        funLoadLanguagesInRecyclerView(
            viewView,
            sourceLanguagesList!!,
            recentLanguagesCodeList!!,
            recentLanguagesKey!!,
            recentLanguageKey!!,
            languageCodeKey,
            languageNameKey
        )
        funRecentLanguagesInRecyclerView(
            sourceLanguagesList!!,
            recentLanguagesCodeList!!,
            recentLanguagesKey,
            recentLanguageKey,
            languageCodeKey,
            languageNameKey
        )
    }

    private fun funLoadLanguagesInRecyclerView(
        viewModel: ViewModelResultLanguages,
        sourceLanguagesList: String,
        recentLanguagesCodeList: String,
        recentLanguagesKey: String,
        recentLanguageKey: String,
        sourceLanguageCodeKey: String?,
        targetLanguageNameKey: String?
    ) {
        val recyclerViewLanguages: RecyclerView = findViewById(R.id.recyclerViewAllLanguages)
        val searchViewLanguage: SearchView = findViewById(R.id.searchViewLanguage)
        val lm = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerViewLanguages.layoutManager = lm


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
            recentLanguagesCodeList,
            recentLanguagesKey,
            recentLanguageKey,
            sourceLanguageCodeKey,
            targetLanguageNameKey
        )

        recyclerViewLanguages.adapter = adapterLanguages
        adapterLanguages.funSetCallBack(this)

        searchViewLanguage.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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
        sourceLanguagesList: String,
        recentLanguagesCodeList: String,
        recentLanguagesKey: String?,
        recentLanguageKey: String?,
        languageCodeKey: String?,
        languageNameKey: String?
    ) {

        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewRecentLanguages)
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

        recyclerView.layoutManager = lm
        recyclerView.adapter = adapterLanguages
    }


    override fun call(call: Boolean?) {
        finish()
    }

    override fun dialogDismiss(value: Boolean) {
    }

}