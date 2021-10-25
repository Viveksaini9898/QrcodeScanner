package com.qr.scanner.activity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.NonNull
import androidx.core.content.FileProvider
import com.qr.scanner.R
import com.core.WriterException

import com.core.common.BitMatrix

import com.core.oned.Code128Writer

import com.core.BarcodeFormat

import kotlinx.android.synthetic.main.activity_view_barcode.*
import kotlinx.android.synthetic.main.toolbar.*
import java.util.*
import com.core.MultiFormatWriter
import com.qr.scanner.utils.saveImageToGallery
import com.qr.scanner.utils.shareBitmap

class ViewBarcodeActivity : AppCompatActivity() {
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
            shareBitmap(this,image)
        }
        saveBarCodeLayout?.setOnClickListener {
            saveImageToGallery(applicationContext,image)
        }
    }

    @Throws(WriterException::class)
    private fun createBarcodeBitmap(data: String?, width: Int, height: Int): Bitmap? {
        val writer = MultiFormatWriter()
        val finalData: String? = Uri.encode(data)

        // Use 1 as the height of the matrix as this is a 1D Barcode.
        val bm = writer.encode(finalData, BarcodeFormat.CODE_128, width, 1)
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

}