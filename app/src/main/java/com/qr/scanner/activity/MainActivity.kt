package com.qr.scanner.activity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.qr.scanner.R
import com.qr.scanner.fragment.*
import com.qr.scanner.utils.createDir
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.*
import com.qr.scanner.utils.loadFragment


class MainActivity : BaseActivity() {
    private val MY_CAMERA_REQUEST_CODE: Int = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermission()
        } else {
            loadScannerFragment()
        }
        createDir()

        bottomBar?.onItemSelected = {
            when (it) {
                0 -> {
                    loadScannerFragment()
                }
                1 -> {
                    QrGenerateFragment().loadFragment(this, R.id.container)
                }

                2 -> {
                    ViewPagerFragment().loadFragment(this, R.id.container)

                }
                3 -> {
                    SettingsFragment().loadFragment(this, R.id.container)
                }

            }
        }

        bottomBar?.onItemReselected = {
            when (it) {
                0 -> {
                }
                1 -> {
                }
                2 -> {
                }

            }
        }
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this@MainActivity,
            arrayOf(Manifest.permission.CAMERA),
            MY_CAMERA_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions!!, grantResults)
        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadScannerFragment()
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun loadScannerFragment() {
        ScanBarcodeFromCameraFragment().loadFragment(this, R.id.container)
    }
}