package com.example.imagetranslater.datasource.pinned

import android.app.Application
import com.example.imagetranslater.datasource.MyRoom
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RepositoryPinned(application: Application) {

    private var room: MyRoom = MyRoom.getInstance(application)
    var daoPinned: DaoPinned = room.daoPinned()!!

//    var results: LiveData<List<EntityPinned>> = daoPinned.getAllPinned()

    suspend fun getFirstRow() = withContext(Dispatchers.IO) {
        daoPinned.getFirstRow()
    }

    suspend fun getCount(text: String) = withContext(Dispatchers.IO) {
        daoPinned.getCount(text)
    }

    suspend fun funGetAll(): List<EntityPinned> = withContext(Dispatchers.IO) {
        daoPinned.getAllPinned()
    }

    fun funInsert(result: EntityPinned) {
        Insert(result, daoPinned)
    }

    fun funDelete(result: EntityPinned) {
        Delete(result, daoPinned)
    }

    fun funDelete(int: Int) {
        DeleteById(int, daoPinned)
    }

    fun funDelete(imagePath: String) {
        DeleteByPath(imagePath, daoPinned)
    }

    fun funDelete() {
        DeleteAll(daoPinned)
    }

    internal class Insert(result: EntityPinned, dao: DaoPinned) : Runnable {
        // to stop the thread
        private var exit: Boolean
        var t: Thread = Thread(this)
        private var result: EntityPinned
        private var dao: DaoPinned

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

    internal class Delete(result: EntityPinned, dao: DaoPinned) : Runnable {
        // to stop the thread
        private var exit: Boolean
        private var t: Thread = Thread(this)
        private var result: EntityPinned
        private var dao: DaoPinned

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

    internal class DeleteByPath(var path: String, dao: DaoPinned) : Runnable {
        // to stop the thread
        private var exit: Boolean
        var t: Thread = Thread(this)
        private var dao: DaoPinned

        override fun run() {
            dao.deletePinned(path)
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

    internal class DeleteById(var int: Int, dao: DaoPinned) : Runnable {
        // to stop the thread
        private var exit: Boolean
        var t: Thread = Thread(this)
        private var dao: DaoPinned

        override fun run() {
            dao.deletePinned(int)
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

    internal class DeleteAll(dao: DaoPinned) : Runnable {
        // to stop the thread
        private var exit: Boolean
        var t: Thread = Thread(this)
        private var dao: DaoPinned

        override fun run() {
            dao.deleteAllPinned()
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