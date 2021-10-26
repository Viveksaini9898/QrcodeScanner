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
import com.qr.scanner.constant.PREFIX
import com.qr.scanner.extension.isNotBlank
import com.qr.scanner.extension.textString
import com.qr.scanner.utils.viewQrCodeActivity
import kotlinx.android.synthetic.main.fragment_phone_generate.*


class PhoneGenerateFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_phone_generate, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleTextChanged()
        initEditText()


        generateQrcode?.setOnClickListener {
            if (edit_text?.isNotBlank()!!) {
                val result = Result(toBarcodeText(edit_text?.textString!!), null, null, BarcodeFormat.QR_CODE)
                viewQrCodeActivity(requireContext(), result)
            }else {
                edit_text?.error = "Invalid Number"
            }
        }
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

    private fun toBarcodeText(phone: String): String = "$PREFIX$phone"

}