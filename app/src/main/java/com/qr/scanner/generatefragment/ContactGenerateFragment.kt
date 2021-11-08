package com.qr.scanner.generatefragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.qr.scanner.R
import com.qr.scanner.activity.ViewQrCodeActivity
import com.qr.scanner.extension.isNotBlank
import com.qr.scanner.extension.textString
import com.qr.scanner.extension.unsafeLazy
import com.qr.scanner.model.Parsers
import com.qr.scanner.model.VCard
import com.qr.scanner.preference.UserPreferences
import com.qr.scanner.viewmodel.HistoryViewModel
import kotlinx.android.synthetic.main.fragment_contact_generate.*

class ContactGenerateFragment : Fragment() {

    private val viewModel by lazy {
        ViewModelProvider(this).get(HistoryViewModel::class.java)
    }
    private val userPreferences by unsafeLazy {
        UserPreferences(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contact_generate, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toggleCreateBarcodeButton()

        handleTextChanged()
        initEditText()

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
        viewModel.insert(result,userPreferences?.doNotSaveDuplicates)
        ViewQrCodeActivity.start(requireContext(), result)

    }

    private fun parse(): Parsers {

        return VCard(
            firstName = edit_text_first_name.textString,
            lastName = edit_text_last_name.textString,
            organization = edit_text_organization.textString,
            title = edit_text_job.textString,
            email = edit_text_email.textString,
            phone = edit_text_phone.textString,
            secondaryPhone = edit_text_fax.textString,
            url = edit_text_website.textString
        )
    }


    private fun handleTextChanged() {
        edit_text_first_name.addTextChangedListener { toggleCreateBarcodeButton() }
        edit_text_last_name.addTextChangedListener { toggleCreateBarcodeButton() }
        edit_text_phone.addTextChangedListener { toggleCreateBarcodeButton() }
    }

    private fun initEditText() {
        edit_text_first_name.requestFocus()
    }

    private fun toggleCreateBarcodeButton() {
        if (edit_text_first_name.isNotBlank() || edit_text_last_name.isNotBlank() || edit_text_phone.isNotBlank()) {
            generateQrcode?.isEnabled = true
            generateQrcode?.isClickable = true
            generateQrcode?.background = resources?.getDrawable(R.drawable.button_background_1)
        } else {
            generateQrcode?.isEnabled = false
            generateQrcode?.isClickable = false
            generateQrcode?.background = resources?.getDrawable(R.drawable.circle_button_background)
        }
    }

}