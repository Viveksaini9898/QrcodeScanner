package com.qr.scanner.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import com.qr.scanner.R
import com.qr.scanner.activity.SupportedFormatsActivity
import com.qr.scanner.dialog.CameraDialog
import com.qr.scanner.preference.UserPreferences
import com.qr.scanner.viewmodel.HistoryViewModel
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.fragment_settings.view.*
import kotlinx.android.synthetic.main.toolbar.*


class SettingsFragment : Fragment() {

    private val viewModel by lazy {
        ViewModelProvider(this).get(HistoryViewModel::class.java)
    }

    private var userPreferences: UserPreferences? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userPreferences = UserPreferences(requireContext())

        if (toolbar != null) {
            toolbar?.title = "Settings"
        }
        sound?.isChecked = userPreferences?.sound!!
        vibrate?.isChecked = userPreferences?.vibrate!!
        autoCopy?.isChecked = userPreferences?.autoCopy!!
        autoFocus?.isChecked = userPreferences?.autoFocus!!
        flash?.isChecked = userPreferences?.flash!!
        doNotSaveDuplicates?.isChecked = userPreferences?.doNotSaveDuplicates!!

        sound?.setOnCheckedChangeListener { buttonView, isChecked ->
            userPreferences?.sound = isChecked
        }

        vibrate?.setOnCheckedChangeListener { buttonView, isChecked ->
            userPreferences?.vibrate = isChecked

        }
        autoCopy?.setOnCheckedChangeListener { buttonView, isChecked ->
            userPreferences?.autoCopy = isChecked

        }
        autoFocus?.setOnCheckedChangeListener { buttonView, isChecked ->
            userPreferences?.autoFocus = isChecked

        }
        flash?.setOnCheckedChangeListener { buttonView, isChecked ->
            userPreferences?.flash = isChecked

        }
        doNotSaveDuplicates?.setOnCheckedChangeListener { buttonView, isChecked ->
            userPreferences?.doNotSaveDuplicates = isChecked

        }

        clearHistory?.setOnClickListener {
            confirmDelete()
        }

        cameraLayout?.setOnClickListener {
            CameraDialog.newInstance(R.string.camera).show(childFragmentManager,"")
        }

        formatLayout?.setOnClickListener {
            SupportedFormatsActivity.start(requireContext())
        }
    }

    private fun confirmDelete() {
        AlertDialog.Builder(requireContext(), R.style.DialogAlertTheme)
            .setTitle(requireContext().resources.getString(R.string.delete_all_history))
            .setCancelable(false)
            .setPositiveButton(
                android.R.string.yes
            ) { _, _ ->
                viewModel?.deleteAll()
            }
            .setNegativeButton(
                android.R.string.no
            ) { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .show()
    }
}