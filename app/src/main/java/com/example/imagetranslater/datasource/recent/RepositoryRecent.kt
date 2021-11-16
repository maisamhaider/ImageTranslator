package com.example.imagetranslater.datasource.recent

import android.app.Application
import com.example.imagetranslater.datasource.MyRoom
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RepositoryRecent(application: Application) {

    private var room: MyRoom = MyRoom.getInstance(application)
    var daoRecent: DaoRecent = room.daoRecent()!!

//    var results: LiveData<List<EntityRecent>> = daoRecent.getAllRecent()

    suspend fun getFirstRow() = withContext(Dispatchers.IO) {
        daoRecent.getFirstRow()
    }

    suspend fun funGetAll() = withContext(Dispatchers.IO) {
        daoRecent.getAllRecent()
    }

    suspend fun entriesCount() = withContext(Dispatchers.IO) {
        daoRecent.entriesCount()
    }

    suspend fun funInsert(result: EntityRecent) = withContext(Dispatchers.IO) {
        daoRecent.insert(result)
    }

    fun funDelete(result: EntityRecent) {
        Delete(result, daoRecent)
    }

    fun funDelete(int: Int) {
        DeleteById(int, daoRecent)
    }

    fun funDelete() {
        DeleteAll(daoRecent)
    }


    internal class Delete(result: EntityRecent, dao: DaoRecent) : Runnable {
        // to stop the thread
        private var exit: Boolean
        private var t: Thread = Thread(this)
        private var result: EntityRecent
        private var dao: DaoRecent

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

    internal class DeleteById(var int: Int, dao: DaoRecent) : Runnable {
        // to stop the thread
        private var exit: Boolean
        var t: Thread = Thread(this)
        private var dao: DaoRecent

        override fun run() {
            dao.deleteRecent(int)
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

    internal class DeleteAll(dao: DaoRecent) : Runnable {
        // to stop the thread
        private var exit: Boolean
        var t: Thread = Thread(this)
        private var dao: DaoRecent

        override fun run() {
            dao.deleteAllRecent()
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