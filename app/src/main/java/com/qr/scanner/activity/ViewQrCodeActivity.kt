package com.qr.scanner.activity

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.MenuItem
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.qr.scanner.R
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.activity_view_qr_code.*

import com.qr.scanner.constant.PARSE_RESULT
import com.qr.scanner.extension.unsafeLazy
import com.qr.scanner.model.ParsedResultType
import com.qr.scanner.utils.shareBitmap
import java.lang.IllegalArgumentException
import java.lang.NullPointerException
import com.qr.scanner.model.Result
import com.qr.scanner.objects.ImageSaver


class ViewQrCodeActivity : BaseActivity() {

    companion object {
        fun start(context: Context, result: Result?) {
            val intent = Intent(context, ViewQrCodeActivity::class.java).apply {
                putExtra(PARSE_RESULT, result)
            }
            context.startActivity(intent)
        }
    }

    private val result by unsafeLazy {
        intent?.getSerializableExtra(PARSE_RESULT) as? Result ?: throw IllegalArgumentException("No result passed")
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_qr_code)

        if (toolbar != null) {
            toolbar?.title = "Qr Scanner"
            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }


        when (result.parse) {
            ParsedResultType.WIFI -> {
                toolbar?.title = "Wifi"
            }
            ParsedResultType.SMS -> {
                toolbar?.title = "SMS"
            }
            ParsedResultType.URL -> {
                toolbar?.title = "Website"
            }
            ParsedResultType.PHONE -> {
                toolbar?.title = "Phone"
            }
            ParsedResultType.EMAIL -> {
                toolbar?.title = "Email"
            }
            ParsedResultType.VCARD -> {
                toolbar?.title = "Contact"
            }
            ParsedResultType.VEVENT -> {
                toolbar?.title = "Calender"
            }
            ParsedResultType.OTHER -> {
                toolbar?.title = "Text"
            }
            ParsedResultType.APP -> {
                toolbar?.title = "App"
            }
        }
        val image: Bitmap? = generateQr(result.text,640,640)
        qrImage?.setImageBitmap(image)

        saveQrCodeLayout?.setOnClickListener {
            ImageSaver.savePngImageToPublicDirectory(this, image!!)
        }
        shareQrcodeLayout?.setOnClickListener {
            shareBitmap(this,ImageSaver.saveImageToCache(this, image!!))

        }
    }

    @Throws(WriterException::class, NullPointerException::class)
    private fun generateQr(text: String, width: Int, height: Int): Bitmap? {
        val bitMatrix: BitMatrix = try {
            MultiFormatWriter().encode(
                text, BarcodeFormat.QR_CODE,
                width, height, null
            )
        } catch (Illegalargumentexception: IllegalArgumentException) {
            return null
        }
        val bitMatrixWidth = bitMatrix.width
        val bitMatrixHeight = bitMatrix.height
        val pixels = IntArray(bitMatrixWidth * bitMatrixHeight)
        val colorWhite = -0x1
        val colorBlack = -0x1000000
        for (y in 0 until bitMatrixHeight) {
            val offset = y * bitMatrixWidth
            for (x in 0 until bitMatrixWidth) {
                pixels[offset + x] = if (bitMatrix[x, y]) colorBlack else colorWhite
            }
        }
        val bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444)
        bitmap.setPixels(pixels, 0, width, 0, 0, bitMatrixWidth, bitMatrixHeight)
        return bitmap
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