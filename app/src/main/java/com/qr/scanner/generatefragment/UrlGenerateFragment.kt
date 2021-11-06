package com.qr.scanner.generatefragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.core.BarcodeFormat
import com.core.Result
import com.qr.scanner.R
import com.qr.scanner.activity.ViewQRcodeActivity
import com.qr.scanner.extension.isNotBlank
import com.qr.scanner.extension.textString
import com.qr.scanner.model.Other
import com.qr.scanner.model.Parsers
import com.qr.scanner.model.Url
import com.qr.scanner.utils.viewQrCodeActivity
import com.qr.scanner.viewmodel.HistoryViewModel
import kotlinx.android.synthetic.main.fragment_url_generate.edit_text
import kotlinx.android.synthetic.main.fragment_url_generate.generateQrcode

class UrlGenerateFragment : Fragment() {

    private val viewModel by lazy {
        ViewModelProvider(this).get(HistoryViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_url_generate, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleTextChanged()
        initEditText()

        generateQrcode?.setOnClickListener {
            if (edit_text.isNotBlank()) {
                createQrCode(parse())
            } else {
                edit_text?.error = "Invalid URL"
            }
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
        return Url(edit_text.textString)
    }

    private fun initEditText() {
        edit_text.requestFocus()

    }

    private fun handleTextChanged() {
        edit_text.addTextChangedListener {
            if (edit_text.isNotBlank()) {
                generateQrcode?.background = resources?.getDrawable(R.drawable.button_background_1)
            } else {
                generateQrcode?.background =
                    resources?.getDrawable(R.drawable.circle_button_background)

            }
        }
    }
}