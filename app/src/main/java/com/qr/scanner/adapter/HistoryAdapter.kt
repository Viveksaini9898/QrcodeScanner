package com.qr.scanner.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.qr.scanner.R
import com.qr.scanner.activity.ScanResultActivity
import com.qr.scanner.extension.unsafeLazy
import com.qr.scanner.utils.*
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import com.qr.scanner.model.Result
import com.qr.scanner.model.ParsedResultType
import com.qr.scanner.result.ParsedResultHandler
import com.qr.scanner.viewmodel.HistoryViewModel

class HistoryAdapter(
    private var activity: Activity?,
    private val viewModel: HistoryViewModel?
) :
    ListAdapter<Result, HistoryAdapter.ViewHolder>(ListAdapterCallBack) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v: View? =
            LayoutInflater.from(parent.context).inflate(R.layout.history_list_item, parent, false)
        return ViewHolder(v!!)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val historyList = getItem(position)

        val barcode by unsafeLazy {
            ParsedResultHandler(historyList)
        }
        when (historyList.parse) {
            ParsedResultType.WIFI -> {
                holder.name?.text = barcode?.networkName
                holder.date?.text = getDate(historyList?.date!!)
                holder.image?.setImageResource(R.drawable.ic_wifi_black_24dp)
            }
            ParsedResultType.OTHER -> {
                if(barcode.isProductBarcode){
                    holder.name?.text = barcode.text
                    holder.date?.text = getDate(historyList?.date!!)
                    holder.image?.setImageResource(R.drawable.ic_barcode)
                }else {
                    holder.name?.text = barcode?.text
                    holder.date?.text = getDate(historyList?.date!!)
                    holder.image?.setImageResource(R.drawable.ic_text_black_24dp)
                }

            }
            ParsedResultType.SMS -> {
                holder.name?.text = barcode.phone
                holder.date?.text = getDate(historyList?.date!!)
                holder.image?.setImageResource(R.drawable.ic_sms_black_24dp)

            }
            ParsedResultType.PHONE -> {
                holder.name?.text = barcode.phone
                holder.date?.text = getDate(historyList?.date!!)
                holder.image?.setImageResource(R.drawable.ic_call_black_24dp)

            }
            ParsedResultType.URL -> {
                holder.name?.text = barcode?.url
                holder.date?.text = getDate(historyList?.date!!)
                when {
                    isYoutubeUrl(barcode?.url) -> {
                        holder.image?.setImageResource(R.drawable.ic_youtube)
                    }
                    isTwitterUrl(barcode?.url) -> {
                        holder.image?.setImageResource(R.drawable.ic_twitter)
                    }
                    isFacebookUrl(barcode?.url) -> {
                        holder.image?.setImageResource(R.drawable.ic_facebook)
                    }
                    isInstagramUrl(barcode?.url) -> {
                        holder.image?.setImageResource(R.drawable.ic_instagram)
                    }
                    else -> {
                        holder.image?.setImageResource(R.drawable.ic_link_black_24dp)
                    }
                }
            }
            ParsedResultType.EMAIL -> {
                holder.name?.text = barcode?.email
                holder.date?.text = getDate(historyList?.date!!)

                holder.image?.setImageResource(R.drawable.ic_email_black_24dp)

            }
            ParsedResultType.VCARD -> {
                holder.name?.text = barcode.firstName
                holder.date?.text = getDate(historyList?.date!!)

                holder.image?.setImageResource(R.drawable.ic_contact_page_black_24dp)

            }
            ParsedResultType.VEVENT -> {
                holder.name?.text = barcode?.eventSummary
                holder.date?.text = getDate(historyList?.date!!)

                holder.image?.setImageResource(R.drawable.ic_event_black_24dp)

            }

        }

        holder?.delete?.setOnClickListener {
            confirmDelete(historyList)
        }


        holder?.card?.setOnClickListener {
            ScanResultActivity.start(activity!!,historyList)
        }
    }

    override fun getItemCount(): Int {
        return currentList?.size!!
    }

    override fun submitList(list: List<Result>?) {
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

    private fun confirmDelete(historyList: Result) {
        AlertDialog.Builder(activity!!, R.style.DialogAlertTheme)
            .setTitle(activity!!.resources.getQuantityString(R.plurals.delete_alert_title, 1))
            .setMessage(activity!!.resources.getQuantityString(R.plurals.delete_alert_message, 1))
            .setCancelable(false)
            .setPositiveButton(
                android.R.string.yes
            ) { _, _ ->
              viewModel?.delete(historyList)
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
