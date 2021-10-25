package com.qr.scanner.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.core.Result
import com.core.client.result.ParsedResultType
import com.qr.scanner.R
import com.qr.scanner.result.ResultHandlerFactory
import com.qr.scanner.resultfragment.*
import com.qr.scanner.utils.*
import kotlinx.android.synthetic.main.toolbar.*

class ScanResultActivity : AppCompatActivity() {


    private var result: Result? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_result)

        if (toolbar != null) {
            toolbar?.title = "Scan Result"
            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        if (intent.extras != null) {
            result = intent.getParcelableExtra(RESULT) as Result?
        }

        val resultHandler = ResultHandlerFactory.makeResultHandler(this, result)
        toast(applicationContext, resultHandler.type.toString())


        when (resultHandler.type) {
            ParsedResultType.WIFI -> {
                WifiResultFragment.newInstance(result!!).loadFragment(this, R.id.container)
                toolbar?.title = "Wifi"
            }
            ParsedResultType.TEXT -> {
                TextResultFragment.newInstance(result).loadFragment(this, R.id.container)
                toolbar?.title = "Text"
            }
            ParsedResultType.SMS -> {
                SmsResultFragment.newInstance(result).loadFragment(this, R.id.container)
                toolbar?.title = "SMS"

            }
            ParsedResultType.TEL -> {
                PhoneResultFragment.newInstance(result).loadFragment(this, R.id.container)
                toolbar?.title = "Phone"

            }
            ParsedResultType.URI -> {
                UrlResultFragment.newInstance(result).loadFragment(this, R.id.container)
                toolbar?.title = "Website"

            }
            ParsedResultType.EMAIL_ADDRESS -> {
                EmailResultFragment.newInstance(result).loadFragment(this, R.id.container)
                toolbar?.title = "Email"
            }
            ParsedResultType.ADDRESSBOOK -> {
                ContactResultFragment.newInstance(result).loadFragment(this, R.id.container)
                toolbar?.title = "Contact"
            }
            ParsedResultType.CALENDAR -> {
                CalendarResultFragment.newInstance(result).loadFragment(this, R.id.container)
                toolbar?.title = "Event"
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