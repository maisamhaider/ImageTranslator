package com.example.imagetranslater.datasource.pinned

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.imagetranslater.utils.AnNot.ObjRoomItems.TABLE_PINNED

@Entity(tableName = TABLE_PINNED)
class EntityPinned(
    title: String,
    sourceText: String,
    text: String,
    sourceLanguageCode: String,
    targetLanguageCode: String,
    sourceLanguageName: String,
    targetLanguageName: String,
    imagePath: String,
    textImagePath: String,
    shareImagePath: String,
    date: String,
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id = 0

    @ColumnInfo(name = "title")
    var title: String? = title

    @ColumnInfo(name = "text")
    var text: String? = text

    @ColumnInfo(name = "sourceText")
    var sourceText: String? = sourceText

    @ColumnInfo(name = "sourceLanguageCode")
    var sourceLanguageCode: String? = sourceLanguageCode

    @ColumnInfo(name = "targetLanguageCode")
    var targetLanguageCode: String? = targetLanguageCode

    @ColumnInfo(name = "sourceLanguageName")
    var sourceLanguageName: String? = sourceLanguageName

    @ColumnInfo(name = "targetLanguageName")
    var targetLanguageName: String? = targetLanguageName

    @ColumnInfo(name = "imagePath")
    var imagePath: String? = imagePath

    @ColumnInfo(name = "textImagePath")
    var textImagePath: String? = textImagePath

    @ColumnInfo(name = "shareImagePath")
    var shareImagePath: String? = shareImagePath

    @ColumnInfo(name = "date")
    var date: String? = date

}