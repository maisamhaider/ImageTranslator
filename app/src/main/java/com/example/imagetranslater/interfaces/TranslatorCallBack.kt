package com.example.imagetranslater.interfaces

interface TranslatorCallBack {
    fun call(result: String, source: String)
    fun failure(messages: String)
}