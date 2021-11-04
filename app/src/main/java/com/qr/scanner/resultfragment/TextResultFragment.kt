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
import com.qr.scanner.utils.*
import kotlinx.android.synthetic.main.fragment_text_result.*


class TextResultFragment : Fragment() {

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
        return inflater.inflate(R.layout.fragment_text_result, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userPreferences = UserPreferences(requireContext())


        if (userPreferences?.autoCopy!!) {
            copyContent(requireContext(), barcode.text)
        }

        if (!barcode?.text.isNullOrEmpty()) {
            text?.text = barcode?.text
        } else {
            text?.text = "None"
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
        searchLayout?.setOnClickListener {
            searchContent(requireContext(), barcode.text)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(result: com.qr.scanner.model.Result?) =
            TextResultFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(PARSE_RESULT, result)
                }
            }
    }
}