package com.qr.scanner.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.core.Result
import com.core.client.result.ParsedResultType
import com.qr.scanner.R
import com.qr.scanner.constant.RESULT
import com.qr.scanner.extension.Toast.toast
import com.qr.scanner.history.History
import com.qr.scanner.result.ResultHandlerFactory
import com.qr.scanner.resultfragment.*
import com.qr.scanner.utils.*
import com.qr.scanner.viewmodel.HistoryViewModel
import kotlinx.android.synthetic.main.toolbar.*

class ScanResultActivity : AppCompatActivity() {

    private val viewModel by lazy {
        ViewModelProvider(this).get(HistoryViewModel::class.java)
    }

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


   /* private fun toggleIsFavorite() {
        if (result?.isFavorite!!) {
            updateHistory(result,false)
        }else {
            updateHistory(result,true)
        }
    }*/

    private fun updateHistory(result: Result?,favorite: Boolean) {
       /* val history = History(0,
            result?.text!!, result?.barcodeFormat!!, result?.timestamp!!,
            isGenerated = false,
            favorite
        )
        viewModel?.update(history)
        */showIsFavorite(favorite)
    }

    private fun showIsFavorite(isFavorite :Boolean) {
        val iconId = if (isFavorite) {
            R.drawable.ic_favorite_red_900_24dp
        } else {
            R.drawable.ic_favorite_border_black_24dp
        }
        toolbar.menu?.findItem(R.id.item_add_to_favorites)?.icon = ContextCompat.getDrawable(this, iconId)

    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.qr_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.item_add_to_favorites ->{
            //    toggleIsFavorite()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}