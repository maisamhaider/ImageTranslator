package com.example.imagetranslater.api

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.example.imagetranslater.interfaces.TranslatorCallBack
import cz.msebera.android.httpclient.HttpResponse
import cz.msebera.android.httpclient.StatusLine
import cz.msebera.android.httpclient.client.HttpClient
import cz.msebera.android.httpclient.client.methods.HttpGet
import cz.msebera.android.httpclient.client.methods.HttpUriRequest
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder
import org.json.JSONArray
import java.io.ByteArrayOutputStream
import java.net.URLEncoder
import java.util.concurrent.Executor
import java.util.concurrent.Executors


class OnlineTranslatorApi(val context: Context) {

    fun execute(
        text: String, sourceCode: String, targetCode: String,
        translatorCallBack: TranslatorCallBack
    ) {
        val executor: Executor = Executors.newSingleThreadExecutor()

        executor.execute {
            try {
                val encoded: String = URLEncoder.encode(text, "utf-8")
                val stringBuilder = StringBuilder()
                stringBuilder.append("https://translate.googleapis.com/translate_a/single?client=gtx&sl=")
                    .append(sourceCode).append("&tl=").append(targetCode).append("&dt=t&q=")
                    .append(encoded)

                val client: HttpClient = HttpClientBuilder.create().build()
                val httpUriRequest: HttpUriRequest = HttpGet(stringBuilder.toString())
                val response: HttpResponse = client.execute(httpUriRequest)

                val statusLine: StatusLine = response.statusLine
                if (statusLine.statusCode == 200) {
                    val byteArrayOutputStream = ByteArrayOutputStream()

                    response.entity.writeTo(byteArrayOutputStream)

                    val byteArrayOutputStream2 = byteArrayOutputStream.toString()
                    byteArrayOutputStream.close()

                    val jSONArray = JSONArray(byteArrayOutputStream2).getJSONArray(0)
                    var result = ""

                    for (i in 0 until jSONArray.length()) {
                        val jSONArray2 = jSONArray.getJSONArray(i)
                        val sb2 = StringBuilder()
                        sb2.append(result)
                        sb2.append(jSONArray2[0].toString())
                        result = sb2.toString()
                    }
                    Handler(Looper.getMainLooper()).post {
                        translatorCallBack.call(result, text)
                    }

                } else {
                    Handler(Looper.getMainLooper()).post {
                        translatorCallBack.failure(statusLine.reasonPhrase)
                    }
                }
                response.entity.content.close()

            } catch (e: Exception) {
                Handler(Looper.getMainLooper()).post {
                    translatorCallBack.failure(e.message.toString())
                }
            }

        }

    }
}