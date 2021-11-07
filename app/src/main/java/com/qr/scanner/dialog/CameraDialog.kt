package com.qr.scanner.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.qr.scanner.R
import com.qr.scanner.preference.UserPreferences
import kotlinx.android.synthetic.main.fragment_camera_dialog.*


class CameraDialog : DialogFragment() {

    companion object {
        private const val MESSAGE_ID_KEY = "MESSAGE_ID_KEY"

        fun newInstance(messageId: Int): CameraDialog {
            return CameraDialog().apply {
                arguments = Bundle().apply {
                    putInt(MESSAGE_ID_KEY, messageId)
                }
                isCancelable = false
            }
        }
    }

    val userPreferences by lazy {
        UserPreferences(requireContext())
    }

    private var customView: View? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return customView
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        customView = layoutInflater.inflate(R.layout.fragment_camera_dialog, null)


        return AlertDialog.Builder(requireActivity(), R.style.DialogAlertTheme)
            .setView(customView)
            .setPositiveButton(R.string.button_ok) { _, _ -> }
            .create()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        backRadioButton?.isChecked = userPreferences.backCamera
        frontRadioButton?.isChecked = userPreferences.backCamera.not()

        backRadioButton?.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                frontRadioButton?.isChecked = false
            }
            userPreferences?.backCamera = isChecked
        }
        frontRadioButton?.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                backRadioButton?.isChecked = false
            }
            userPreferences.backCamera = isChecked.not()
        }
    }
}