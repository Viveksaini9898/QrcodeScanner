package com.qr.scanner.utils

import android.app.Activity
import android.content.*
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.ContactsContract
import android.provider.MediaStore
import android.provider.Telephony
import android.util.Patterns
import android.webkit.URLUtil
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.qr.scanner.activity.ViewQRcodeActivity
import androidx.core.content.FileProvider
import com.qr.scanner.constant.RESULT
import com.qr.scanner.extension.Toast.toast
import com.qr.scanner.extension.toEmailType
import com.qr.scanner.extension.toPhoneType
import com.qr.scanner.result.ParsedResultHandler
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception



fun storePath(): String? {
    return Environment.getExternalStorageDirectory().toString() + File.separator + "Pictures" + File.separator + "QrScanner"
}

fun createDir() {
    val appDir = File(storePath())
    if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED && !appDir.isDirectory) {
        appDir.mkdirs()
    }

}

fun Fragment.loadFragment(activity: AppCompatActivity, layoutId: Int) {
    val fm: FragmentManager = activity?.supportFragmentManager
    val ft = fm.beginTransaction()
    ft.replace(layoutId, this)
    ft.commitAllowingStateLoss()
}

fun copyContent(context: Context?, string: String?) {
    val clipboard =
        context?.getSystemService(AppCompatActivity.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("", string)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
}

fun shareContent(context: Context?, string: String?) {
    val sharingIntent = Intent(Intent.ACTION_SEND)
    sharingIntent.type = "text/plain"
    sharingIntent?.putExtra(Intent.EXTRA_TEXT, string)
    context?.startActivity(Intent.createChooser(sharingIntent, "Share "))
}

fun searchContent(context: Context?, string: String?) {
    val url = if (Patterns.WEB_URL.matcher(string).matches()) {
        string;
    } else {
        "https://www.google.com/search?q=$string"
    }
    context?.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)));
}
fun amazonContent(context: Context?, string: String?) {
    val url = if (Patterns.WEB_URL.matcher(string).matches()) {
        string;
    } else {
        "https://www.amazon.in/s?k=$string"
    }
    context?.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)));
}
fun flipkartContent(context: Context?, string: String?) {
    val url = if (Patterns.WEB_URL.matcher(string).matches()) {
        string;
    } else {
        "https://www.flipkart.com/search?q=$string"
    }
    context?.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)));
}
fun ebayContent(context: Context?, string: String?) {
    val url = if (Patterns.WEB_URL.matcher(string).matches()) {
        string;
    } else {
        "https://www.ebay.com/sch/i.html?_nkw=$string"
    }
    context?.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)));
}

fun dialPhone(phoneNumber: String?, context: Context?) {
    launchIntent(context, Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber")))
}

fun launchIntent(context: Context?, intent: Intent?) {
    try {
        rawLaunchIntent(context, intent)
    } catch (ignored: ActivityNotFoundException) {
    }
}

fun rawLaunchIntent(context: Context?, intent: Intent?) {
    if (intent != null) {
        context?.startActivity(intent)
    }
}



fun viewQrCodeActivity(context: Context?, result: com.qr.scanner.model.Result) {
    val intent = Intent(context, ViewQRcodeActivity::class.java)
    intent.putExtra(RESULT, result)
    context?.startActivity(intent)
}

fun isTwitterUrl(url: String?): Boolean {
    if (url == null) {
        return false
    }
    if (URLUtil.isValidUrl(url)) {
        val uri = Uri.parse(url)
        if ("www.twitter.com" == uri.host) {
            return true
        } else if ("twitter.com" == uri.host) {
            return true
        }
    }
    return false
}

fun isFacebookUrl(url: String?): Boolean {
    if (url == null) {
        return false
    }
    if (URLUtil.isValidUrl(url)) {
        val uri = Uri.parse(url)
        if ("www.facebook.com" == uri.host) {
            return true
        } else if ("facebook.com" == uri.host) {
            return true
        }
    }
    return false
}

fun isInstagramUrl(url: String?): Boolean {
    if (url == null) {
        return false
    }
    if (URLUtil.isValidUrl(url)) {
        val uri = Uri.parse(url)
        if ("www.instagram.com" == uri.host) {
            return true
        } else if ("instagram.com" == uri.host) {
            return true
        }
    }
    return false
}

fun isYoutubeUrl(url: String?): Boolean {
    if (url == null) {
        return false
    }
    if (URLUtil.isValidUrl(url)) {
        val uri = Uri.parse(url)
        if ("www.youtube.com" == uri.host) {
            return true
        } else if ("youtube.com" == uri.host) {
            return true
        }
    }
    return false
}

fun sendSMS(phoneNumber: String, context: Context?) {
    sendSMSFromUri("smsto:$phoneNumber", context)
}

private fun sendSMSFromUri(uri: String, context: Context?) {
    val intent = Intent(Intent.ACTION_SENDTO, Uri.parse(uri))
    intent.putExtra("compose_mode", true)
    launchIntent(context, intent)
}


fun saveImageToGallery(context: Context?,bmp: Bitmap?) {
    val appDir = File(storePath())
    if (!appDir.exists()) {
        appDir.mkdir()
    }
    val fileName = System.currentTimeMillis().toString() + "qr_scanner" + ".png"
    val file = File(appDir, fileName)
    try {
        val fos = FileOutputStream(file)
        val isSuccess = bmp?.compress(Bitmap.CompressFormat.PNG, 100, fos)
        fos.flush()
        fos.close()

        val uri: Uri = Uri.fromFile(file)
        context?.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri))
        toast(context,"Save Successfully")
    } catch (e: IOException) {
        e.printStackTrace()
    }
}

