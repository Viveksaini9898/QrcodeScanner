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
import com.qr.scanner.extension.isNotBlank
import com.qr.scanner.extension.textString
import com.qr.scanner.history.History
import com.qr.scanner.utils.viewQrCodeActivity
import com.qr.scanner.viewmodel.HistoryViewModel
import kotlinx.android.synthetic.main.fragment_email_generate.*
import kotlinx.android.synthetic.main.fragment_text_generate.*
import kotlinx.android.synthetic.main.fragment_text_generate.generateQrcode

class TextGenerateFragment : Fragment() {

    private val viewModel by lazy {
        ViewModelProvider(this).get(HistoryViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_text_generate, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handleTextChanged()
        initEditText()

        generateQrcode?.setOnClickListener {
            val result = Result(edit_text.textString, null, null, BarcodeFormat.QR_CODE)
            val history = History(0,
                result?.text!!, result?.barcodeFormat!!, result?.timestamp!!,
                isGenerated = true,
                isFavorite = false
            )
          //  viewModel?.insert(history)
            viewQrCodeActivity(requireContext(), result)
        }
    }

    private fun initEditText() {
        edit_text.requestFocus()

    }

    private fun handleTextChanged() {
        edit_text.addTextChangedListener {
            if (edit_text.isNotBlank()) {
                generateQrcode?.isClickable = true
                generateQrcode?.background = resources?.getDrawable(R.drawable.button_background_1)
            } else {
                generateQrcode?.isClickable = false
                generateQrcode?.background = resources?.getDrawable(R.drawable.circle_button_background)

            }
        }
    }
}