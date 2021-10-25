package com.qr.scanner.activity

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.core.BarcodeFormat
import com.qr.scanner.Contents
import com.qr.scanner.R
import com.qr.scanner.encode.QRCodeEncoder
import com.core.WriterException
import com.qr.scanner.utils.saveImageToGallery
import com.qr.scanner.utils.shareBitmap
import kotlinx.android.synthetic.main.activity_view_generate_qr.*


class ViewGenerateQrActivity : AppCompatActivity() {
    private var bitmap: Bitmap? = null
    private var data: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_generate_qr)

        if (intent.extras != null){
            data = intent?.getStringExtra("data")
        }
        val qrCodeEncoder = QRCodeEncoder(
            data,
            null,
            Contents.Type.EMAIL,
            BarcodeFormat.QR_CODE.toString(),
            640
        )
        try {
            bitmap = qrCodeEncoder.encodeAsBitmap()
            qrImage?.setImageBitmap(bitmap)
        } catch (e: WriterException) {
            e.printStackTrace()
        }
        shareQrcodeLayout?.setOnClickListener {
            shareBitmap(this,bitmap)
        }
        saveQrCodeLayout?.setOnClickListener {
            saveImageToGallery(applicationContext,bitmap)
        }
    }
}