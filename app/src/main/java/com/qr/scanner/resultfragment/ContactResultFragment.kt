package com.qr.scanner.resultfragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.core.Result
import com.core.client.result.AddressBookParsedResult
import com.core.client.result.EmailAddressParsedResult
import com.qr.scanner.R
import com.qr.scanner.constant.RESULT
import com.qr.scanner.preference.UserPreferences
import com.qr.scanner.result.ResultHandlerFactory
import com.qr.scanner.utils.*
import kotlinx.android.synthetic.main.fragment_contact_result.view.*

class ContactResultFragment : Fragment() {


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

        val view: View = inflater.inflate(R.layout.fragment_contact_result, container, false)

        userPreferences = UserPreferences(requireContext())

        val resultHandler = ResultHandlerFactory.makeResultHandler(activity, result)

        val contactResult = resultHandler.result as AddressBookParsedResult?

        if (userPreferences?.autoCopy!!){
            copyContent(requireContext(),contactResult.toString())
        }
        if (contactResult?.names != null && contactResult?.names?.isNotEmpty()!!) {
            view?.tvName?.text = contactResult?.names[0]
        } else {
            view?.tvName?.visibility = View.GONE
        }
        if ((contactResult?.org != null && contactResult?.org?.isNotEmpty()!!) && (contactResult?.title != null && contactResult?.title.isNotEmpty()!!)) {
            view?.tvOrg?.text = contactResult?.org + " , " + contactResult?.title
        } else {
            view?.tvOrg?.visibility = View.GONE
        }
        if (contactResult?.urLs != null && contactResult?.urLs?.isNotEmpty()!!) {
            view?.tvWebsite?.text = contactResult?.urLs[0]
        } else {
            view?.websiteLayout?.visibility = View.GONE
        }
        if (contactResult?.phoneNumbers != null && contactResult?.phoneNumbers?.isNotEmpty()!!) {
            for (number in contactResult?.phoneNumbers.indices) {
                when (number) {
                    0 -> {
                        if (number != null) {
                            view?.numberLayout?.visibility = View.VISIBLE
                            view?.tvNumber?.text = contactResult?.phoneNumbers[number]
                        }
                    }
                    1 -> {
                        if (number != null) {
                            view?.numberLayout1?.visibility = View.VISIBLE
                            view?.tvNumber1?.text = contactResult?.phoneNumbers[number]
                        }
                    }
                    2 -> {
                        if (number != null) {
                            view?.numberLayout2?.visibility = View.VISIBLE
                            view?.tvNumber2?.text = contactResult?.phoneNumbers[number]
                        }
                    }
                    3 -> {
                        if (number != null) {
                            view?.numberLayout3?.visibility = View.VISIBLE
                            view?.tvNumber3?.text = contactResult?.phoneNumbers[number]
                        }
                    }
                    4 -> {
                        if (number != null) {
                            view?.numberLayout4?.visibility = View.VISIBLE
                            view?.tvNumber4?.text = contactResult?.phoneNumbers[number]
                        }
                    }
                }
            }
        }
        if (contactResult?.emails != null && contactResult?.emails?.isNotEmpty()!!) {
            view?.tvEmail?.text = contactResult?.emails[0]
        } else {
            view?.emailLayout?.visibility = View.GONE
        }
        if (contactResult?.addresses != null && contactResult?.addresses?.isNotEmpty()!!) {
            view?.tvAddress?.text = contactResult?.addresses[0].replace("\n", " ")
        } else {
            view?.addressLayout?.visibility = View.GONE
            view?.mapLayout?.visibility = View.GONE
        }

        view?.shareLayout?.setOnClickListener {
            shareContent(requireContext(), contactResult.toString())
        }

        view?.copyLayout?.setOnClickListener {
            copyContent(requireContext(), contactResult.toString())
        }

        view?.callLayout?.setOnClickListener {
            if (contactResult?.phoneNumbers != null && contactResult?.phoneNumbers?.isNotEmpty()!!) {
                dialPhone(contactResult?.phoneNumbers[0], requireContext())
            }
        }
        view?.sendEmailLayout?.setOnClickListener {
            resultHandler?.sendEmail(contactResult?.emails, null, null, null, null)
        }
        view?.mapLayout?.setOnClickListener {
            resultHandler?.searchMap(contactResult?.addresses!![0])
        }

        view?.sendMessageLayout?.setOnClickListener {
            resultHandler?.sendSMS(contactResult?.phoneNumbers!![0], null)
        }
        view?.addContactLayout?.setOnClickListener {
            if (contactResult != null) {
                val addresses: Array<String>? = contactResult?.addresses
                val address1 = if (addresses == null || addresses.isEmpty()) null else addresses[0]
                val addressTypes: Array<String>? = contactResult?.addressTypes
                val address1Type =
                    if (addressTypes == null || addressTypes.isEmpty()) null else addressTypes[0]
                resultHandler.addContact(
                    contactResult?.names,
                    contactResult?.nicknames,
                    contactResult?.pronunciation,
                    contactResult?.phoneNumbers,
                    contactResult?.phoneTypes,
                    contactResult?.emails,
                    contactResult?.emailTypes,
                    contactResult?.note,
                    contactResult?.instantMessenger,
                    address1,
                    address1Type,
                    contactResult?.org,
                    contactResult?.title,
                    contactResult?.urLs,
                    contactResult?.birthday,
                    contactResult?.geo
                )
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
            ContactResultFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(RESULT, result)
                }
            }
    }
}