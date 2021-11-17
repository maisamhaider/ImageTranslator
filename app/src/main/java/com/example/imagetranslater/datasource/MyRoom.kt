package com.example.imagetranslater.datasource

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.imagetranslater.datasource.pinned.DaoPinned
import com.example.imagetranslater.datasource.pinned.EntityPinned
import com.example.imagetranslater.datasource.recent.DaoRecent
import com.example.imagetranslater.datasource.recent.EntityRecent
import com.example.imagetranslater.datasource.recentlanguages.DaoRecentLanguages
import com.example.imagetranslater.datasource.recentlanguages.EntityRecentLanguages

@Database(
    entities = [EntityRecentLanguages::class, EntityRecent::class,
        EntityPinned::class], version = 2, exportSchema = false
)
abstract class MyRoom : RoomDatabase() {
    abstract fun daoRecentLanguages(): DaoRecentLanguages?
    abstract fun daoRecent(): DaoRecent?
    abstract fun daoPinned(): DaoPinned?

    companion object {
        @JvmStatic
        var instance: MyRoom? = null

        @JvmStatic
        @Synchronized
        fun getInstance(context: Context): MyRoom {
            if (instance == null) {
                instance = Room.databaseBuilder(context, MyRoom::class.java, "DATABASE_ROOM")
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return instance!!
        }
    }


}