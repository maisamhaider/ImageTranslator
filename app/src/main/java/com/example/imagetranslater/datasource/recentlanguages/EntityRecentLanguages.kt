package com.example.imagetranslater.datasource.recentlanguages

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.imagetranslater.utils.AnNot.ObjRoomItems.TABLE_RECENT_LANGUAGES

@Entity(tableName = TABLE_RECENT_LANGUAGES)
class EntityRecentLanguages(code: String, name: String) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id = 0

    @ColumnInfo(name = "code")
    var code: String? = code

    @ColumnInfo(name = "name")
    var name: String? = name

}