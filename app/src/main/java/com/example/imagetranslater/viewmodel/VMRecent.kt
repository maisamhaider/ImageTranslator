package com.example.imagetranslater.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.imagetranslater.datasource.recent.EntityRecent
import com.example.imagetranslater.datasource.recent.RepositoryRecent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class VMRecent(application: Application) : AndroidViewModel(application) {

    private val repositoryRecent = RepositoryRecent(application)
//    var results: LiveData<List<EntityRecent>> = repositoryRecent.results

    suspend fun funInsert(result: EntityRecent) {
        repositoryRecent.funInsert(result)
    }

    fun funDelete(result: EntityRecent) {
        repositoryRecent.funDelete(result)
    }

    fun funDelete(int: Int) {
        repositoryRecent.funDelete(int)
    }

    fun funDelete() {
        repositoryRecent.funDelete()
    }


//    fun funGetAll(): LiveData<List<EntityRecent>> {
//        return repositoryRecent.funGetAll()
//    }

    suspend fun funGetAll(): List<EntityRecent> {
        return repositoryRecent.funGetAll()
    }

    suspend fun entriesCount() = withContext(Dispatchers.IO) {
        repositoryRecent.entriesCount()
    }

}