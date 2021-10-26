package com.qr.scanner.resultfragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.core.Result
import com.core.client.result.ParsedResultType
import com.core.client.result.SMSParsedResult
import com.qr.scanner.R
import com.qr.scanner.constant.RESULT
import com.qr.scanner.preference.UserPreferences
import com.qr.scanner.result.ResultHandlerFactory
import com.qr.scanner.utils.*
import kotlinx.android.synthetic.main.fragment_sms_result_fragmet.view.*

class SmsResultFragment : Fragment() {

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
        val view: View? = inflater.inflate(R.layout.fragment_sms_result_fragmet, container, false)

        userPreferences = UserPreferences(requireContext())

        val resultHandler = ResultHandlerFactory.makeResultHandler(activity, result)

        val smsResult = resultHandler.result as SMSParsedResult?

        if (userPreferences?.autoCopy!!){
            copyContent(requireContext(),smsResult.toString())
        }
        if (smsResult?.numbers != null && smsResult?.numbers?.get(0)?.isNotEmpty()!!) {
            view?.tvNumber?.text = smsResult?.numbers?.get(0)
        } else {
            view?.tvNumber?.visibility = View.GONE
        }
        if (smsResult?.body != null && smsResult?.body?.isNotEmpty()!!) {
            view?.tvBody?.text = smsResult?.body
        } else {
            view?.tvBody?.visibility = View.GONE
        }
        if (smsResult?.subject != null && smsResult?.subject?.isNotEmpty()!!) {
            view?.tvSubject?.text = smsResult?.subject
        } else {
            view?.tvSubject?.visibility = View.GONE
        }

        view?.sendLayout?.setOnClickListener {
            sendSMS(smsResult?.numbers?.get(0)!!,smsResult?.body,context)
        }

        view?.shareLayout?.setOnClickListener {
            shareContent(requireContext(), smsResult.toString())
        }

        view?.copyLayout?.setOnClickListener {
            copyContent(requireContext(), smsResult.toString())
        }

        view?.viewQrcode?.setOnClickListener {
            viewQrCodeActivity(requireContext(), result)
        }

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(result: Result?) =
            SmsResultFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(RESULT, result)
                }
            }
    }
}