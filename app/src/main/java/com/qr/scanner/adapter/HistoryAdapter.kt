package com.qr.scanner.adapter

import android.app.Activity
import android.content.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.core.Result
import com.core.client.result.*
import com.qr.scanner.R
import com.qr.scanner.activity.ProductResultActivity
import com.qr.scanner.activity.ScanResultActivity
import com.qr.scanner.constant.RESULT
import com.qr.scanner.history.History
import com.qr.scanner.result.ResultHandlerFactory
import com.qr.scanner.utils.*
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class HistoryAdapter(
    private var activity: Activity?
) :
    ListAdapter<History, HistoryAdapter.ViewHolder>(ListAdapterCallBack) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v: View? =
            LayoutInflater.from(parent.context).inflate(R.layout.history_list_item, parent, false)
        return ViewHolder(v!!)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val historyList = getItem(position)
        val result = Result(historyList?.text,null,null,historyList?.format, historyList?.timestamp!!,historyList.isFavorite)
        val resultHandler =
            ResultHandlerFactory.makeResultHandler(activity, result)
        when (resultHandler.type) {
            ParsedResultType.WIFI -> {
                val wifiResult = resultHandler?.result as WifiParsedResult?
                holder.name?.text = wifiResult?.ssid
                holder.date?.text = getDate(historyList?.timestamp!!)
                holder.image?.setImageResource(R.drawable.ic_wifi_black_24dp)
            }
            ParsedResultType.TEXT -> {
                val textResult = resultHandler?.result as TextParsedResult
                holder.name?.text = textResult?.text
                holder.date?.text = getDate(historyList?.timestamp!!)
                holder.image?.setImageResource(R.drawable.ic_text_black_24dp)

            }
            ParsedResultType.SMS -> {
                val smsResult = resultHandler.result as SMSParsedResult?
                holder.name?.text = smsResult?.numbers?.get(0)
                holder.date?.text = getDate(historyList?.timestamp!!)
                holder.image?.setImageResource(R.drawable.ic_sms_black_24dp)

            }
            ParsedResultType.TEL -> {
                val telResult = resultHandler?.result as TelParsedResult?
                holder.name?.text = telResult?.number
                holder.date?.text = getDate(historyList?.timestamp!!)
                holder.image?.setImageResource(R.drawable.ic_call_black_24dp)

            }
            ParsedResultType.URI -> {
                val urlResult = resultHandler?.result as URIParsedResult
                holder.name?.text = urlResult?.uri
                holder.date?.text = getDate(historyList?.timestamp!!)
                when {
                    isYoutubeUrl(urlResult?.uri) -> {
                        holder.image?.setImageResource(R.drawable.ic_youtube)
                    }
                    isTwitterUrl(urlResult?.uri) -> {
                        holder.image?.setImageResource(R.drawable.ic_twitter)
                    }
                    isFacebookUrl(urlResult?.uri) -> {
                        holder.image?.setImageResource(R.drawable.ic_facebook)
                    }
                    isInstagramUrl(urlResult?.uri) -> {
                        holder.image?.setImageResource(R.drawable.ic_instagram)
                    }
                    else -> {
                        holder.image?.setImageResource(R.drawable.ic_link_black_24dp)
                    }
                }
            }
            ParsedResultType.EMAIL_ADDRESS -> {
                val emailResult = resultHandler.result as EmailAddressParsedResult?
                holder.name?.text = emailResult?.emailAddress
                holder.date?.text = getDate(historyList?.timestamp!!)

                holder.image?.setImageResource(R.drawable.ic_email_black_24dp)

            }
            ParsedResultType.ADDRESSBOOK -> {
                val contactResult = resultHandler.result as AddressBookParsedResult?
                holder.name?.text = contactResult?.names?.get(0)
                holder.date?.text = getDate(historyList?.timestamp!!)

                holder.image?.setImageResource(R.drawable.ic_contact_page_black_24dp)

            }
            ParsedResultType.CALENDAR -> {
                val calenderResult = resultHandler?.result as CalendarParsedResult
                holder.name?.text = calenderResult?.summary
                holder.date?.text = getDate(historyList?.timestamp!!)

                holder.image?.setImageResource(R.drawable.ic_event_black_24dp)

            }
            ParsedResultType.PRODUCT -> {
                val productResult = resultHandler?.result as ProductParsedResult
                holder.name?.text = productResult?.productID
                holder.date?.text = getDate(historyList?.timestamp!!)

                holder.image?.setImageResource(R.drawable.ic_barcode)

            }

        }

        holder?.delete?.setOnClickListener {
            confirmDelete(activity,position)
        }


        holder?.card?.setOnClickListener {
            if (resultHandler.type == ParsedResultType.PRODUCT) {
                val productResult = resultHandler?.result as ProductParsedResult
                val intent = Intent(activity, ProductResultActivity::class.java)
                intent.putExtra("product", productResult.productID)
                intent.putExtra(RESULT, result)
                activity?.startActivity(intent)
            } else {
                val intent = Intent(activity, ScanResultActivity::class.java)
                intent.putExtra(RESULT, result)
                activity?.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return currentList?.size!!
    }

    private fun updateList(list: MutableList<History>) {
        list?.let {
            val newList = ArrayList<History>(list)
            submitList(newList)
        }
    }

    fun removeItem(position: Int) {
        if (position >= itemCount) return
        val list = currentList.toMutableList()
        if (list.removeAt(position) != null) updateList(list)
    }

    override fun submitList(list: List<History>?) {
        super.submitList(list?.let { ArrayList(it) })
    }

    private fun correctDate(timestamp: Long, isToday: Boolean): String? {
        var sDate = ""
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val c: Calendar = Calendar.getInstance()
        var netDate: Date? = null
        try {
            netDate = Date(timestamp)
            sdf.format(netDate)
            sDate = sdf.format(netDate)
            val currentDateTimeString: String = sdf.format(c.time)
            c.add(Calendar.DATE, -1)
            val yesterdayDateTimeString: String = sdf.format(c.time)
            if (currentDateTimeString == sDate && isToday) {
                sDate = "Today"
            } else if (yesterdayDateTimeString == sDate && isToday) {
                sDate = "Yesterday"
            }
        } catch (e: Exception) {
            System.err.println("There's an error in the Date!")
        }
        return sDate
    }

    private fun getDate(time_stamp_server: Long): String? {
        val formatter = SimpleDateFormat("dd-mm-yyyy HH:mm")
        return formatter.format(time_stamp_server)
    }

    private fun confirmDelete(context: Context?,position: Int) {
        AlertDialog.Builder(activity!!, R.style.DialogAlertTheme)
            .setTitle(activity!!.resources.getQuantityString(R.plurals.delete_alert_title, 1))
            .setMessage(activity!!.resources.getQuantityString(R.plurals.delete_alert_message, 1))
            .setCancelable(false)
            .setPositiveButton(
                android.R.string.yes
            ) { _, _ ->
              //  removeItem(position)
            }
            .setNegativeButton(
                android.R.string.no
            ) { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .show()
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var image = itemView.findViewById<View>(R.id.image) as ImageView
        var delete = itemView.findViewById<View>(R.id.delete) as ImageView
        var date = itemView.findViewById<View>(R.id.tvDate) as TextView
        var name = itemView.findViewById<View>(R.id.tvName) as TextView
        var card = itemView.findViewById<View>(R.id.cardLayout) as CardView
    }
}
