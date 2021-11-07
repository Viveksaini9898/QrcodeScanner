package com.qr.scanner.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager

import com.google.zxing.BarcodeFormat
import com.qr.scanner.R
import com.qr.scanner.adapter.FormatsAdapter
import com.qr.scanner.extension.unsafeLazy
import com.qr.scanner.objects.SupportedBarcodeFormats
import com.qr.scanner.preference.UserPreferences
import kotlinx.android.synthetic.main.activity_supported_formats.*
import kotlinx.android.synthetic.main.toolbar.*

class SupportedFormatsActivity : BaseActivity(), FormatsAdapter.Listener {

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, SupportedFormatsActivity::class.java)
            context.startActivity(intent)
        }
    }

    val userPreferences by unsafeLazy { UserPreferences(this) }
    private val formats by unsafeLazy { SupportedBarcodeFormats.FORMATS }
    private val formatSelection by unsafeLazy { formats.map(userPreferences::isFormatSelected) }


    private val formatsAdapter by unsafeLazy { FormatsAdapter(this, formats, formatSelection) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_supported_formats)

        if (toolbar != null) {
            toolbar?.title = "Supported formats"
            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        initRecyclerView()
    }

    override fun onFormatChecked(format: BarcodeFormat, isChecked: Boolean) {
        userPreferences.setFormatSelected(format,isChecked)
    }


    private fun initRecyclerView() {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@SupportedFormatsActivity)
            adapter = formatsAdapter
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