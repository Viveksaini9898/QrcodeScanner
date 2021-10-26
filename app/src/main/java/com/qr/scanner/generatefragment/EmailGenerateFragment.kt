package com.qr.scanner.generatefragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import com.qr.scanner.R
import com.qr.scanner.extension.isNotBlank
import kotlinx.android.synthetic.main.fragment_email_generate.*
import com.core.BarcodeFormat
import com.core.Result
import com.core.client.result.EmailAddressParsedResult
import com.core.client.result.EmailAddressResultParser
import com.qr.scanner.Contents
import com.qr.scanner.activity.ViewGenerateQrActivity
import com.qr.scanner.activity.ViewQRcodeActivity
import com.qr.scanner.constant.*

import com.qr.scanner.encode.QRCodeEncoder
import com.qr.scanner.extension.appendIfNotNullOrBlank
import com.qr.scanner.extension.textString
import com.qr.scanner.result.EmailAddressResultHandler
import com.qr.scanner.result.ResultHandlerFactory
import com.qr.scanner.utils.RESULT
import com.qr.scanner.utils.saveImageToGallery
import com.qr.scanner.utils.shareBitmap
import com.qr.scanner.utils.viewQrCodeActivity
import kotlinx.android.synthetic.main.activity_view_barcode.*


class EmailGenerateFragment : Fragment() {


    private var email: EmailAddressParsedResult? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_email_generate, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initTitleEditText()
        handleTextChanged()

        generateQrcode?.setOnClickListener {
            val result = Result(toBarcodeText(), null, null, BarcodeFormat.QR_CODE)
            viewQrCodeActivity(requireContext(), result)
        }
    }

    private fun toBarcodeText(): String {
        return StringBuilder()
            .append(MATMSG_SCHEMA_PREFIX)
            .appendIfNotNullOrBlank(
                MATMSG_EMAIL_PREFIX,
                edit_text_email.textString,
                MATMSG_SEPARATOR
            )
            .appendIfNotNullOrBlank(
                MATMSG_SUBJECT_PREFIX,
                edit_text_subject.textString,
                MATMSG_SEPARATOR
            )
            .appendIfNotNullOrBlank(
                MATMSG_BODY_PREFIX,
                edit_text_message.textString,
                MATMSG_SEPARATOR
            )
            .append(MATMSG_SEPARATOR)
            .toString()
    }

    fun loadViewGenerateQrActivity(context: Context?, string: String?) {
        val intent = Intent(context, ViewGenerateQrActivity::class.java)
        intent.putExtra("data", string)
        context?.startActivity(intent)
    }

    private fun initTitleEditText() {
        edit_text_email.requestFocus()
    }

    private fun handleTextChanged() {
        edit_text_email.addTextChangedListener { toggleCreateBarcodeButton() }
        edit_text_subject.addTextChangedListener { toggleCreateBarcodeButton() }
        edit_text_message.addTextChangedListener { toggleCreateBarcodeButton() }
    }

    private fun toggleCreateBarcodeButton() {
        if (edit_text_email.isNotBlank() || edit_text_subject.isNotBlank() || edit_text_message.isNotBlank()) {
            generateQrcode?.background = resources?.getDrawable(R.drawable.button_background_1)
        } else {
            generateQrcode?.background = resources?.getDrawable(R.drawable.circle_button_background)
        }
    }
}