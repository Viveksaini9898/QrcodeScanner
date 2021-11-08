package com.qr.scanner.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.qr.scanner.history.HistoryRepository
import com.qr.scanner.model.Result
import com.qr.scanner.utils.subscribeOnBackground

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


    /*fun getApps(context: Context?): LiveData<List<ResolveInfo>>? {
        subscribeOnBackground {
           getAllApps = apps(context)
        }
        return apps(context) as L
    }*/

    private fun apps(context: Context?): List<ResolveInfo> {
        val mainIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        return context?.packageManager
            ?.queryIntentActivities(mainIntent, 0)
            ?.filter { it.activityInfo?.packageName != null }!!
    }


}