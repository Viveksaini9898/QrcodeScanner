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
import kotlinx.android.synthetic.main.fragment_contact_result.*
import com.qr.scanner.model.Result

class ContactResultFragment : Fragment() {


    private var userPreferences: UserPreferences? = null
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
        return inflater.inflate(R.layout.fragment_contact_result, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userPreferences = UserPreferences(requireContext())

        if (userPreferences?.autoCopy!!) {
            copyContent(requireContext(), barcode.text)
        }
        val fullName = "${barcode.firstName.orEmpty()} ${barcode.lastName.orEmpty()}"

        if (fullName.isNullOrEmpty().not()) {
            tvName?.text = fullName
        } else {
            tvName?.visibility = View.GONE
        }
        if ((barcode?.organization.isNullOrEmpty().not()) && (barcode.jobTitle.isNullOrEmpty()
                .not())
        ) {
            tvOrg?.text = "${barcode.organization.orEmpty()} ${barcode.jobTitle.orEmpty()}"
        } else {
            tvOrg?.visibility = View.GONE
        }
        if (barcode.url.isNullOrEmpty().not()) {
            tvWebsite?.text = barcode.url
        } else {
            websiteLayout?.visibility = View.GONE
        }

        if (barcode.phone.isNullOrEmpty().not()) {
            numberLayout?.visibility = View.VISIBLE
            tvNumber?.text = barcode.phone
        } else {
            tvNumber?.visibility = View.GONE
        }

        if (barcode.secondaryPhone.isNullOrEmpty().not()) {
            numberLayout1?.visibility = View.VISIBLE
            tvNumber1?.text = barcode.secondaryPhone
        } else {
            tvNumber1?.visibility = View.GONE
        }
        if (barcode.tertiaryPhone.isNullOrEmpty().not()) {
            numberLayout2?.visibility = View.VISIBLE
            tvNumber2?.text = barcode.tertiaryPhone
        } else {
            tvNumber2?.visibility = View.GONE
        }

        if (barcode?.email.isNullOrEmpty().not()) {
            tvEmail?.text = barcode.email
        } else {
            emailLayout?.visibility = View.GONE
            sendEmailLayout?.visibility = View.GONE
        }
        if (barcode.address.isNullOrEmpty().not()) {
            tvAddress?.text = barcode.address
        } else {
            addressLayout?.visibility = View.GONE
            mapLayout?.visibility = View.GONE
        }

        shareLayout?.setOnClickListener {
            shareContent(requireContext(), barcode.text)
        }

        copyLayout?.setOnClickListener {
            copyContent(requireContext(), barcode.text)
        }

        callLayout?.setOnClickListener {
            if (barcode.phone.isNullOrEmpty().not()) {
                callPhone(requireContext(), barcode.phone)
            }
        }
        sendEmailLayout?.setOnClickListener {
            sendEmail(requireContext(),barcode.email,null,null)
        }
        mapLayout?.setOnClickListener {
            searchMap(requireContext(),barcode.address)
        }

        sendMessageLayout?.setOnClickListener {
            sendSMS(barcode.phone!!, requireContext())
        }
        addContactLayout?.setOnClickListener {
            addToContacts(requireContext(), barcode)
        }

        viewQrcode?.setOnClickListener {
            ViewQRcodeActivity.start(requireContext(), result!!)
        }

    }

    companion object {
        @JvmStatic
        fun newInstance(result: Result) =
            ContactResultFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(PARSE_RESULT, result)
                }
            }
    }
}