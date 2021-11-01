package com.qr.scanner.history

import androidx.room.*


@Database(entities = [History::class], version = 1)
abstract class HistoryDataBase : RoomDatabase() {
    abstract fun historyDao():  HistoryDao
}