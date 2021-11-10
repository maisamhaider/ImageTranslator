package com.example.imagetranslater.datasource.recentlanguages

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.imagetranslater.datasource.MyRoom
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RepositoryResultLanguages(application: Application) {

    private var room: MyRoom = MyRoom.getInstance(application)
    var daoRecentLanguages: DaoRecentLanguages = room.daoRecentLanguages()!!

    var results: LiveData<List<EntityRecentLanguages>> = daoRecentLanguages.getAllRL()

    suspend fun getFirstRow() = withContext(Dispatchers.IO) {
        daoRecentLanguages.getFirstRow()
    }

    fun funGetAll(): LiveData<List<EntityRecentLanguages>> {
        return daoRecentLanguages.getAllRL()
    }

    suspend fun isLangExists(code: String, name: String) = withContext(Dispatchers.IO) {
        daoRecentLanguages.isLangExists(code, name)
    }

    suspend fun entriesCount() = withContext(Dispatchers.IO) {
        daoRecentLanguages.entriesCount()
    }

    fun funInsert(result: EntityRecentLanguages) {
        Insert(result, daoRecentLanguages)
    }

    fun funDelete(result: EntityRecentLanguages) {
        Delete(result, daoRecentLanguages)
    }

    fun funDelete(int: Int) {
        DeleteById(int, daoRecentLanguages)
    }

    fun funDelete() {
        DeleteAll(daoRecentLanguages)
    }

    internal class Insert(result: EntityRecentLanguages, dao: DaoRecentLanguages) : Runnable {
        // to stop the thread
        private var exit: Boolean
        var t: Thread = Thread(this)
        private var result: EntityRecentLanguages
        private var dao: DaoRecentLanguages

        override fun run() {
            dao.insert(result)
        }

        // for stopping the thread
        fun stop() {
            exit = true
        }

        init {
            exit = false
            this.result = result
            this.dao = dao
            t.start() // Starting the thread
        }
    }

    internal class Delete(result: EntityRecentLanguages, dao: DaoRecentLanguages) : Runnable {
        // to stop the thread
        private var exit: Boolean
        var t: Thread = Thread(this)
        private var result: EntityRecentLanguages
        private var dao: DaoRecentLanguages

        override fun run() {
            dao.delete(result)
        }

        // for stopping the thread
        fun stop() {
            exit = true
        }

        init {
            exit = false
            this.result = result
            this.dao = dao
            t.start() // Starting the thread
        }
    }

    internal class DeleteById(var int: Int, dao: DaoRecentLanguages) : Runnable {
        // to stop the thread
        private var exit: Boolean
        var t: Thread = Thread(this)
        private var dao: DaoRecentLanguages

        override fun run() {
            dao.deleteRL(int)
        }

        // for stopping the thread
        fun stop() {
            exit = true
        }

        init {
            exit = false
            this.dao = dao
            t.start() // Starting the thread
        }
    }

    internal class DeleteAll(dao: DaoRecentLanguages) : Runnable {
        // to stop the thread
        private var exit: Boolean
        var t: Thread = Thread(this)
        private var dao: DaoRecentLanguages

        override fun run() {
            dao.deleteAllRL()
        }

        // for stopping the thread
        fun stop() {
            exit = true
        }

        init {
            exit = false
            this.dao = dao
            t.start() // Starting the thread
        }
    }


}