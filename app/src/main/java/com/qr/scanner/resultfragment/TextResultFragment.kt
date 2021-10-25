package com.qr.scanner.resultfragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.core.Result
import com.core.client.result.TextParsedResult
import com.qr.scanner.R
import com.qr.scanner.preference.UserPreferences
import com.qr.scanner.result.ResultHandlerFactory
import com.qr.scanner.utils.*
import kotlinx.android.synthetic.main.fragment_text_result.view.*


class TextResultFragment : Fragment() {

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
        val view: View? = inflater.inflate(R.layout.fragment_text_result, container, false)

        userPreferences = UserPreferences(requireContext())

        val resultHandler = ResultHandlerFactory.makeResultHandler(activity,result)

        val textResult = resultHandler?.result as TextParsedResult

        if (userPreferences?.autoCopy!!){
            copyContent(requireContext(),textResult.toString())
        }

        if (textResult?.text != null && textResult?.text?.isNotEmpty()!!) {
            view?.text?.text = textResult?.text
        }else {
            view?.text?.text = "None"
        }

        view?.copyLayout?.setOnClickListener {
            copyContent(requireContext(),textResult.toString())
        }

        view?.viewQrCode?.setOnClickListener {
            viewQrCodeActivity(requireContext(), result)
        }
        view?.shareLayout?.setOnClickListener {
            shareContent(requireContext(),textResult.toString())
        }
        view?.searchLayout?.setOnClickListener {
            searchContent(requireContext(),textResult.toString())
        }
        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(result: Result?) =
            TextResultFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(RESULT, result)
                }
            }
    }
}