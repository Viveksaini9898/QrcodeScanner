package com.qr.scanner.fragment

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.hardware.Camera
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.MenuItemCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.core.BarcodeFormat
import com.core.Result
import com.core.client.result.ParsedResultType
import com.core.client.result.ProductParsedResult
import com.qr.scanner.BeepManager
import com.qr.scanner.R
import com.qr.scanner.ScannerView
import com.qr.scanner.activity.ImageScannerActivity
import com.qr.scanner.activity.MainActivity
import com.qr.scanner.activity.ProductResultActivity
import com.qr.scanner.activity.ScanResultActivity
import com.qr.scanner.dialog.CameraSelectorDialogFragment
import com.qr.scanner.dialog.FormatSelectorDialogFragment
import com.qr.scanner.history.HistoryItem
import com.qr.scanner.history.HistoryManager
import com.qr.scanner.history.SQLite.ORM.HistoryORM
import com.qr.scanner.history.entity.History
import com.qr.scanner.preference.UserPreferences
import com.qr.scanner.result.ResultHandlerFactory
import com.qr.scanner.utils.RESULT
import com.qr.scanner.utils.toast
import kotlinx.android.synthetic.main.fragment_scanner.view.*
import java.text.DateFormat
import java.util.ArrayList

class ScannerFragment : Fragment(),
    ScannerView.ResultHandler, FormatSelectorDialogFragment.FormatSelectorDialogListener,
    CameraSelectorDialogFragment.CameraSelectorDialogListener {

    private var userPreferences: UserPreferences? = null
    private var beepManager: BeepManager? = null
    private var mScannerView: ScannerView? = null
    private var mFlash = false
    private var mAutoFocus = false
    private var mSelectedIndices: ArrayList<Int>? = null
    private var mCameraId = -1


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        state: Bundle?
    ): View? {
        val view: View? = inflater.inflate(R.layout.fragment_scanner, container, false)

        userPreferences = UserPreferences(requireContext())

        mScannerView = view?.findViewById(R.id.scannerView)
        mScannerView?.addView(ScannerView(activity))
        beepManager = BeepManager(activity)

        mAutoFocus = userPreferences?.autoFocus!!

        if (state != null) {
            mFlash = state.getBoolean(FLASH_STATE, false)
            mAutoFocus =
                state.getBoolean(AUTO_FOCUS_STATE, true)
            mSelectedIndices =
                state.getIntegerArrayList(SELECTED_FORMATS)
            mCameraId = state.getInt(CAMERA_ID, -1)
        } else {
            mFlash = false
            mAutoFocus = true
            mSelectedIndices = null
            mCameraId = -1
        }
        setupFormats()

        view?.imageLayout?.setOnClickListener {
            startActivity(Intent(requireActivity(), ImageScannerActivity::class.java))
        }

        view?.flashLayout?.setOnClickListener {
            mFlash = !mFlash
            if (mFlash) {
                view?.flash?.setImageResource(R.drawable.ic_flash_off_black_24dp)
            } else {
                view?.flash?.setImageResource(R.drawable.ic_flash_on_black_24dp)
            }
            mScannerView?.flash = mFlash
        }

        view?.cameraLayout?.setOnClickListener {
            val cFragment: DialogFragment =
                CameraSelectorDialogFragment.newInstance(this, mCameraId)
            cFragment.show(requireActivity().supportFragmentManager, "camera_selector")
        }
        return view
    }

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)

        // setHasOptionsMenu(true)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater!!)
        var menuItem: MenuItem? = if (mFlash) {
            menu.add(Menu.NONE, R.id.menu_flash, 0, R.string.flash_on)
        } else {
            menu.add(Menu.NONE, R.id.menu_flash, 0, R.string.flash_off)
        }
        MenuItemCompat.setShowAsAction(menuItem, MenuItem.SHOW_AS_ACTION_NEVER)
        menuItem = if (mAutoFocus) {
            menu.add(Menu.NONE, R.id.menu_auto_focus, 0, R.string.auto_focus_on)
        } else {
            menu.add(Menu.NONE, R.id.menu_auto_focus, 0, R.string.auto_focus_off)
        }
        MenuItemCompat.setShowAsAction(menuItem, MenuItem.SHOW_AS_ACTION_NEVER)
        menuItem = menu.add(Menu.NONE, R.id.menu_formats, 0, R.string.formats)
        MenuItemCompat.setShowAsAction(menuItem, MenuItem.SHOW_AS_ACTION_NEVER)
        menuItem = menu.add(Menu.NONE, R.id.menu_image, 0, R.string.image)
        MenuItemCompat.setShowAsAction(menuItem, MenuItem.SHOW_AS_ACTION_NEVER)
        menuItem = menu.add(Menu.NONE, R.id.menu_camera_selector, 0, R.string.select_camera)
        MenuItemCompat.setShowAsAction(menuItem, MenuItem.SHOW_AS_ACTION_NEVER)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar items
        return when (item.itemId) {
            R.id.menu_flash -> {
                mFlash = !mFlash
                if (mFlash) {
                    item.setTitle(R.string.flash_on)
                } else {
                    item.setTitle(R.string.flash_off)
                }
                mScannerView?.flash = mFlash
                true
            }
            R.id.menu_auto_focus -> {
                mAutoFocus = !mAutoFocus
                if (mAutoFocus) {
                    item.setTitle(R.string.auto_focus_on)
                } else {
                    item.setTitle(R.string.auto_focus_off)
                }
                mScannerView?.setAutoFocus(mAutoFocus)
                true
            }
            R.id.menu_formats -> {
                val fragment: DialogFragment =
                    FormatSelectorDialogFragment.newInstance(this, mSelectedIndices)
                fragment.show(requireActivity().supportFragmentManager, "format_selector")
                true
            }
            R.id.menu_camera_selector -> {
                val cFragment: DialogFragment =
                    CameraSelectorDialogFragment.newInstance(this, mCameraId)
                cFragment.show(requireActivity().supportFragmentManager, "camera_selector")
                true
            }
            R.id.menu_image -> {
                startActivity(Intent(requireActivity(), ImageScannerActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        beepManager?.updatePrefs()
        mScannerView?.setResultHandler(this)
        mScannerView?.startCamera(mCameraId)
        mScannerView?.flash = mFlash
        mScannerView?.setAutoFocus(mAutoFocus)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(FLASH_STATE, mFlash)
        outState.putBoolean(AUTO_FOCUS_STATE, mAutoFocus)
        outState.putIntegerArrayList(SELECTED_FORMATS, mSelectedIndices)
        outState.putInt(CAMERA_ID, mCameraId)
    }

    override fun handleResult(rawResult: Result) {
        beepManager?.playBeepSoundAndVibrate()


        HistoryManager(requireActivity()).add(requireActivity(),rawResult)
        val resultHandler = ResultHandlerFactory.makeResultHandler(activity, rawResult)
        if (resultHandler.type == ParsedResultType.PRODUCT){
            val productResult = resultHandler?.result as ProductParsedResult
            val intent = Intent(context, ProductResultActivity::class.java)
            intent.putExtra("product", productResult.productID)
            intent.putExtra(RESULT,rawResult)
            startActivity(intent)
        }else {
            val intent = Intent(context, ScanResultActivity::class.java)
            intent.putExtra(RESULT, rawResult)
            startActivity(intent)
        }

    }


    private fun closeMessageDialog() {
        closeDialog("scan_results")
    }

    private fun closeFormatsDialog() {
        closeDialog("format_selector")
    }

    private fun closeDialog(dialogName: String?) {
        val fragmentManager = requireActivity().supportFragmentManager
        val fragment = fragmentManager.findFragmentByTag(dialogName) as DialogFragment?
        fragment?.dismiss()
    }

    fun onDialogPositiveClick(dialog: DialogFragment?) {
        // Resume the camera
        mScannerView?.resumeCameraPreview(this)
    }

    override fun onFormatsSaved(selectedIndices: ArrayList<Int>?) {
        mSelectedIndices = selectedIndices
        setupFormats()
    }

    override fun onCameraSelected(cameraId: Int) {
        mCameraId = cameraId
        mScannerView?.startCamera(mCameraId)
        mScannerView?.flash = mFlash
        mScannerView?.setAutoFocus(mAutoFocus)
    }

    private fun setupFormats() {
        val formats: MutableList<BarcodeFormat> = ArrayList()
        if (mSelectedIndices == null || mSelectedIndices?.isEmpty()!!) {
            mSelectedIndices = ArrayList()
            for (i in 0 until ScannerView.ALL_FORMATS.size) {
                mSelectedIndices?.add(i)
            }
        }
        for (index in mSelectedIndices!!) {
            formats.add(ScannerView.ALL_FORMATS[index])
        }
        if (mScannerView != null) {
            mScannerView?.setFormats(formats)
        }
    }

    override fun onPause() {
        super.onPause()
        mScannerView?.stopCamera()
        closeMessageDialog()
        closeFormatsDialog()
    }

    override fun onDestroy() {
        super.onDestroy()
        beepManager?.close()

    }

    private fun showDialog(result: Result?) {
        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.custom_dialog)
        val v = dialog.window?.decorView
        v?.setBackgroundResource(android.R.color.transparent)
        val text = dialog.findViewById<View>(R.id.someText) as TextView
        text.text = result?.text
        val img = dialog.findViewById<View>(R.id.imgOfDialog) as ImageView
        img.setImageResource(R.drawable.ic_done_gr)
        val webSearch = dialog.findViewById<TextView>(R.id.searchButton)
        val copy = dialog.findViewById<TextView>(R.id.copyButton)
        val share = dialog.findViewById<TextView>(R.id.shareButton)
        webSearch.setOnClickListener { /* if (Patterns.WEB_URL.matcher(result.getText()).matches()) {
                    url = result.getText();
                } else {
                    url = "http://www.google.com/#q=" + result.getText();
                }
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                dialog.dismiss();*/
            mScannerView?.resumeCameraPreview(this)
        }
        copy.setOnClickListener {
            val clipboard = context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("", result?.text)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
            mScannerView?.resumeCameraPreview(this)
        }
        share.setOnClickListener {
            val sharingIntent = Intent(Intent.ACTION_SEND)
            sharingIntent.type = "text/plain"
            val shareBody = result?.text
            sharingIntent?.putExtra(Intent.EXTRA_TEXT, shareBody)
            startActivity(Intent.createChooser(sharingIntent, "Share "))
            dialog.dismiss()
            mScannerView?.resumeCameraPreview(this)
        }
        dialog.setOnCancelListener { mScannerView?.resumeCameraPreview(this) }
        dialog.show()
    }

    private fun getCameraId(): Int {
        var cameraId = -1
        val ci = Camera.CameraInfo()
        for (i in 0 until Camera.getNumberOfCameras()) {
            Camera.getCameraInfo(i, ci)
            if (ci.facing === Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i
                break
            } else if (ci.facing === Camera.CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i
                break
            }
        }
        return cameraId
    }

    companion object {
        private const val FLASH_STATE = "FLASH_STATE"
        private const val AUTO_FOCUS_STATE = "AUTO_FOCUS_STATE"
        private const val SELECTED_FORMATS = "SELECTED_FORMATS"
        private const val CAMERA_ID = "CAMERA_ID"
    }
}