package com.qr.scanner.objects

import com.google.zxing.BarcodeFormat
import com.google.zxing.Result
import com.google.zxing.ResultMetadataType
import com.qr.scanner.model.*

object BarcodeParser {

    fun parseResult(result: Result): com.qr.scanner.model.Result {
        val parse = parseSchema(result.barcodeFormat, result.text)
        return Result(
            text = result.text,
            formattedText = parse.toFormattedText(),
            format = result.barcodeFormat,
            parse = parse.parser,
            date = result.timestamp,
            errorCorrectionLevel = result.resultMetadata?.get(ResultMetadataType.ERROR_CORRECTION_LEVEL) as? String,
            country = result.resultMetadata?.get(ResultMetadataType.POSSIBLE_COUNTRY) as? String
        )
    }

    fun parseSchema(format: BarcodeFormat, text: String): Parsers {
        if (format != BarcodeFormat.QR_CODE) {
            return Other(text)
        }

        return App.parse(text)
            ?: GoogleMaps.parse(text)
            ?: Url.parse(text)
            ?: Phone.parse(text)
            ?: Geo.parse(text)
            ?: Bookmark.parse(text)
            ?: Sms.parse(text)
            ?: Mms.parse(text)
            ?: Wifi.parse(text)
            ?: Email.parse(text)
            ?: VEvent.parse(text)
            ?: MeCard.parse(text)
            ?: VCard.parse(text)
            ?: Other(text)
    }
}
