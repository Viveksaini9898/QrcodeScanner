package com.qr.scanner.generatefragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.qr.scanner.R
import com.qr.scanner.extension.isNotBlank
import kotlinx.android.synthetic.main.fragment_email_generate.*
import com.qr.scanner.activity.ViewQRcodeActivity

import com.qr.scanner.extension.textString
import com.qr.scanner.model.Email
import com.qr.scanner.model.Parsers

import com.qr.scanner.viewmodel.HistoryViewModel


class EmailGenerateFragment : Fragment() {


    private val viewModel by lazy {
        ViewModelProvider(this).get(HistoryViewModel::class.java)
    }

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
           createQrCode(parse())
        }
    }

    private fun createQrCode(parse: Parsers) {
        val result = com.qr.scanner.model.Result(
            text = parse.toBarcodeText(),
            formattedText = parse.toFormattedText(),
            format = com.google.zxing.BarcodeFormat.QR_CODE,
            parse = parse.parser,
            date = System.currentTimeMillis(),
            isGenerated = true
        )
        viewModel?.insert(result)
        ViewQRcodeActivity.start(requireContext(), result)

    }

    private fun parse(): Parsers {

        return Email(
            email = edit_text_email.textString,
            subject = edit_text_subject.textString,
            body = edit_text_message.textString
        )
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