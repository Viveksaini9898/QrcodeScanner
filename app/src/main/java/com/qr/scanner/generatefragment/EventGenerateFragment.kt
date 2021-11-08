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
import com.qr.scanner.model.VEvent
import com.qr.scanner.preference.UserPreferences
import com.qr.scanner.viewmodel.HistoryViewModel
import kotlinx.android.synthetic.main.fragment_event_generate.*
import kotlinx.android.synthetic.main.fragment_event_generate.generateQrcode
import java.text.SimpleDateFormat
import java.util.*


class EventGenerateFragment : Fragment() {

    private val viewModel by lazy {
        ViewModelProvider(this).get(HistoryViewModel::class.java)
    }
    private val userPreferences by unsafeLazy {
        UserPreferences(requireContext())
    }

    private val BARCODE_TEXT_DATE_FORMATTER by unsafeLazy {
        SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
    }

    private val FORMATTED_TEXT_DATE_FORMATTER by unsafeLazy {
        SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.ENGLISH)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_event_generate, container, false)
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
        return VEvent(
            organizer = edit_text_organizer.textString,
            summary = edit_text_summary.textString,
            location = edit_text_location.textString,
            startDate = button_date_time_start.dateTime,
            endDate = button_date_time_end.dateTime
        )
    }

    private fun initEditText() {
        edit_text_summary.requestFocus()
    }

    private fun handleTextChanged() {
        edit_text_location.addTextChangedListener { toggleCreateBarcodeButton() }
        edit_text_organizer.addTextChangedListener { toggleCreateBarcodeButton() }
        edit_text_summary.addTextChangedListener { toggleCreateBarcodeButton() }
    }

    private fun toggleCreateBarcodeButton() {
        if (edit_text_location.isNotBlank() || edit_text_organizer.isNotBlank() || edit_text_summary.isNotBlank()) {
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