fun getPath(context: Context?,uri: Uri?): String? {
    return try {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor? = context?.contentResolver?.query(uri!!, projection, null, null, null)
        cursor?.moveToFirst()
        val columnIndex: Int? = cursor?.getColumnIndex(projection[0])
        val filePath: String? = cursor?.getString(columnIndex!!)
        cursor?.close()
        filePath
    }catch (e: Exception){
        null
    }
}

fun shareBitmap(activity: Activity?,bitmap: Bitmap?) {
    val cachePath = File(activity?.externalCacheDir, "share/")
    cachePath.mkdirs()
    val fileName = System.currentTimeMillis().toString() + "qr_scanner" + ".png"
    val file = File(cachePath, fileName)
    val fileOutputStream: FileOutputStream
    try {
        fileOutputStream = FileOutputStream(file)
        bitmap?.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
        fileOutputStream.flush()
        fileOutputStream.close()
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
    } catch (e: IOException) {
        e.printStackTrace()
    }
    val myImageFileUri: Uri =
        FileProvider.getUriForFile(activity!!, activity?.packageName + ".provider", file)

    val intent = Intent(Intent.ACTION_SEND)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    intent.putExtra(Intent.EXTRA_STREAM, myImageFileUri)
    intent.type = "image/*"
    activity?.startActivity(Intent.createChooser(intent, "Share with"))
}

fun startActivityIfExists(context: Context?,intent: Intent) {
    intent.apply {
        flags = flags or Intent.FLAG_ACTIVITY_NEW_TASK
    }

    if (intent.resolveActivity(context?.packageManager!!) != null) {
        context?.startActivity(intent)
    }
}

fun addToContacts(context: Context?,barcode: ParsedResultHandler) {
    val intent = Intent(ContactsContract.Intents.Insert.ACTION).apply {
        type = ContactsContract.Contacts.CONTENT_TYPE

        val fullName = "${barcode.firstName.orEmpty()} ${barcode.lastName.orEmpty()}"
        putExtra(ContactsContract.Intents.Insert.NAME, fullName)
        putExtra(ContactsContract.Intents.Insert.COMPANY, barcode.organization.orEmpty())
        putExtra(ContactsContract.Intents.Insert.JOB_TITLE, barcode.jobTitle.orEmpty())

        putExtra(ContactsContract.Intents.Insert.PHONE, barcode.phone.orEmpty())
        putExtra(ContactsContract.Intents.Insert.PHONE_TYPE, barcode.phoneType.orEmpty().toPhoneType())

        putExtra(ContactsContract.Intents.Insert.SECONDARY_PHONE, barcode.secondaryPhone.orEmpty())
        putExtra(ContactsContract.Intents.Insert.SECONDARY_PHONE_TYPE, barcode.secondaryPhoneType.orEmpty().toPhoneType())

        putExtra(ContactsContract.Intents.Insert.TERTIARY_PHONE, barcode.tertiaryPhone.orEmpty())
        putExtra(ContactsContract.Intents.Insert.TERTIARY_PHONE_TYPE, barcode.tertiaryPhoneType.orEmpty().toPhoneType())

        putExtra(ContactsContract.Intents.Insert.EMAIL, barcode.email.orEmpty())
        putExtra(ContactsContract.Intents.Insert.EMAIL_TYPE, barcode.emailType.orEmpty().toEmailType())

        putExtra(ContactsContract.Intents.Insert.SECONDARY_EMAIL, barcode.secondaryEmail.orEmpty())
        putExtra(ContactsContract.Intents.Insert.SECONDARY_EMAIL_TYPE, barcode.secondaryEmailType.orEmpty().toEmailType())

        putExtra(ContactsContract.Intents.Insert.TERTIARY_EMAIL, barcode.tertiaryEmail.orEmpty())
        putExtra(ContactsContract.Intents.Insert.TERTIARY_EMAIL_TYPE, barcode.tertiaryEmailType.orEmpty().toEmailType())

        putExtra(ContactsContract.Intents.Insert.NOTES, barcode.note.orEmpty())
    }
    startActivityIfExists(context,intent)
}

fun callPhone(context: Context?,phone: String?) {
    val phoneUri = "tel:${phone.orEmpty()}"
    startActivityIfExists(context,Intent.ACTION_DIAL, phoneUri)
}

fun startActivityIfExists(context: Context?,action: String, uri: String) {
    val intent = Intent(action, Uri.parse(uri))
    startActivityIfExists(context,intent)
}

fun sendEmail(context: Context?,email: String?, subject: String?, body: String?) {
    val uri = Uri.parse("mailto:${email.orEmpty()}")
    val intent = Intent(Intent.ACTION_SEND, uri).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_EMAIL, arrayOf(email.orEmpty()))
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, body)
    }
    startActivityIfExists(context, intent)
}

fun searchMap(context: Context?,address: String?) {
    val intent =Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=" + Uri.encode(address)))
    startActivityIfExists(context,intent)
}
