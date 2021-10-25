package com.qr.scanner.generatefragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import com.qr.scanner.R
import com.qr.scanner.extension.isNotBlank
import kotlinx.android.synthetic.main.fragment_email_generate.*
import kotlinx.android.synthetic.main.fragment_text_generate.*
import kotlinx.android.synthetic.main.fragment_text_generate.generateQrcode

class TextGenerateFragment : Fragment() {

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
    }

    private fun initEditText() {
        edit_text.requestFocus()

    }

    private fun handleTextChanged() {
        edit_text.addTextChangedListener {
            if (edit_text.isNotBlank()) {
                generateQrcode?.background = resources?.getDrawable(R.drawable.button_background_1)

            } else {
                generateQrcode?.background = resources?.getDrawable(R.drawable.circle_button_background)

            }
        }
    }
}