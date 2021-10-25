package com.qr.scanner.activity

import android.os.Build
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.qr.scanner.R
import kotlinx.android.synthetic.main.toolbar.*

open class BaseActivity: AppCompatActivity(){

    protected open fun setWindowColors() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.statusBarColor = ContextCompat.getColor(applicationContext, R.color.app_default_light)
            window.navigationBarColor =
                ContextCompat.getColor(this, R.color.white)
        } else {
            window.statusBarColor =
                ContextCompat.getColor(applicationContext, R.color.app_default_light)
            window.navigationBarColor =
                ContextCompat.getColor(applicationContext, R.color.white)
        }
    }


}