package com.qr.scanner.history

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.core.BarcodeFormat
import com.core.Result
import com.core.ResultMetadataType
import com.core.ResultPoint

@Entity(tableName = "History")
@TypeConverters(DataBaseTypeConverter::class)
data class History(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val text: String?,
    val format: BarcodeFormat?,
    val timestamp: Long?,
    val isGenerated: Boolean = false,
    val isFavorite: Boolean = false,

)