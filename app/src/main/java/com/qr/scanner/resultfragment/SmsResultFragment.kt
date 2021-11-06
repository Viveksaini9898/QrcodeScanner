package com.qr.scanner.resultfragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
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
import kotlinx.android.synthetic.main.fragment_sms_result_fragmet.*
import com.qr.scanner.model.Result

class SmsResultFragment : Fragment() {

    private var userPreferences: UserPreferences? = null
    private var result: Result? = null

    private val barcode by unsafeLazy {
        ParsedResultHandler(result!!)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            result = it.getSerializable(PARSE_RESULT) as Result?
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sms_result_fragmet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userPreferences = UserPreferences(requireContext())


        if (userPreferences?.autoCopy!!){
            copyContent(requireContext(),barcode.text)
        }
        if (barcode.phone.isNullOrEmpty().not()) {
            tvNumber?.text = barcode.phone
        } else {
            tvNumber?.visibility = View.GONE
        }
        if (barcode?.smsBody.isNullOrEmpty().not()) {
            tvBody?.text = barcode.smsBody
        } else {
            tvBody?.visibility = View.GONE
        }

        sendLayout?.setOnClickListener {
            sendSmsOrMms(barcode.phone)
        }

        shareLayout?.setOnClickListener {
            shareContent(requireContext(), barcode.text)
        }

        copyLayout?.setOnClickListener {
            copyContent(requireContext(), barcode.text)
        }

        viewQrcode?.setOnClickListener {
            ViewQRcodeActivity.start(requireContext(), result!!)
        }

    }
    
    
    private fun sendSmsOrMms(phone: String?) {
        val uri = Uri.parse("sms:${phone.orEmpty()}")
        val intent = Intent(Intent.ACTION_SENDTO, uri).apply {
            putExtra("sms_body", barcode.smsBody.orEmpty())
        }
        startIntent(requireContext(),intent)
    }

    companion object {
        @JvmStatic
        fun newInstance(result: Result) =
            SmsResultFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(PARSE_RESULT, result)
                }
            }
    }
}