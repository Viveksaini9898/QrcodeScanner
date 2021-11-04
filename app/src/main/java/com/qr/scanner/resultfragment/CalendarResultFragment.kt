package com.qr.scanner.resultfragment

import android.content.Intent
import android.os.Bundle
import android.provider.CalendarContract
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.qr.scanner.R
import com.qr.scanner.activity.ViewQRcodeActivity
import com.qr.scanner.constant.PARSE_RESULT
import com.qr.scanner.extension.unsafeLazy
import com.qr.scanner.preference.UserPreferences
import com.qr.scanner.result.ParsedResultHandler
import com.qr.scanner.utils.*
import kotlinx.android.synthetic.main.fragment_calendar_result.*
import kotlinx.android.synthetic.main.fragment_calendar_result.view.*
import java.text.SimpleDateFormat


class CalendarResultFragment : Fragment() {

    private var userPreferences: UserPreferences? = null
    private var result: com.qr.scanner.model.Result? = null
    private val barcode by unsafeLazy {
        ParsedResultHandler(result!!)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            result = it.getSerializable(PARSE_RESULT) as com.qr.scanner.model.Result?
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_calendar_result, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userPreferences = UserPreferences(requireContext())


        if (userPreferences?.autoCopy!!){
            copyContent(requireContext(),barcode.text)
        }

        if (barcode.eventSummary.isNullOrEmpty().not()) {
            tvTitle?.text = barcode.eventSummary
        } else {
            titleLayout?.visibility = View.GONE
        }
        if (barcode?.eventLocation.isNullOrEmpty().not()) {
            tvLocation?.text = barcode.eventLocation
        } else {
            locationLayout?.visibility = View.GONE
            searchLocationLayout?.visibility = View.GONE
        }
        if (barcode.eventStartDate != null) {
            tvStartTime?.text = getDate(barcode?.eventStartDate!!)
        } else {
            startTimeLayout?.visibility = View.GONE
        }
        if (barcode.eventEndDate != null) {
            tvEndTime?.text = getDate(barcode.eventEndDate!!)
        } else {
            endTimeLayout?.visibility = View.GONE
        }
        if (barcode.eventOrganizer.isNullOrEmpty().not()) {
            tvOrganizer?.text = barcode.eventOrganizer
        } else {
            organizerLayout?.visibility = View.GONE
        }

        if (barcode.eventDescription.isNullOrEmpty().not()) {
            tvDescription?.text = barcode.eventDescription
        } else {
            descriptionLayout?.visibility = View.GONE
        }


        copyLayout?.setOnClickListener {
            copyContent(requireContext(), barcode.text)
        }

        viewQrCode?.setOnClickListener {
            ViewQRcodeActivity.start(requireContext(), result!!)
        }
        shareLayout?.setOnClickListener {
            shareContent(requireContext(), barcode.text)
        }
        searchLocationLayout?.setOnClickListener {
            searchMap(requireContext(),barcode.eventLocation)
        }

        addEventLayout?.setOnClickListener {
            addToCalendar()
        }
    }

    private fun addToCalendar() {
        val intent = Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
            putExtra(CalendarContract.Events.TITLE, barcode.eventSummary)
            putExtra(CalendarContract.Events.DESCRIPTION, barcode.eventDescription)
            putExtra(CalendarContract.Events.EVENT_LOCATION, barcode.eventLocation)
            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, barcode.eventStartDate)
            putExtra(CalendarContract.EXTRA_EVENT_END_TIME, barcode.eventEndDate)
        }
        startActivityIfExists(requireContext(),intent)
    }

    private fun getDate(time_stamp_server: Long): String? {
        val formatter = SimpleDateFormat("dd-mm-yyyy HH:mm")
        return formatter.format(time_stamp_server)
    }


    companion object {
        @JvmStatic
        fun newInstance(result: com.qr.scanner.model.Result) =
            CalendarResultFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(PARSE_RESULT, result)
                }
            }
    }
}