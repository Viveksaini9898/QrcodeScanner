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
import kotlinx.android.synthetic.main.fragment_url_result.*
import kotlinx.android.synthetic.main.fragment_url_result.view.*


class UrlResultFragment : Fragment() {

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
        return inflater.inflate(R.layout.fragment_url_result, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userPreferences = UserPreferences(requireContext())

        if (userPreferences?.autoCopy!!){
            copyContent(requireContext(),barcode.url)
        }

        if (barcode.url.isNullOrEmpty().not()) {
            text?.text = barcode.url
        }else {
            text?.text = "None"
        }

        if (barcode.bookmarkTitle.isNullOrEmpty().not()) {
            title?.text = barcode.bookmarkTitle
        }else {
            title?.visibility = View.GONE
        }

        when {
            isYoutubeUrl(barcode.url) -> {
                topImage?.setImageResource(R.drawable.ic_youtube)
            }
            isTwitterUrl(barcode.url) -> {
                topImage?.setImageResource(R.drawable.ic_twitter)
            }
            isFacebookUrl(barcode.url) -> {
                topImage?.setImageResource(R.drawable.ic_facebook)
            }
            isInstagramUrl(barcode.url) -> {
                topImage?.setImageResource(R.drawable.ic_instagram)
            }
            else -> {
                topImage?.setImageResource(R.drawable.ic_link_black_24dp)
            }
        }


        shareLayout?.setOnClickListener {
            shareContent(requireContext(), barcode.text)
        }

        copyLayout?.setOnClickListener {
            copyContent(requireContext(), barcode.text)
        }

        openLinkLayout?.setOnClickListener {
            searchContent(requireContext(), barcode.url)
        }

        viewQrcode?.setOnClickListener {
              ViewQRcodeActivity.start(requireContext(), result!!)
        }

    }

    companion object {
        @JvmStatic
        fun newInstance(result: com.qr.scanner.model.Result) =
            UrlResultFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(PARSE_RESULT, result)
                }
            }
    }
}