package com.qr.scanner.resultfragment

import android.content.Context
import android.net.wifi.WifiManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.qr.scanner.R
import com.qr.scanner.activity.ViewQRcodeActivity
import com.qr.scanner.constant.PARSE_RESULT
import com.qr.scanner.extension.unsafeLazy
import com.qr.scanner.preference.UserPreferences
import com.qr.scanner.result.ParsedResultHandler
import com.qr.scanner.utils.copyContent
import com.qr.scanner.utils.shareContent
import kotlinx.android.synthetic.main.fragment_wifi_result.*
import kotlinx.android.synthetic.main.fragment_wifi_result.view.*


class WifiResultFragment : Fragment() {

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
        return inflater.inflate(R.layout.fragment_wifi_result, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userPreferences = UserPreferences(requireContext())

        if (userPreferences?.autoCopy!!) {
            copyContent(requireContext(), barcode.text)
        }

        if (barcode.networkName.isNullOrEmpty().not()) {
            ssidLayout?.visibility = View.VISIBLE
            ssid?.text = barcode.networkName
        } else {
            ssidLayout?.visibility = View.GONE
        }
        if (barcode?.networkPassword.isNullOrEmpty().not()) {
            passwordLayout?.visibility = View.VISIBLE
            password?.text = barcode?.networkPassword
        } else {
            passwordLayout?.visibility = View.VISIBLE
        }
        if (barcode.identity.isNullOrEmpty().not()) {
            identityLayout?.visibility = View.VISIBLE
            identity?.text = barcode.identity
        } else {
            identityLayout?.visibility = View.GONE

        }
        if (barcode?.isHidden != null) {
            hiddenLayout?.visibility = View.VISIBLE
            if (barcode?.isHidden!!) {
                hidden?.text = "Yes"
            } else {
                hidden?.text = "No"
            }
        } else {
            hiddenLayout?.visibility = View.GONE
        }
        if (barcode?.anonymousIdentity.isNullOrEmpty().not()) {
            anonymousLayout?.visibility = View.VISIBLE
            anonymous?.text = barcode?.anonymousIdentity
        } else {
            anonymousLayout?.visibility = View.GONE
        }
        if (barcode?.networkAuthType.isNullOrEmpty().not()) {
            networkEncryptionLayout?.visibility = View.VISIBLE
            networkEncryption?.text = barcode?.networkAuthType
        } else {
            networkEncryptionLayout?.visibility = View.GONE
        }
        // eapMethod?.text = wifiResult?.eapMethod
        // phase2Method?.text = wifiResult?.phase2Method

        connectLayout?.setOnClickListener {
            connectWifi()
        }
        copyLayout?.setOnClickListener {
            copyContent(requireContext(), barcode.text)
        }
        copyPasswordLayout?.setOnClickListener {
            copyContent(requireContext(), barcode?.networkPassword)
        }

        viewQrcode?.setOnClickListener {
            ViewQRcodeActivity.start(requireContext(), result!!)

        }
        shareLayout?.setOnClickListener {
            shareContent(requireContext(), barcode.text)
        }


    }

    private fun connectWifi() {
        val wifiManager =
            activity?.applicationContext?.getSystemService(Context.WIFI_SERVICE) as WifiManager
                ?: return
        Toast.makeText(
            requireActivity()?.applicationContext,
            R.string.wifi_changing_network,
            Toast.LENGTH_SHORT
        ).show()
        //  WifiConfigManager(wifiManager).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, wifiResult)
    }


    companion object {
        @JvmStatic
        fun newInstance(result: com.qr.scanner.model.Result?) =
            WifiResultFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(PARSE_RESULT, result)
                }
            }
    }
}