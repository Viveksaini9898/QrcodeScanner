package com.qr.scanner.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.zxing.BarcodeFormat
import com.qr.scanner.history.DataBaseTypeConverter
import java.io.Serializable


@Entity(tableName = "results")
@TypeConverters(DataBaseTypeConverter::class)
data class Result(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String? = null,
    val text: String,
    val formattedText: String,
    val format: BarcodeFormat,
    val parse: ParsedResultType,
    val date: Long,
    val isGenerated: Boolean = false,
    var isFavorite: Boolean = false,
    val errorCorrectionLevel: String? = null,
    val country: String? = null
) : Serializable