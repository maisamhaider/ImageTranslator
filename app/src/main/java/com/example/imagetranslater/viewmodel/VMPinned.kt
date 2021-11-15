package com.example.imagetranslater.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.imagetranslater.datasource.pinned.EntityPinned
import com.example.imagetranslater.datasource.pinned.RepositoryPinned
import com.example.imagetranslater.datasource.recent.EntityRecent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class VMPinned(application: Application) : AndroidViewModel(application) {

    private val repositoryPinned = RepositoryPinned(application)
    private var _results: LiveData<List<EntityRecent>> = MutableLiveData()

    val retrieve get() = _results

    fun funInsert(result: EntityPinned) {
        repositoryPinned.funInsert(result)

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