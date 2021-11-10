package com.example.imagetranslater.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.imagetranslater.datasource.pinned.EntityPinned
import com.example.imagetranslater.datasource.pinned.RepositoryPinned
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class VMPinned(application: Application) : AndroidViewModel(application) {

    private val repositoryPinned = RepositoryPinned(application)
//    var results: LiveData<List<EntityRecent>> = repositoryRecent.results

    fun funInsert(result: EntityPinned) {
        repositoryPinned.funInsert(result);
    }

    fun funDelete(result: EntityPinned) {
        repositoryPinned.funDelete(result)
    }

    fun funDelete(int: Int) {
        repositoryPinned.funDelete(int)
    }
    fun funDelete(imagePath: String) {
        repositoryPinned.funDelete(imagePath)
    }

    fun funDelete() {
        repositoryPinned.funDelete()
    }


    suspend fun funGetAll(): List<EntityPinned> {
        return repositoryPinned.funGetAll()
    }

    suspend fun entriesCount(text: String) = withContext(Dispatchers.IO) {
        repositoryPinned.getCount(text)
    }

}