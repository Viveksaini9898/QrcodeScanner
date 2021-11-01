package com.qr.scanner.history

import android.app.Application
import androidx.lifecycle.LiveData
import com.qr.scanner.utils.subscribeOnBackground

class HistoryRepository(application: Application) {

    private var allHistory: LiveData<List<History>>? = null
    private var favorites: LiveData<List<History>>? = null
    private var generate: LiveData<List<History>>? = null

    private val historyDao = HistoryDao?.getInstance(application)

    init {
        allHistory = historyDao.getAllHistory()
        favorites = historyDao.getFavorites()
        generate = historyDao.getGenerate()
    }

    fun insert(history: History?) {
        subscribeOnBackground {
            historyDao.insert(history!!)
        }
    }

    fun delete(history: History?) {
        subscribeOnBackground {
            historyDao.delete(history!!)
        }
    }

    fun deleteAll(history: History?) {
        subscribeOnBackground {
            historyDao.deleteAllHistory()
        }
    }

    fun getAllHistory(): LiveData<List<History>>? {
        return allHistory!!
    }

    fun getFavorites(): LiveData<List<History>> {
        return favorites!!
    }

    fun getGenerate(): LiveData<List<History>> {
        return generate!!
    }

}