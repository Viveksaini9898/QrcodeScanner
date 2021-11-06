package com.qr.scanner.objects

import android.graphics.Bitmap
import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatReader
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.Result
import com.google.zxing.common.HybridBinarizer
import com.qr.scanner.extension.orZero
import io.reactivex.Single
import io.reactivex.SingleEmitter
import io.reactivex.schedulers.Schedulers

object ImageScanner {
    private var bitmapBuffer: IntArray? = null

    fun tryParse(image: Bitmap): Result {
        val width = image.width
        val height = image.height
        val size = width * height

        if (size > bitmapBuffer?.size.orZero()) {
            bitmapBuffer = IntArray(size)
        }

        image.getPixels(bitmapBuffer, 0, width, 0, 0, width, height)

        val source = RGBLuminanceSource(width, height, bitmapBuffer)
        val bitmap = BinaryBitmap(HybridBinarizer(source))

        val reader = MultiFormatReader()
        return reader.decode(bitmap)
    }
}