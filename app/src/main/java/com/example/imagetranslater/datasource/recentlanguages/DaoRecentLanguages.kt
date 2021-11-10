package com.example.imagetranslater.datasource.recentlanguages

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface DaoRecentLanguages {
    @Insert
    fun insert(entityRecentLanguages: EntityRecentLanguages)

    @Update
    fun update(entityRecentLanguages: EntityRecentLanguages)

    @Delete
    fun delete(entityRecentLanguages: EntityRecentLanguages)

    @Query("DELETE FROM TABLE_RECENT_LANGUAGES WHERE id = :id")
    fun deleteRL(id: Int)

    @Query("DELETE FROM TABLE_RECENT_LANGUAGES")
    fun deleteAllRL()

    @Query("SELECT * FROM TABLE_RECENT_LANGUAGES")
    fun getAllRL(): LiveData<List<EntityRecentLanguages>>

    @Query("SELECT * FROM TABLE_RECENT_LANGUAGES ORDER BY id ASC LIMIT 1")
    fun getFirstRow(): EntityRecentLanguages

    @Query("SELECT * FROM TABLE_RECENT_LANGUAGES WHERE code = :code AND name =:name")
    fun isLangExists(code: String, name: String): Int

    @Query("SELECT COUNT() FROM TABLE_RECENT_LANGUAGES")
    fun entriesCount(): Int
}