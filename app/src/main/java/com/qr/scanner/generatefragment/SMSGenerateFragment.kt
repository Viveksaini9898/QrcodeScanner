package com.qr.scanner.generatefragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import com.core.BarcodeFormat
import com.core.Result
import com.qr.scanner.R
import com.qr.scanner.constant.SEPARATOR
import com.qr.scanner.constant.SMS_PREFIX
import com.qr.scanner.extension.isNotBlank
import com.qr.scanner.extension.textString
import com.qr.scanner.utils.viewQrCodeActivity
import kotlinx.android.synthetic.main.fragment_s_m_s_generate.*
import kotlinx.android.synthetic.main.fragment_s_m_s_generate.edit_text_message
import kotlinx.android.synthetic.main.fragment_s_m_s_generate.generateQrcode


class SMSGenerateFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_s_m_s_generate, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initTitleEditText()
        handleTextChanged()

        generateQrcode?.setOnClickListener {
            val result = Result(toBarcodeText(edit_text_phone?.textString,edit_text_message?.textString), null, null, BarcodeFormat.QR_CODE)
            viewQrCodeActivity(requireContext(), result)
        }
    }

    private fun initTitleEditText() {
        edit_text_phone.requestFocus()
    }

    private fun handleTextChanged() {
        edit_text_phone.addTextChangedListener { toggleCreateBarcodeButton() }
        edit_text_message.addTextChangedListener { toggleCreateBarcodeButton() }
    }

    private fun toggleCreateBarcodeButton() {
        if(edit_text_phone.isNotBlank() || edit_text_message.isNotBlank()){
            generateQrcode?.background = resources?.getDrawable(R.drawable.button_background_1)
        } else {
            generateQrcode?.background = resources?.getDrawable(R.drawable.circle_button_background)
        }
    }

    private fun toBarcodeText(phone: String?, message: String?): String {
        return SMS_PREFIX +
                phone.orEmpty() +
                "$SEPARATOR${message.orEmpty()}"
    }
}