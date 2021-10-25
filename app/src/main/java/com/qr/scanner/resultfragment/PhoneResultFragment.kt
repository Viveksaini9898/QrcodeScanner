package com.qr.scanner.resultfragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.core.Result
import com.core.client.result.TelParsedResult

import com.qr.scanner.R
import com.qr.scanner.preference.UserPreferences
import com.qr.scanner.result.ResultHandler
import com.qr.scanner.result.ResultHandlerFactory
import com.qr.scanner.utils.*
import kotlinx.android.synthetic.main.fragment_phone_result.view.*


class PhoneResultFragment : Fragment() {

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
        val view: View? = inflater.inflate(R.layout.fragment_phone_result, container, false)

        userPreferences = UserPreferences(requireContext())

        val resultHandler = ResultHandlerFactory.makeResultHandler(activity, result)

        val telResult = resultHandler?.result as TelParsedResult?

        if (userPreferences?.autoCopy!!){
            copyContent(requireContext(),telResult.toString())
        }

        if (telResult?.number != null && telResult?.number?.isNotEmpty()!!) {
            view?.tvNumber?.text = telResult?.number
        } else {
            view?.tvNumber?.visibility = View.GONE
        }

        view?.shareLayout?.setOnClickListener {
            shareContent(requireContext(), telResult.toString())
        }

        view?.copyLayout?.setOnClickListener {
            copyContent(requireContext(), telResult.toString())
        }

        view?.callLayout?.setOnClickListener {
            if (telResult?.number != null && telResult?.number?.isNotEmpty()!!) {
                dialPhone(telResult?.number, requireContext())
            }
        }
        view?.addContactLayout?.setOnClickListener {
            if (telResult?.number != null && telResult?.number?.isNotEmpty()!!) {
                resultHandler.addPhoneOnlyContact(arrayOf(telResult.number),null)
            }
        }

        view?.viewQrcode?.setOnClickListener {
            viewQrCodeActivity(requireContext(), result)
        }

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(result: Result?) =
            PhoneResultFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(RESULT, result)
                }
            }
    }
}