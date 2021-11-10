package com.example.imagetranslater.datasource.pinned

import androidx.room.*

@Dao
interface DaoPinned {
    @Insert
    fun insert(entityPinned: EntityPinned)

    @Update
    fun update(entityPinned: EntityPinned)

    @Delete
    fun delete(entityPinned: EntityPinned)

    @Query("DELETE FROM TABLE_PINNED WHERE id = :id")
    fun deletePinned(id: Int)

    @Query("DELETE FROM TABLE_PINNED WHERE textImagePath = :imagePath")
    fun deletePinned(imagePath: String)

    @Query("DELETE FROM TABLE_PINNED")
    fun deleteAllPinned()

//    @Query("SELECT * FROM TABLE_PINNED")
//    fun getAllPinned(): LiveData<List<EntityPinned>>

    @Query("SELECT * FROM TABLE_PINNED")
    fun getAllPinned(): List<EntityPinned>

    @Query("SELECT * FROM TABLE_PINNED ORDER BY id ASC LIMIT 1")
    fun getFirstRow(): EntityPinned

    @Query("SELECT COUNT() FROM TABLE_PINNED WHERE text = :text")
    fun getCount(text: String): Int
}