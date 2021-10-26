package com.qr.scanner.activity

import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.core.*
import com.core.common.HybridBinarizer
import com.qr.scanner.R
import java.io.FileNotFoundException
import java.io.InputStream
import androidx.core.app.ActivityCompat.startActivityForResult
import com.qr.scanner.extension.Toast.toast
import com.qr.scanner.history.HistoryManager
import com.qr.scanner.utils.getPath
import kotlinx.android.synthetic.main.activity_image_scanner.*
import kotlinx.android.synthetic.main.toolbar.*
import java.lang.Exception


class ImageScannerActivity : AppCompatActivity() {

    private var bitmap: Bitmap? = null
    private var result: Result? = null
    private var bitmapBuffer: IntArray? = null

    private val SELECT_PHOTO: Int = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_scanner)

        if (toolbar != null) {
            toolbar?.title = "Scan"
            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

       pickImage()

        selectImage?.setOnClickListener {
           pickImage()
        }

        scanQrcode?.setOnClickListener {

            result = tryParse(bitmap!!)
            if (result != null) {
                val intent = Intent(applicationContext, ScanResultActivity::class.java)
                intent.putExtra("result", result)
                startActivity(intent)
                HistoryManager(this).add(applicationContext,result)
            } else {
                toast(applicationContext, "Qr not found")
            }
        }
    }

    private fun pickImage(){
        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent, SELECT_PHOTO)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            SELECT_PHOTO -> if (resultCode == RESULT_OK) {
                if (data != null) {
                    val selectedImage: Uri? = data?.data

                    bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImage)
                    image?.setImageBitmap(bitmap)

                } else {
                    toast(applicationContext, "Image no found")
                }
            } else {
                finish()
            }
        }
    }

    private fun tryParse(image: Bitmap): Result? {
        try {
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
        } catch (e: Exception) {
            return null
        }
    }


    private fun Int?.orZero(): Int {
        return this ?: 0
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}