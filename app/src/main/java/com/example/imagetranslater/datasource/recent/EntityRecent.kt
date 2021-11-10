package com.example.imagetranslater.datasource.recent

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.imagetranslater.utils.AnNot.ObjRoomItems.TABLE_RECENT

@Entity(tableName = TABLE_RECENT)
class EntityRecent(
    title: String,
    sourceText: String,
    text: String,
    sourceLanguageCode: String,
    targetLanguageCode: String,
    sourceLanguageName: String,
    targetLanguageName: String,
    imagePath: String,
    textImagePath: String,
    date: String,
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id = 0

    @ColumnInfo(name = "title")
    var title: String? = title

    @ColumnInfo(name = "sourceText")
    var sourceText: String? = sourceText

    @ColumnInfo(name = "text")
    var text: String? = text

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

    @ColumnInfo(name = "date")
    var date: String? = date

}