package com.qr.scanner.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import com.qr.scanner.R
import com.qr.scanner.activity.QrGenerateActivity
import com.qr.scanner.constant.TYPE
import kotlinx.android.synthetic.main.fragment_qr_genrate_frgment.*
import kotlinx.android.synthetic.main.toolbar.*

class QrGenerateFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_qr_genrate_frgment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (toolbar != null) {
            toolbar?.title = "Generate"
        }
        emailLayout?.setOnClickListener {
            loadActivity("email")
        }
        textLayout?.setOnClickListener {
            loadActivity("text")
        }
        websiteLayout?.setOnClickListener {
            loadActivity("website")
        }
        contactLayout?.setOnClickListener {
            loadActivity("contact")
        }
        eventLayout?.setOnClickListener {
            loadActivity("event")
        }
        wifiLayout?.setOnClickListener {
            loadActivity("wifi")
        }
        callLayout?.setOnClickListener {
            loadActivity("call")
        }
        smsLayout?.setOnClickListener {
            loadActivity("sms")
        }

        copyLayout?.setOnClickListener {

        }
    }



    private fun loadActivity(string: String?){
        val intent = Intent(context, QrGenerateActivity::class.java)
        intent.putExtra(TYPE, string)
        startActivity(intent)
    }

}