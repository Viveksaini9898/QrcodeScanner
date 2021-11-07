package com.qr.scanner.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.qr.scanner.history.HistoryRepository
import com.qr.scanner.model.Result

class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = HistoryRepository(application)
    private val allHistory = repository.getAllHistory()
    private val favorites = repository.getFavorites()
    private val generate = repository.getGenerate()

    fun insert(result: Result?) {
        repository.insert(result)
    }

    fun insert(result: Result?,doNotSaveDuplicates:Boolean) {
        repository.insert(result,doNotSaveDuplicates)
    }

    fun update(result: Result?) {
        repository.update(result)
    }

    fun delete(result: Result?) {
        repository.delete(result)
    }

    fun deleteAll() {
        repository.deleteAll()
    }

    fun getAllHistory(): LiveData<List<Result>>? {
        return allHistory!!
    }

    fun getFavorite(): LiveData<List<Result>>? {
        return favorites!!
    }

    fun getGenerate(): LiveData<List<Result>>? {
        return generate!!
    }

}