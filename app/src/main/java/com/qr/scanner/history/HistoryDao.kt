package com.qr.scanner.history

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Dao
interface HistoryDao {

    companion object {
        private var INSTANCE: HistoryDao? = null
        fun getInstance(context: Context): HistoryDao {
            return INSTANCE ?: Room
                .databaseBuilder(context.applicationContext, HistoryDataBase::class.java, "history_db")
                .addMigrations(object : Migration(1, 2) {
                    override fun migrate(database: SupportSQLiteDatabase) {
                        database.execSQL("ALTER TABLE History ADD COLUMN name TEXT")
                    }
                }).allowMainThreadQueries()
                .build()
                .historyDao().apply {
                    INSTANCE = this
                }
        }
    }


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(history: History)

    @Update
    fun update(history: History)

    @Delete
    fun delete(history: History)

    @Query("delete from History")
    fun deleteAllHistory()

    @Query("select * from History ORDER BY timestamp DESC")
    fun getAllHistory(): LiveData<List<History>>

    @Query("SELECT * FROM History WHERE isFavorite = 1 ORDER BY timestamp DESC")
    fun getFavorites(): LiveData<List<History>>

    @Query("SELECT * FROM History WHERE isGenerated = 1 ORDER BY timestamp DESC")
    fun getGenerate(): LiveData<List<History>>

}