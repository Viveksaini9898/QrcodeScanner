package com.qr.scanner.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import com.qr.scanner.R
import com.qr.scanner.preference.UserPreferences
import kotlinx.android.synthetic.main.fragment_settings.view.*
import kotlinx.android.synthetic.main.toolbar.*


class SettingsFragment : Fragment() {


    private var userPreferences: UserPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View?= inflater.inflate(R.layout.fragment_settings, container, false)

        userPreferences = UserPreferences(requireContext())

        val toolbar: Toolbar? = view?.findViewById(R.id.toolbar)
        if (toolbar != null) {
            toolbar?.title = "Settings"
        }
        view?.sound?.isChecked = userPreferences?.sound!!
        view?.vibrate?.isChecked = userPreferences?.vibrate!!
        view?.autoCopy?.isChecked = userPreferences?.autoCopy!!
        view?.autoFocus?.isChecked = userPreferences?.autoFocus!!

        view?. sound?.setOnCheckedChangeListener { buttonView, isChecked ->
            userPreferences?.sound = isChecked
        }

        view?.vibrate?.setOnCheckedChangeListener { buttonView, isChecked ->
            userPreferences?.vibrate = isChecked

        }
        view?.autoCopy?.setOnCheckedChangeListener { buttonView, isChecked ->
            userPreferences?.autoCopy = isChecked

        }
        view?.autoFocus?.setOnCheckedChangeListener { buttonView, isChecked ->
            userPreferences?.autoFocus = isChecked

        }

        return view
    }

}