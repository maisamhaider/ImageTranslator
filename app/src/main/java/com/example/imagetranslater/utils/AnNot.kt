package com.example.imagetranslater.utils

import android.net.Uri
import com.example.imagetranslater.model.ModelLanguage
import com.google.mlkit.vision.text.Text
import java.util.*

annotation class AnNot {


    object Image {
        lateinit var uri: Uri
        lateinit var vision: Text
        var FROM_GALLERY = false
        var translate = true
    }

    object ObjRoomItems {
        const val TABLE_PINNED = "TABLE_PINNED"
        const val PINNED = "PINNED"
        const val RECENT = "RECENT"
        const val TABLE_RECENT = "TABLE_RECENT"
        const val TABLE_RECENT_LANGUAGES = "TABLE_RECENT_LANGUAGES"
        const val TYPE = "TYPE"

    }

    object ObjIntentKeys {
        const val SOURCE_LANGUAGE_LIST = "SOURCE_LANGUAGE_LIST"
        const val WHICH_LANGUAGE_CODE = "WHICH_LANGUAGE_CODE"
        const val WHICH_LANGUAGE_NAME = "WHICH_LANGUAGE"
        const val WHICH_RECENT_LANGUAGE = "WHICH_RECENT_LANGUAGE"
        const val WHICH_RECENT_LANGUAGE_CODE_LIST = "WHICH_RECENT_LANGUAGE_CODE_LIST"


        const val ID = "ID"
        const val IMAGE_ORIGINAL = "IMAGE_ORIGINAL"
        const val IMAGE_RESULT = "IMAGE_RESULT"
        const val SHARE_IMAGE_RESULT = "SHARE_IMAGE_RESULT"
        const val SOURCE_TEXT = "SOURCE_TEXT"
        const val TEXT = "TEXT"
        const val SOURCE_LANGUAGE_NAME = "SOURCE_LANGUAGE_NAME"
        const val TARGET_LANGUAGE_NAME = "TARGET_LANGUAGE_NAME"
        const val SOURCE_LANGUAGE_CODE = "SOURCE_LANGUAGE_CODE"
        const val TARGET_LANGUAGE_CODE = "TARGET_LANGUAGE_CODE"
        const val DATE = "DATE"


        const val LANGUAGE_ONLINE = "LANGUAGE_ONLINE"
        const val LANGUAGE_CAMERA_SUPPORTED = "LANGUAGE_CAMERA_SUPPORTED"


    }

    object ObjNames {
        const val APP_PREFERENCES = "APP_PREFERENCES"
    }


    object ObjPreferencesKeys {

        const val SOURCE_RECENT_LANGUAGES_CODE_IMAGE_TRANSLATOR =
            "SOURCE_RECENT_LANGUAGES_CODE_IMAGE_TRANSLATOR"
         const val SOURCE_RECENT_LANGUAGE_SELECTED_IMAGE_TRANSLATOR =
            "SOURCE_RECENT_LANGUAGE_SELECTED_IMAGE_TRANSLATOR"
        const val SOURCE_LANGUAGE_SELECTED_CODE_IMAGE_TRANSLATOR =
            "SOURCE_LANGUAGE_SELECTED_CODE_IMAGE_TRANSLATOR"
        const val SOURCE_LANGUAGE_SELECTED_NAME_IMAGE_TRANSLATOR =
            "SOURCE_LANGUAGE_SELECTED_NAME_IMAGE_TRANSLATOR"

        const val TARGET_RECENT_LANGUAGES_CODE_IMAGE_TRANSLATOR =
            "TARGET_RECENT_LANGUAGES_CODE_IMAGE_TRANSLATOR"

        const val TARGET_RECENT_LANGUAGE_SELECTED_IMAGE_TRANSLATOR =
            "TARGET_RECENT_LANGUAGE_SELECTED_IMAGE_TRANSLATOR"
        const val TARGET_LANGUAGE_SELECTED_CODE_IMAGE_TRANSLATOR =
            "TARGET_LANGUAGE_SELECTED_CODE_IMAGE_TRANSLATOR"
        const val TARGET_LANGUAGE_SELECTED_NAME_IMAGE_TRANSLATOR =
            "TARGET_LANGUAGE_SELECTED_NAME_IMAGE_TRANSLATOR"

        const val SOURCE_RECENT_LANGUAGES_CODE_TRANSLATOR =
            "SOURCE_RECENT_LANGUAGES_CODE_TRANSLATOR"
        const val SOURCE_RECENT_LANGUAGE_SELECTED_TRANSLATOR =
            "SOURCE_RECENT_LANGUAGE_SELECTED_TRANSLATOR"
        const val SOURCE_LANGUAGE_SELECTED_CODE_TRANSLATOR =
            "SOURCE_LANGUAGE_SELECTED_CODE_TRANSLATOR"
        const val SOURCE_LANGUAGE_SELECTED_NAME_TRANSLATOR =
            "SOURCE_LANGUAGE_SELECTED_NAME_TRANSLATOR"

        const val TARGET_RECENT_LANGUAGES_CODE_TRANSLATOR =
            "TARGET_RECENT_LANGUAGES_CODE_TRANSLATOR"
         const val TARGET_RECENT_LANGUAGE_SELECTED_TRANSLATOR =
            "TARGET_RECENT_LANGUAGE_SELECTED_TRANSLATOR"
        const val TARGET_LANGUAGE_SELECTED_CODE_TRANSLATOR =
            "TARGET_LANGUAGE_SELECTED_CODE_TRANSLATOR"
        const val TARGET_LANGUAGE_SELECTED_NAME_TRANSLATOR =
            "TARGET_LANGUAGE_SELECTED_NAME_TRANSLATOR"

    }

    object ObjLists {
        private val LANGUAGES_CODES = listOf(
            "af", "ar", "be", "bg", "bn", "ca", "cs", "cy", "da",
            "de", "el", "en", "eo", "es", "et", "fa", "fi", "fr", "ga", "gl", "gu", "he", "hi",
            "hr", "ht", "hu", "id", "is", "it", "ja", "ka", "kn", "ko", "lt", "lv", "mk", "mr",
            "ms", "mt", "nl", "no", "pl", "pt", "ro", "ru", "sk", "sl", "sq", "sv", "sw", "ta",
            "te", "th", "tl", "tr", "uk", "ur", "vi", "zh"
        )
        private val LANGUAGES_CODES_ONLINE = listOf(
            "af", "sq", "am", "ar", "hy", "az", "eu", "be", "bn", "bs", "bg", "ca", "ceb", "zh-CN",
            "zh-TW", "co", "hr", "cs", "da", "nl", "en", "eo", "et", "fi", "fr", "fy", "gl", "ka",
            "de", "el", "gu", "ht", "ha", "haw", "he", "hi", "hmn", "hu", "is", "ig", "id", "ga",
            "it", "ja", "jv", "kn", "kk", "km", "rw", "ko", "ku", "ky", "lo", "lv", "lt", "lb",
            "mk", "mg", "ms", "ml", "mt", "mi", "mr", "mn", "my", "ne", "no", "ny", "or", "ps",
            "fa", "pl", "pt", "pa", "ro", "ru", "sm", "gd", "sr", "st", "sn", "sd", "si", "sk",
            "sl", "so", "es", "su", "sw", "sv", "tl", "tg", "ta", "tt", "te", "th", "tr", "tk",
            "uk", "ur", "ug", "uz", "vi", "cy", "xh", "yi", "yo", "zu"
        )

        private val LANGUAGES_NAMES = listOf(
            "Afrikaans", "Arabic", "Belarusian", "Bulgarian",
            "Bengali", "Catalan", "Czech", "Welsh", "Danish", "German", "Greek", "English",
            "Esperanto", "Spanish", "Estonian", "Persian", "Finnish", "French", "Irish", "Galician",
            "Gujarati", "Hebrew", "Hindi", "Croatian", "Haitian", "Hungarian", "Indonesian",
            "Icelandic", "Italian", "Japanese", "Georgian", "Kannada", "Korean", "Lithuanian",
            "Latvian", "Macedonian", "Marathi", "Malay", "Maltese", "Dutch", "Norwegian", "Polish",
            "Portuguese", "Romanian", "Russian", "Slovak", "Slovenian", "Albanian", "Swedish",
            "Swahili", "Tamil", "Telugu", "Thai", "Tagalog", "Turkish", "Ukrainian", "Urdu",
            "Vietnamese", "Chinese"
        )
        private val LANGUAGES_CODES_CAMERA_SUPPORTED = listOf(
            "af", "sq", "ca", "hr", "cs", "da", "nl", "en", "et", "tl", "fi", "fr", "de", "hu",
            "is", "id", "it", "lv", "lt", "ms", "no", "pl", "pt", "ro", "sr", "sk", "sl",
            "es", "sv", "tr", "vi"
        )
        private val LANGUAGES_NAMES_CAMERA_SUPPORTED = listOf(
            "Afrikaans",
            "Albanian",
            "Catalan",
            "Croatian",
            "Czech",
            "Danish",
            "Dutch",
            "English",
            "Estonian",
            "Filipino",
            "Finnish",
            "French",
            "German",
            "Hungarian",
            "Icelandic",
            "Indonesian",
            "Italian",
            "Latvian",
            "Lithuanian",
            "Malay",
            "Norwegian",
            "Polish",
            "Portuguese",
            "Romanian",
            "Serbian",
            "Slovak",
            "Slovenian",
            "Spanish",
            "Swedish",
            "Turkish",
            "Vietnamese"
        )


        private val LANGUAGES_NAMES_ONLINE = listOf(
            "Afrikaans",
            "Albanian",
            "Amharic",
            "Arabic",
            "Armenian",
            "Azerbaijani",
            "Basque",
            "Belarusian",
            "Bengali",
            "Bosnian",
            "Bulgarian",
            "Catalan",
            "Cebuano",
            "Chinese (Simplified)",
            "Chinese (Traditional)",
            "Corsican",
            "Croatian",
            "Czech",
            "Danish",
            "Dutch",
            "English",
            "Esperanto",
            "Estonian",
            "Finnish",
            "French",
            "Frisian",
            "Galician",
            "Georgian",
            "German",
            "Greek",
            "Gujarati",
            "Haitian Creole",
            "Hausa",
            "Hawaiian",
            "Hebrew",
            "Hindi",
            "Hmong",
            "Hungarian",
            "Icelandic",
            "Igbo",
            "Indonesian",
            "Irish",
            "Italian",
            "Japanese",
            "Javanese",
            "Kannada",
            "Kazakh",
            "Khmer",
            "Kinyarwanda",
            "Korean",
            "Kurdish",
            "Kyrgyz",
            "Lao",
            "Latvian",
            "Lithuanian",
            "Luxembourgish",
            "Macedonian",
            "Malagasy",
            "Malay",
            "Malayalam",
            "Maltese",
            "Maori",
            "Marathi",
            "Mongolian",
            "Myanmar (Burmese)",
            "Nepali",
            "Norwegian",
            "Nyanja (Chichewa)",
            "Odia (Oriya)",
            "Pashto",
            "Persian",
            "Polish",
            "Portuguese (Portugal, Brazil)",
            "Punjabi",
            "Romanian",
            "Russian",
            "Samoan",
            "Scots Gaelic",
            "Serbian",
            "Sesotho",
            "Shona",
            "Sindhi",
            "Sinhala (Sinhalese)",
            "Slovak",
            "Slovenian",
            "Somali",
            "Spanish",
            "Sundanese",
            "Swahili",
            "Swedish",
            "Tagalog (Filipino)",
            "Tajik",
            "Tamil",
            "Tatar",
            "Telugu",
            "Thai",
            "Turkish",
            "Turkmen",
            "Ukrainian",
            "Urdu",
            "Uyghur",
            "Uzbek",
            "Vietnamese",
            "Welsh",
            "Xhosa",
            "Yiddish",
            "Yoruba",
            "Zulu"
        )

        fun funGetLanguagesListOffline(): MutableList<ModelLanguage> {
            var model: ModelLanguage?
            val list: MutableList<ModelLanguage> = ArrayList()
            var i = 0
            LANGUAGES_CODES.forEach {
                model = ModelLanguage(it, LANGUAGES_NAMES[i])
                list.add(model!!)
                i++
            }
            return list
        }


        fun funGetLanguagesListOnline(): MutableList<ModelLanguage> {
            var model: ModelLanguage?
            val list: MutableList<ModelLanguage> = ArrayList()
            var i = 0
            LANGUAGES_CODES_ONLINE.forEach {
                model = ModelLanguage(it, LANGUAGES_NAMES_ONLINE[i])
                list.add(model!!)
                i++
            }
            return list
        }

        fun funGetLanguagesListCameraSupported(): MutableList<ModelLanguage> {
            var model: ModelLanguage?
            val list: MutableList<ModelLanguage> = ArrayList()
            var i = 0
            LANGUAGES_CODES_CAMERA_SUPPORTED.forEach {
                model = ModelLanguage(it, LANGUAGES_NAMES_CAMERA_SUPPORTED[i])
                list.add(model!!)
                i++
            }
            return list
        }


    }

}

