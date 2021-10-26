package com.qr.scanner.resultfragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.core.Result
import com.core.client.result.EmailAddressParsedResult
import com.core.client.result.ParsedResultType
import com.core.client.result.SMSParsedResult
import com.qr.scanner.R
import com.qr.scanner.constant.RESULT
import com.qr.scanner.preference.UserPreferences
import com.qr.scanner.result.ResultHandlerFactory
import com.qr.scanner.utils.*
import kotlinx.android.synthetic.main.fragment_email_result.view.*

class EmailResultFragment : Fragment() {

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
        val view: View? = inflater.inflate(R.layout.fragment_email_result, container, false)

        userPreferences = UserPreferences(requireContext())

        val resultHandler = ResultHandlerFactory.makeResultHandler(activity, result)

        val emailResult = resultHandler.result as EmailAddressParsedResult?

        if (userPreferences?.autoCopy!!){
            copyContent(requireContext(),emailResult.toString())
        }
        if (emailResult?.emailAddress != null && emailResult?.emailAddress?.isNotEmpty()!!) {
            view?.tvEmail?.text = emailResult?.emailAddress
        } else {
            view?.tvEmail?.visibility = View.GONE
        }
        if (emailResult?.body != null && emailResult?.body?.isNotEmpty()!!) {
            view?.tvBody?.text = emailResult?.body
        } else {
            view?.tvBody?.visibility = View.GONE
        }
        if (emailResult?.subject != null && emailResult?.subject?.isNotEmpty()!!) {
            view?.tvSubject?.text = emailResult?.subject
        } else {
            view?.tvSubject?.visibility = View.GONE
        }

        view?.sendLayout?.setOnClickListener {
            resultHandler?.sendEmail(
                arrayOf(emailResult?.emailAddress),
                null,
                null,
                emailResult?.subject,
                emailResult?.body
            )
        }

        view?.shareLayout?.setOnClickListener {
            shareContent(requireContext(), emailResult.toString())
        }

        view?.copyLayout?.setOnClickListener {
            copyContent(requireContext(), emailResult.toString())
        }
        view?.copyMessageLayout?.setOnClickListener {
            copyContent(requireContext(), emailResult?.body)
        }

        view?.viewQrcode?.setOnClickListener {
            viewQrCodeActivity(requireContext(), result)
        }

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(result: Result?) =
            EmailResultFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(RESULT, result)
                }
            }
    }
}