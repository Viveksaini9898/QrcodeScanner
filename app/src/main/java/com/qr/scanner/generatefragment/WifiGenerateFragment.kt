package com.qr.scanner.generatefragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.core.BarcodeFormat
import com.core.Result
import com.qr.scanner.R
import com.qr.scanner.constant.*
import com.qr.scanner.extension.appendIfNotNullOrBlank
import com.qr.scanner.extension.isNotBlank
import com.qr.scanner.extension.textString
import com.qr.scanner.utils.viewQrCodeActivity
import kotlinx.android.synthetic.main.fragment_wifi_generate.*

class WifiGenerateFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_wifi_generate, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initEncryptionTypesSpinner()
        initNetworkNameEditText()
        handleTextChanged()

        val encryption = when (spinner_encryption.selectedItemPosition) {
            0 -> "WPA"
            1 -> "WEP"
            2 -> "nopass"
            else -> "nopass"
        }

        generateQrcode?.setOnClickListener {
            if (edit_text_network_name?.isNotBlank()!!) {
                val result = Result(
                    toBarcodeText(
                        encryption,
                        edit_text_network_name.textString,
                        edit_text_password.textString,
                        check_box_is_hidden.isChecked
                    ), null, null, BarcodeFormat.QR_CODE
                )
                viewQrCodeActivity(requireContext(), result)
            } else {
                //edit_text?.error = "Invalid Number"
            }
        }
    }


    private fun initEncryptionTypesSpinner() {
        spinner_encryption.adapter = ArrayAdapter.createFromResource(
            requireContext(), R.array.encryption_types, R.layout.item_spinner
        ).apply {
            setDropDownViewResource(R.layout.item_spinner_dropdown)
        }

        spinner_encryption.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                text_input_layout_password.isVisible = position != 2
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }

    private fun initNetworkNameEditText() {
        edit_text_network_name.requestFocus()
    }

    private fun handleTextChanged() {
        edit_text_network_name.addTextChangedListener { toggleCreateBarcodeButton() }
        edit_text_password.addTextChangedListener { toggleCreateBarcodeButton() }
    }

    private fun toggleCreateBarcodeButton() {
        if (edit_text_network_name.isNotBlank()) {
            generateQrcode?.background = resources?.getDrawable(R.drawable.button_background_1)
        } else {
            generateQrcode?.background = resources?.getDrawable(R.drawable.circle_button_background)
        }
    }

    private fun toBarcodeText(
        encryption: String?,
        name: String?,
        password: String?,
        isHidden: Boolean
    ): String {
        return StringBuilder()
            .append(SCHEMA_PREFIX)
            .appendIfNotNullOrBlank(ENCRYPTION_PREFIX, encryption, WIFI_SEPARATOR)
            .appendIfNotNullOrBlank(NAME_PREFIX, name, WIFI_SEPARATOR)
            .appendIfNotNullOrBlank(PASSWORD_PREFIX, password, WIFI_SEPARATOR)
            .appendIfNotNullOrBlank(IS_HIDDEN_PREFIX, isHidden?.toString(), WIFI_SEPARATOR)
            .append(WIFI_SEPARATOR)
            .toString()
    }
}