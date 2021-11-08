package com.qr.scanner.resultfragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.qr.scanner.R
import com.qr.scanner.activity.ViewQrCodeActivity
import com.qr.scanner.constant.PARSE_RESULT
import com.qr.scanner.extension.unsafeLazy
import com.qr.scanner.model.Result
import com.qr.scanner.preference.UserPreferences
import com.qr.scanner.result.ParsedResultHandler
import kotlinx.android.synthetic.main.fragment_app_result.*

import com.qr.scanner.utils.*


class AppResultFragment : Fragment() {

    private val userPreferences by unsafeLazy {
        UserPreferences(requireContext())
    }
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_app_result, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (userPreferences?.autoCopy!!) {
            copyContent(requireContext(), barcode.appMarketUrl)
        }

        if (barcode?.appMarketUrl.isNullOrEmpty().not()) {
            text?.text = barcode?.appMarketUrl
        } else {
            text?.text = "None"
        }

        if (barcode.appPackage.isNullOrEmpty()
                .not() && requireContext().packageManager.isAppInstalled(
                barcode.appPackage!!
            )
        ) {
            topImage?.setImageDrawable(requireContext()?.appLauncherIcon(barcode.appPackage))
        } else {
            topImage?.setImageResource(R.drawable.ic_app_black_24dp)
        }

        copyLayout?.setOnClickListener {
            copyContent(requireContext(), barcode.appMarketUrl)
        }

        viewQrCode?.setOnClickListener {
            ViewQrCodeActivity.start(requireContext(), result!!)
        }
        shareLayout?.setOnClickListener {
            shareContent(requireContext(), barcode.appMarketUrl)
        }
        openAppLayout?.setOnClickListener {
            openApp()
        }
        openMarketLayout?.setOnClickListener {
            openInAppMarket()
        }

    }


    private fun openApp() {
        val intent =
            requireContext().packageManager?.getLaunchIntentForPackage(barcode.appPackage.orEmpty())
        if (intent != null) {
            startIntent(requireContext(), intent)
        }
    }

    private fun openInAppMarket() {
        startIntent(requireContext(), Intent.ACTION_VIEW, barcode.appMarketUrl.orEmpty())
    }


    companion object {
        @JvmStatic
        fun newInstance(result: Result) =
            AppResultFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(PARSE_RESULT, result)
                }
            }
    }
}