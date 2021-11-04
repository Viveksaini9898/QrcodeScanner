package com.qr.scanner.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContentProviderCompat.requireContext
import com.core.Result
import com.qr.scanner.R
import com.qr.scanner.constant.RESULT
import com.qr.scanner.preference.UserPreferences
import com.qr.scanner.utils.*
import kotlinx.android.synthetic.main.activity_product_result.*
import kotlinx.android.synthetic.main.toolbar.*

class ProductResultActivity : AppCompatActivity() {

    private var userPreferences: UserPreferences? = null
    private var result: Result? = null
    private var product: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_result)

        if (toolbar != null) {
            toolbar?.title = "Product"
            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        userPreferences = UserPreferences(applicationContext)

        if (intent.extras != null) {
            product = intent.getStringExtra("product")
            result = intent.getParcelableExtra(RESULT) as Result?
        }

        if (userPreferences?.autoCopy!!){
            copyContent(applicationContext,product)
        }

        if (product != null && product.isNullOrEmpty()) {
            text?.text = product
        } else {
            text?.text = "Not Found"
        }

        copyLayout?.setOnClickListener {
            copyContent(applicationContext, product)
        }

        viewQrCode?.setOnClickListener {
            val intent = Intent(applicationContext, ViewBarcodeActivity::class.java)
            intent.putExtra("product", product)
            startActivity(intent)
        }
        shareLayout?.setOnClickListener {
            shareContent(applicationContext, product)
        }
        googleLayout?.setOnClickListener {
            searchContent(applicationContext, product)
        }

        flipkartLayout?.setOnClickListener {
            flipkartContent(applicationContext, product)
        }
        ebayLayout?.setOnClickListener {
            ebayContent(applicationContext, product)
        }
        amazonLayout?.setOnClickListener {
            amazonContent(applicationContext, product)
        }

    }
}