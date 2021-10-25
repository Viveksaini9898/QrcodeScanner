package com.qr.scanner.resultfragment

import android.content.Context
import android.net.wifi.WifiManager
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.core.Result
import com.core.client.result.WifiParsedResult
import com.qr.scanner.R
import com.qr.scanner.preference.UserPreferences
import com.qr.scanner.result.ResultHandlerFactory
import com.qr.scanner.utils.copyContent
import com.qr.scanner.utils.shareContent
import com.qr.scanner.utils.viewQrCodeActivity
import com.qr.scanner.wifi.WifiConfigManager
import kotlinx.android.synthetic.main.fragment_wifi_result.view.*


class WifiResultFragment : Fragment() {

    private var userPreferences: UserPreferences? = null
    private var wifiResult: WifiParsedResult? = null
    private var result: Result? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            result = it.getParcelable("result")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View? = inflater.inflate(R.layout.fragment_wifi_result, container, false)

        userPreferences = UserPreferences(requireContext())

        val resultHandler = ResultHandlerFactory.makeResultHandler(activity, result)

        wifiResult = resultHandler?.result as WifiParsedResult?

        if (userPreferences?.autoCopy!!){
            copyContent(requireContext(),wifiResult.toString())
        }

        if (wifiResult?.ssid != null && wifiResult?.ssid?.isNotEmpty()!!) {
            view?.ssidLayout?.visibility = View.VISIBLE
            view?.ssid?.text = wifiResult?.ssid
        } else {
            view?.ssidLayout?.visibility = View.GONE
        }
        if (wifiResult?.password != null && wifiResult?.password?.isNotEmpty()!!) {
            view?.passwordLayout?.visibility = View.VISIBLE
            view?.password?.text = wifiResult?.password
        } else {
            view?.passwordLayout?.visibility = View.VISIBLE
        }
        if (wifiResult?.identity != null && wifiResult?.identity?.isNotEmpty()!!) {
            view?.identityLayout?.visibility = View.VISIBLE
            view?.identity?.text = wifiResult?.identity
        } else {
            view?.identityLayout?.visibility = View.GONE

        }
        if (wifiResult?.isHidden != null) {
            view?.hiddenLayout?.visibility = View.VISIBLE
            if (wifiResult?.isHidden!!) {
                view?.hidden?.text = "Yes"
            } else {
                view?.hidden?.text = "No"
            }
        } else {
            view?.hiddenLayout?.visibility = View.GONE
        }
        if (wifiResult?.anonymousIdentity != null && wifiResult?.anonymousIdentity?.isNotEmpty()!!) {
            view?.anonymousLayout?.visibility = View.VISIBLE
            view?.anonymous?.text = wifiResult?.anonymousIdentity
        } else {
            view?.anonymousLayout?.visibility = View.GONE
        }
        if (wifiResult?.networkEncryption != null && wifiResult?.networkEncryption?.isNotEmpty()!!) {
            view?.networkEncryptionLayout?.visibility = View.VISIBLE
            view?.networkEncryption?.text = wifiResult?.networkEncryption
        } else {
            view?.networkEncryptionLayout?.visibility = View.GONE
        }
        // view?.eapMethod?.text = wifiResult?.eapMethod
        // view?.phase2Method?.text = wifiResult?.phase2Method

        view?.connectLayout?.setOnClickListener {
            connectWifi()
        }
        view?.copyLayout?.setOnClickListener {
            copyContent(requireContext(), wifiResult.toString())
        }
        view?.copyPasswordLayout?.setOnClickListener {
            copyContent(requireContext(), wifiResult?.password)
        }

        view?.viewQrcode?.setOnClickListener {
            viewQrCodeActivity(requireContext(), result)

        }
        view?.shareLayout?.setOnClickListener {
            shareContent(requireContext(), wifiResult.toString())

        }


        return view
    }

    private fun connectWifi() {
        val wifiManager =
            activity?.applicationContext?.getSystemService(Context.WIFI_SERVICE) as WifiManager
        if (wifiManager == null) {
            return
        }
        Toast.makeText(
            requireActivity()?.applicationContext,
            R.string.wifi_changing_network,
            Toast.LENGTH_SHORT
        ).show()
        WifiConfigManager(wifiManager).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, wifiResult)
    }


    companion object {
        @JvmStatic
        fun newInstance(result: Result) =
            WifiResultFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("result", result)
                }
            }
    }
}