package com.qr.scanner.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.qr.scanner.R
import com.qr.scanner.constant.PARSE_RESULT
import com.qr.scanner.extension.unsafeLazy
import com.qr.scanner.model.ParsedResultType
import com.qr.scanner.resultfragment.*
import com.qr.scanner.utils.*
import com.qr.scanner.viewmodel.HistoryViewModel
import kotlinx.android.synthetic.main.toolbar.*
import com.qr.scanner.model.Result
import com.qr.scanner.result.ParsedResultHandler

class ScanResultActivity : AppCompatActivity() {


    companion object {
        fun start(context: Context?, result: Result?) {
            val intent = Intent(context, ScanResultActivity::class.java).apply {
                putExtra(PARSE_RESULT, result)
            }
            context?.startActivity(intent)
        }
    }

    private val viewModel by lazy {
        ViewModelProvider(this).get(HistoryViewModel::class.java)
    }

    private val result by unsafeLazy {
        intent?.getSerializableExtra(PARSE_RESULT) as? Result ?: throw IllegalArgumentException("No result passed")
    }

    private val barcode by unsafeLazy {
        ParsedResultHandler(result!!)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_result)

        if (toolbar != null) {
            toolbar?.title = "Scan Result"
            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }


        when (result.parse) {
            ParsedResultType.WIFI -> {
                WifiResultFragment.newInstance(result!!).loadFragment(this, R.id.container)
                toolbar?.title = "Wifi"
            }
            ParsedResultType.OTHER -> {
                if (barcode.isProductBarcode) {
                    ProductResultFragment.newInstance(result).loadFragment(this, R.id.container)
                    toolbar?.title = "Product"
                }else {
                    TextResultFragment.newInstance(result).loadFragment(this, R.id.container)
                    toolbar?.title = "Text"
                }
            }
            ParsedResultType.SMS -> {
                SmsResultFragment.newInstance(result).loadFragment(this, R.id.container)
                toolbar?.title = "SMS"

            }
            ParsedResultType.PHONE -> {
                PhoneResultFragment.newInstance(result).loadFragment(this, R.id.container)
                toolbar?.title = "Phone"

            }
            ParsedResultType.URL -> {
                UrlResultFragment.newInstance(result).loadFragment(this, R.id.container)
                toolbar?.title = "Website"

            }
            ParsedResultType.EMAIL -> {
                EmailResultFragment.newInstance(result).loadFragment(this, R.id.container)
                toolbar?.title = "Email"
            }
            ParsedResultType.VCARD -> {
                ContactResultFragment.newInstance(result).loadFragment(this, R.id.container)
                toolbar?.title = "Contact"
            }
            ParsedResultType.VEVENT -> {
                CalendarResultFragment.newInstance(result).loadFragment(this, R.id.container)
                toolbar?.title = "Event"
            }
        }

    }


    private fun toggleIsFavorite() {
        result?.isFavorite = result?.isFavorite.not()
        viewModel?.update(result)
        updateHistory(result.isFavorite)
    }

    private fun updateHistory(favorite: Boolean) {
        showIsFavorite(favorite)
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
        updateHistory(result.isFavorite)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.item_add_to_favorites ->{
                toggleIsFavorite()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}