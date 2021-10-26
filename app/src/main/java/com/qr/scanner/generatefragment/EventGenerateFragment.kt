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
import com.qr.scanner.constant.*
import com.qr.scanner.extension.appendIfNotNullOrBlank
import com.qr.scanner.extension.isNotBlank
import com.qr.scanner.extension.textString
import com.qr.scanner.extension.unsafeLazy
import com.qr.scanner.utils.viewQrCodeActivity
import kotlinx.android.synthetic.main.fragment_email_generate.*
import kotlinx.android.synthetic.main.fragment_event_generate.*
import kotlinx.android.synthetic.main.fragment_event_generate.generateQrcode
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class EventGenerateFragment : Fragment() {

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

        handleTextChanged()
        initEditText()

        generateQrcode?.setOnClickListener {
            val result = Result(toBarcodeText(edit_text_title.textString,edit_text_organizer.textString,edit_text_summary.textString,button_date_time_start.dateTime,button_date_time_end.dateTime), null, null, BarcodeFormat.QR_CODE)
            viewQrCodeActivity(requireContext(), result)
        }
    }

    private fun initEditText() {
        edit_text_title.requestFocus()

    }

    private fun handleTextChanged() {
        edit_text_title.addTextChangedListener { toggleCreateBarcodeButton() }
        edit_text_organizer.addTextChangedListener { toggleCreateBarcodeButton() }
        edit_text_summary.addTextChangedListener { toggleCreateBarcodeButton() }
    }

    private fun toggleCreateBarcodeButton() {
        if (edit_text_title.isNotBlank() || edit_text_organizer.isNotBlank() || edit_text_summary.isNotBlank()) {
            generateQrcode?.background = resources?.getDrawable(R.drawable.button_background_1)
        } else {
            generateQrcode?.background = resources?.getDrawable(R.drawable.circle_button_background)
        }
    }

   private fun toBarcodeText(
        uid: String?,
        organizer: String?,
        summary: String?,
        startDate: Long?,
        endDate: Long?
    ): String {
        val startDate = BARCODE_TEXT_DATE_FORMATTER.formatOrNull(startDate)
        val endDate = BARCODE_TEXT_DATE_FORMATTER.formatOrNull(endDate)

        return StringBuilder()
            .append(EVENT_SCHEMA_PREFIX)
            .append(PARAMETERS_SEPARATOR_1)
            .appendIfNotNullOrBlank(UID_PREFIX, uid, PARAMETERS_SEPARATOR_1)
            .appendIfNotNullOrBlank(ORGANIZER_PREFIX, organizer, PARAMETERS_SEPARATOR_1)
            .appendIfNotNullOrBlank(START_PREFIX, startDate, PARAMETERS_SEPARATOR_1)
            .appendIfNotNullOrBlank(END_PREFIX, endDate, PARAMETERS_SEPARATOR_1)
            .appendIfNotNullOrBlank(SUMMARY_PREFIX, summary, PARAMETERS_SEPARATOR_1)
            .append(EVENT_SCHEMA_PREFIX)
            .toString()
    }

    private fun DateFormat.formatOrNull(time: Long?): String? {
        return try {
            format(Date(time!!))
        } catch (ex: Exception) {
            null
        }
    }
}