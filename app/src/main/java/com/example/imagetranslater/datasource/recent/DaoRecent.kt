package com.example.imagetranslater.datasource.recent

import androidx.room.*

@Dao
interface DaoRecent {
    @Insert
    fun insert(entityRecent: EntityRecent)

    @Update
    fun update(entityRecent: EntityRecent)

    @Delete
    fun delete(entityRecent: EntityRecent)

    @Query("DELETE FROM TABLE_RECENT WHERE id = :id")
    fun deleteRecent(id: Int)

    @Query("DELETE FROM TABLE_RECENT")
    fun deleteAllRecent()

//    @Query("SELECT * FROM TABLE_RECENT")
//    fun getAllRecent(): LiveData<List<EntityRecent>>

    @Query("SELECT * FROM TABLE_RECENT")
    fun getAllRecent(): List<EntityRecent>

    @Query("SELECT * FROM TABLE_RECENT ORDER BY id ASC LIMIT 1")
    fun getFirstRow(): EntityRecent

    @Query("SELECT COUNT() FROM TABLE_RECENT")
    fun entriesCount(): Int
}