package com.example.imagetranslater.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.imagetranslater.datasource.recentlanguages.EntityRecentLanguages
import com.example.imagetranslater.datasource.recentlanguages.RepositoryResultLanguages
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ViewModelResultLanguages(application: Application) : AndroidViewModel(application) {

    private val repositoryResultLanguages = RepositoryResultLanguages(application)
    var results: LiveData<List<EntityRecentLanguages>> = repositoryResultLanguages.results

    fun funInsert(result: EntityRecentLanguages) {
        repositoryResultLanguages.funInsert(result);
    }

    fun funDelete(result: EntityRecentLanguages) {
        repositoryResultLanguages.funDelete(result)
    }

    fun funDelete(int: Int) {
        repositoryResultLanguages.funDelete(int)
    }

    fun funDelete() {
        repositoryResultLanguages.funDelete()
    }

    suspend fun getFirstRow(): EntityRecentLanguages = withContext(Dispatchers.IO) {
        repositoryResultLanguages.getFirstRow()
    }

    fun funGetAll(): LiveData<List<EntityRecentLanguages>> {
        return repositoryResultLanguages.funGetAll()
    }

    suspend fun isLangExists(code: String, name: String) = withContext(Dispatchers.IO) {
        repositoryResultLanguages.isLangExists(code, name)
    }

    suspend fun entriesCount() = withContext(Dispatchers.IO) {
        repositoryResultLanguages.entriesCount()
    }

}