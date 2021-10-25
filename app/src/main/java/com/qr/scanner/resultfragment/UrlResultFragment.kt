package com.qr.scanner.resultfragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.core.Result
import com.core.client.result.URIParsedResult
import com.qr.scanner.R
import com.qr.scanner.preference.UserPreferences
import com.qr.scanner.result.ResultHandlerFactory
import com.qr.scanner.utils.*
import kotlinx.android.synthetic.main.fragment_url_result.view.*


class UrlResultFragment : Fragment() {

    private var userPreferences: UserPreferences? = null
    private var resultData: String? = null
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
        val view: View? = inflater.inflate(R.layout.fragment_url_result, container, false)

        userPreferences = UserPreferences(requireContext())

        val resultHandler = ResultHandlerFactory.makeResultHandler(activity, result)

        val urlResult = resultHandler?.result as URIParsedResult

        if (userPreferences?.autoCopy!!){
            copyContent(requireContext(),urlResult.toString())
        }
        view?.openLinkLayout?.visibility = View.VISIBLE
        if (urlResult?.uri != null && urlResult?.uri?.isNotEmpty()!!) {
            view?.text?.text = urlResult.uri
            resultData = urlResult.uri
        }else {
            view?.text?.text = "None"
        }

        when {
            isYoutubeUrl(resultData) -> {
                view?.topImage?.setImageResource(R.drawable.ic_youtube)
            }
            isTwitterUrl(resultData) -> {
                view?.topImage?.setImageResource(R.drawable.ic_twitter)
            }
            isFacebookUrl(resultData) -> {
                view?.topImage?.setImageResource(R.drawable.ic_facebook)
            }
            isInstagramUrl(resultData) -> {
                view?.topImage?.setImageResource(R.drawable.ic_instagram)
            }
            else -> {
                view?.topImage?.setImageResource(R.drawable.ic_link_black_24dp)
            }
        }


        view?.shareLayout?.setOnClickListener {
            shareContent(requireContext(), resultData)
        }

        view?.copyLayout?.setOnClickListener {
            copyContent(requireContext(), resultData)
        }

        view?.openLinkLayout?.setOnClickListener {
            searchContent(requireContext(), resultData)
        }

        view?.viewQrcode?.setOnClickListener {
            viewQrCodeActivity(requireContext(), result)
        }

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(result: Result?) =
            UrlResultFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(RESULT, result)
                }
            }
    }
}