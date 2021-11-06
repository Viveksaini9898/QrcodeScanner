package com.qr.scanner.resultfragment

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
import com.qr.scanner.utils.copyContent
import com.qr.scanner.utils.sendEmail
import com.qr.scanner.utils.shareContent
import kotlinx.android.synthetic.main.fragment_email_result.*
import kotlinx.android.synthetic.main.fragment_email_result.view.*

class EmailResultFragment : Fragment() {

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
        
        return inflater.inflate(R.layout.fragment_email_result, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userPreferences = UserPreferences(requireContext())


        if (userPreferences?.autoCopy!!) {
            copyContent(requireContext(), barcode.text)
        }
        if (barcode.email.isNullOrEmpty().not()) {
            tvEmail?.text = barcode.email
        } else {
            tvEmail?.visibility = View.GONE
        }
        if (barcode.emailBody.isNullOrEmpty().not()) {
            tvBody?.text = barcode.emailBody
        } else {
            tvBody?.visibility = View.GONE
        }
        if (barcode?.emailSubject.isNullOrEmpty().not()) {
            tvSubject?.text = barcode?.emailSubject
        } else {
            tvSubject?.visibility = View.GONE
        }

        sendLayout?.setOnClickListener {
            sendEmail(requireContext(),barcode.email,barcode.emailSubject.orEmpty(),barcode.emailBody.orEmpty())
        }

        shareLayout?.setOnClickListener {
            shareContent(requireContext(), barcode.text)
        }

        copyLayout?.setOnClickListener {
            copyContent(requireContext(), barcode.text)
        }
        copyMessageLayout?.setOnClickListener {
            copyContent(requireContext(), barcode?.emailBody)
        }

        viewQrcode?.setOnClickListener {
            ViewQRcodeActivity.start(requireContext(), result!!)
        }

    }

    companion object {
        @JvmStatic
        fun newInstance(result: com.qr.scanner.model.Result) =
            EmailResultFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(PARSE_RESULT, result)
                }
            }
    }
}