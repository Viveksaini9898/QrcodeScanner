package com.qr.scanner.activity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.annotation.NonNull
import androidx.core.content.FileProvider
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.qr.scanner.R
import com.qr.scanner.objects.ImageSaver


import kotlinx.android.synthetic.main.activity_view_barcode.*
import kotlinx.android.synthetic.main.toolbar.*
import java.util.*
import com.qr.scanner.utils.saveImageToGallery
import com.qr.scanner.utils.shareBitmap

class ViewBarcodeActivity : BaseActivity() {
    private var data: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_barcode)

        if (toolbar != null) {
            toolbar?.title = "Product"
            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        if (intent.extras != null) {
            data = intent.getStringExtra("product")
        }

        val image: Bitmap? = createBarcodeBitmap(data,600,300)
        barImage?.setImageBitmap(image)

        shareLayout?.setOnClickListener {
            shareBitmap(this,ImageSaver.saveImageToCache(this, image!!))
        }
        saveBarCodeLayout?.setOnClickListener {
            ImageSaver.savePngImageToPublicDirectory(this, image!!)
        }
    }

    @Throws(WriterException::class)
    private fun createBarcodeBitmap(data: String?, width: Int, height: Int): Bitmap? {
        val writer = MultiFormatWriter()
        val finalData: String? = Uri.encode(data)

        // Use 1 as the height of the matrix as this is a 1D Barcode.
        val bm = writer.encode(finalData, BarcodeFormat.EAN_13, width, 1)
        val bmWidth = bm.width
        val imageBitmap = Bitmap.createBitmap(bmWidth, height, Bitmap.Config.ARGB_8888)
        for (i in 0 until bmWidth) {
            // Paint columns of width 1
            val column = IntArray(height)
            Arrays.fill(column, if (bm[i, 0]) Color.BLACK else Color.WHITE)
            imageBitmap.setPixels(column, 0, 1, i, 0, 1, height)
        }
        return imageBitmap
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home ->{
                finish()
                true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}