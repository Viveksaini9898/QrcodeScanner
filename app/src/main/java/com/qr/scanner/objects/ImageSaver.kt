package com.qr.scanner.objects

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore.Images
import androidx.core.content.FileProvider
import com.qr.scanner.R
import com.qr.scanner.extension.Toast.toast
import com.qr.scanner.model.Result
import com.qr.scanner.result.ParsedResultHandler
import com.qr.scanner.utils.storePath
import io.reactivex.Completable
import java.io.*


object ImageSaver {

    fun saveImageToCache(context: Context, image: Bitmap): Uri? {
        // Create folder for image
        val imagesFolder = File(context.externalCacheDir, "images")
        imagesFolder.mkdirs()

        // Create image file
        val imageFileName = System.currentTimeMillis().toString() + "qr_scanner" + ".png"
        val imageFile = File(imagesFolder, imageFileName)

        try {
            // Save image to file
            FileOutputStream(imageFile).apply {
                image.compress(Bitmap.CompressFormat.PNG, 100, this)
                flush()
                close()
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return FileProvider.getUriForFile(context, "com.qr.scanner.provider", imageFile)
    }

    fun savePngImageToPublicDirectory(context: Context, image: Bitmap) {
        val appDir = File(storePath())
        if (!appDir.exists()) {
            appDir.mkdir()
        }
        val imageTitle = System.currentTimeMillis().toString() + "qr_scanner" + ".png"
        val imageFile = File(appDir, imageTitle)

        try {
            // Save image to file
            FileOutputStream(imageFile).apply {
                image.compress(Bitmap.CompressFormat.PNG, 100, this)
                flush()
                close()
            }
            toast(context, R.string.save_successfully)

        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    fun saveSvgImageToPublicDirectory(context: Context, image: String): Completable {
        return Completable.create { emitter ->
            try {
                saveToPublicDirectory(context, "image/svg+xml") { outputStream ->
                    outputStream.write(image.toByteArray())
                }
                emitter.onComplete()
            } catch (ex: Exception) {
                emitter.onError(ex)
            }
        }
    }

    private fun saveToPublicDirectory(
        context: Context,
        mimeType: String,
        action: (OutputStream) -> Unit
    ) {
        val contentResolver = context.contentResolver ?: return

        val imageTitle = System.currentTimeMillis().toString() + "qr_scanner" + ".png"

        val values = ContentValues().apply {
            put(Images.Media.TITLE, imageTitle)
            put(Images.Media.DISPLAY_NAME, imageTitle)
            put(Images.Media.MIME_TYPE, mimeType)
            put(Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
        }

        val uri = contentResolver.insert(Images.Media.EXTERNAL_CONTENT_URI, values) ?: return
        contentResolver.openOutputStream(uri)?.apply {
            action(this)
            flush()
            close()
        }
        toast(context, R.string.save_successfully)
    }
}