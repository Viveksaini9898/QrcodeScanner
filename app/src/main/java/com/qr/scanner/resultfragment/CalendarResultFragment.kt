package com.qr.scanner.resultfragment

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.core.Result
import com.core.client.result.CalendarParsedResult
import com.core.client.result.TextParsedResult
import com.qr.scanner.R
import com.qr.scanner.preference.UserPreferences
import com.qr.scanner.result.CalendarResultHandler
import com.qr.scanner.result.ResultHandlerFactory
import com.qr.scanner.utils.*
import kotlinx.android.synthetic.main.fragment_calendar_result.view.*


class CalendarResultFragment : Fragment() {

    private var userPreferences: UserPreferences? = null
    private var result: Result? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            result = it.getParcelable(RESULT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View? = inflater.inflate(R.layout.fragment_calendar_result, container, false)

        userPreferences = UserPreferences(requireContext())

        val resultHandler = ResultHandlerFactory.makeResultHandler(activity, result)

        val calenderResult = resultHandler?.result as CalendarParsedResult

        if (userPreferences?.autoCopy!!){
            copyContent(requireContext(),calenderResult.toString())
        }

        if (calenderResult?.summary != null && calenderResult?.summary?.isNotEmpty()!!) {
            view?.tvTitle?.text = calenderResult?.summary
        } else {
            view?.titleLayout?.visibility = View.GONE
        }
        if (calenderResult?.location != null && calenderResult?.location?.isNotEmpty()!!) {
            view?.tvLocation?.text = calenderResult?.location
        } else {
            view?.locationLayout?.visibility = View.GONE
        }
        if (calenderResult?.start != null) {
            view?.tvStartTime?.text = calenderResult?.start.toString()
        } else {
            view?.startTimeLayout?.visibility = View.GONE
        }
        if (calenderResult?.end != null) {
            view?.tvEndTime?.text = calenderResult?.end.toString()
        } else {
            view?.endTimeLayout?.visibility = View.GONE
        }
        if (calenderResult?.organizer != null && calenderResult?.organizer?.isNotEmpty()!!) {
            view?.tvOrganizer?.text = calenderResult?.organizer
        } else {
            view?.organizerLayout?.visibility = View.GONE
        }

        if (calenderResult?.description != null && calenderResult?.description?.isNotEmpty()!!) {
            view?.tvDescription?.text = calenderResult?.description
        } else {
            view?.descriptionLayout?.visibility = View.GONE
        }


        view?.copyLayout?.setOnClickListener {
            copyContent(requireContext(), calenderResult.toString())
        }

        view?.viewQrCode?.setOnClickListener {
            viewQrCodeActivity(requireContext(), result)
        }
        view?.shareLayout?.setOnClickListener {
            shareContent(requireContext(), calenderResult.toString())
        }
        view?.searchLocationLayout?.setOnClickListener {
            resultHandler?.searchMap(calenderResult?.location)
        }

        view?.addEventLayout?.setOnClickListener {
            addCalendarEvent(calenderResult?.summary,calenderResult?.startTimestamp,calenderResult?.isStartAllDay,calenderResult?.endTimestamp,calenderResult?.location,calenderResult?.description,calenderResult?.attendees)
        }
        return view
    }


    private fun addCalendarEvent(
        summary: String?,
        start: Long,
        allDay: Boolean,
        end: Long,
        location: String?,
        description: String?,
        attendees: Array<String?>?
    ) {
        var end = end
        val intent = Intent(Intent.ACTION_INSERT)
        intent.type = "vnd.android.cursor.item/event"
        intent.putExtra("beginTime", start)
        if (allDay) {
            intent.putExtra("allDay", true)
        }
        if (end < 0L) {
            end = if (allDay) {
                // + 1 day
                start + 24 * 60 * 60 * 1000
            } else {
                start
            }
        }
        intent.putExtra("endTime", end)
        intent.putExtra("title", summary)
        intent.putExtra("eventLocation", location)
        intent.putExtra("description", description)
        if (attendees != null) {
            intent.putExtra(Intent.EXTRA_EMAIL, attendees)
        }
        try {
            rawLaunchIntent(requireContext(),intent)
        } catch (anfe: ActivityNotFoundException) {
            intent.action = Intent.ACTION_EDIT
            launchIntent(requireContext(),intent)
        }
    }


    companion object {
        @JvmStatic
        fun newInstance(result: Result?) =
            CalendarResultFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(RESULT, result)
                }
            }
    }
}