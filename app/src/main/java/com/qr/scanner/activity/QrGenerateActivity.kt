package com.qr.scanner.activity

import android.os.Bundle
import android.view.MenuItem
import com.qr.scanner.R
import com.qr.scanner.generatefragment.EmailGenerateFragment
import com.qr.scanner.generatefragment.PhoneGenerateFragment
import com.qr.scanner.generatefragment.TextGenerateFragment
import com.qr.scanner.generatefragment.UrlGenerateFragment
import com.qr.scanner.utils.TYPE
import com.qr.scanner.utils.loadFragment
import kotlinx.android.synthetic.main.toolbar.*

class QrGenerateActivity : BaseActivity() {

    private var type: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrgenerate)

        if (intent.extras != null) {
            type = intent.getStringExtra(TYPE)
        }

        if (toolbar != null) {
            toolbar?.title = "Generate"
            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        when (type) {

            "email" -> {
                toolbar?.title = "Email"
                EmailGenerateFragment().loadFragment(this, R.id.container)
            }
            "text" -> {
                toolbar?.title = "Text"
                TextGenerateFragment().loadFragment(this, R.id.container)
            }
            "website" -> {
                toolbar?.title = "Website"
                UrlGenerateFragment().loadFragment(this, R.id.container)
            }
            "contact" -> {
                TextGenerateFragment().loadFragment(this, R.id.container)
                toolbar?.title = "Contact"
            }
            "event" -> {
                TextGenerateFragment().loadFragment(this, R.id.container)
                toolbar?.title = "Event"
            }
            "wifi" -> {
                TextGenerateFragment().loadFragment(this, R.id.container)
                toolbar?.title = "Wifi"
            }
            "call" -> {
                PhoneGenerateFragment().loadFragment(this, R.id.container)
                toolbar?.title = "Call"
            }
            "sms" -> {
                TextGenerateFragment().loadFragment(this, R.id.container)
                toolbar?.title = "Sms"
            }

        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}