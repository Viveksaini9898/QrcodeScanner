package com.qr.scanner.resultfragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.qr.scanner.R
import com.qr.scanner.activity.ViewBarcodeActivity
import com.qr.scanner.constant.PARSE_RESULT
import com.qr.scanner.extension.unsafeLazy
import com.qr.scanner.model.Result
import com.qr.scanner.preference.UserPreferences
import com.qr.scanner.result.ParsedResultHandler
import com.qr.scanner.utils.*
import kotlinx.android.synthetic.main.fragment_product_result.*

class ProductResultFragment : Fragment() {

    private var result: Result? = null
    private val barcode by unsafeLazy {
        ParsedResultHandler(result!!)
    }
    private val userPreferences by unsafeLazy {
        UserPreferences(requireContext())
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
        return inflater.inflate(R.layout.fragment_product_result, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (userPreferences?.autoCopy!!){
            copyContent(requireContext(),barcode.text)
        }

        if (barcode.text.isNullOrEmpty().not()) {
            text?.text = barcode.text
        } else {
            text?.text = "Not Found"
        }

        copyLayout?.setOnClickListener {
            copyContent(requireContext(), barcode.text)
        }

        viewQrCode?.setOnClickListener {
            val intent = Intent(requireContext(), ViewBarcodeActivity::class.java)
            intent.putExtra("product", barcode.text)
            startActivity(intent)
        }
        shareLayout?.setOnClickListener {
            shareContent(requireContext(), barcode.text)
        }
        googleLayout?.setOnClickListener {
            searchContent(requireContext(), barcode.text)
        }

        flipkartLayout?.setOnClickListener {
            flipkartContent(requireContext(), barcode.text)
        }
        ebayLayout?.setOnClickListener {
            ebayContent(requireContext(), barcode.text)
        }
        amazonLayout?.setOnClickListener {
            amazonContent(requireContext(), barcode.text)
        }

    }

    companion object {
        @JvmStatic
        fun newInstance(result: Result) =
            ProductResultFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(PARSE_RESULT, result)
                }
            }
    }
}