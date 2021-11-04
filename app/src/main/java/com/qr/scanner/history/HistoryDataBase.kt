package com.qr.scanner.history

import androidx.room.*
import com.qr.scanner.model.Result


@Database(entities = [Result::class], version = 1)
abstract class HistoryDataBase : RoomDatabase() {
    abstract fun historyDao():  HistoryDao
}