package com.qr.scanner.generatefragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import com.core.BarcodeFormat
import com.core.Result
import com.qr.scanner.R
import com.qr.scanner.extension.isNotBlank
import com.qr.scanner.extension.textString
import com.qr.scanner.utils.viewQrCodeActivity
import ezvcard.Ezvcard
import ezvcard.VCardVersion
import ezvcard.property.*
import kotlinx.android.synthetic.main.fragment_contact_generate.*

class ContactGenerateFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contact_generate, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleTextChanged()
        initEditText()

        generateQrcode?.setOnClickListener {
            val result = Result(
                toBarcodeText(
                    edit_text_first_name.textString,
                    edit_text_last_name.textString,
                    null,
                    edit_text_organization.textString,
                    edit_text_job.textString,
                    edit_text_email.textString,
                    null,
                    null,
                    edit_text_phone.textString,
                    edit_text_fax.textString,
                    edit_text_website.textString
                ), null, null, BarcodeFormat.QR_CODE
            )
            viewQrCodeActivity(requireContext(), result)
        }
    }

    private fun handleTextChanged() {
        edit_text_first_name.addTextChangedListener { toggleCreateBarcodeButton() }
        edit_text_last_name.addTextChangedListener { toggleCreateBarcodeButton() }
        edit_text_phone.addTextChangedListener { toggleCreateBarcodeButton() }
    }

    private fun initEditText() {
        edit_text_first_name.requestFocus()
    }

    private fun toggleCreateBarcodeButton() {
        if (edit_text_first_name.isNotBlank() || edit_text_last_name.isNotBlank() || edit_text_phone.isNotBlank()) {
            generateQrcode?.background = resources?.getDrawable(R.drawable.button_background_1)
        } else {
            generateQrcode?.background = resources?.getDrawable(R.drawable.circle_button_background)
        }
    }

    private fun toBarcodeText(
        firstName: String?,
        lastName: String?,
        nickname: String?,
        organization: String?,
        title: String?,
        email: String?,
        secondaryEmail: String?,
        tertiaryEmail: String?,
        phone: String?,
        secondaryPhone: String?,
        url: String?
    ): String {
        val vCard = ezvcard.VCard()

        vCard.structuredName = StructuredName().apply {
            given = firstName
            family = lastName
        }

        if (nickname.isNullOrBlank().not()) {
            vCard.nickname = Nickname().apply { values.add(nickname) }
        }

        if (organization.isNullOrBlank().not()) {
            vCard.organization = Organization().apply { values.add(organization) }
        }

        if (title.isNullOrBlank().not()) {
            vCard.addTitle(Title(title))
        }

        if (email.isNullOrBlank().not()) {
            vCard.addEmail(Email(email))
        }

        if (secondaryEmail.isNullOrBlank().not()) {
            vCard.addEmail(Email(secondaryEmail))
        }

        if (tertiaryEmail.isNullOrBlank().not()) {
            vCard.addEmail(Email(tertiaryEmail))
        }

        if (phone.isNullOrBlank().not()) {
            vCard.addTelephoneNumber(Telephone(phone))
        }

        if (secondaryPhone.isNullOrBlank().not()) {
            vCard.addTelephoneNumber(Telephone(secondaryPhone))
        }


        if (url.isNullOrBlank().not()) {
            vCard.addUrl(Url(url))
        }

        return Ezvcard
            .write(vCard)
            .version(VCardVersion.V4_0)
            .prodId(false)
            .go()
            .trimEnd('\n', '\r', ' ')
    }
}