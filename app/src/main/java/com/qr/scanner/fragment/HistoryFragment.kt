package com.qr.scanner.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.qr.scanner.R
import com.qr.scanner.adapter.HistoryAdapter
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.qr.scanner.history.HistoryItem
import com.qr.scanner.history.HistoryManager
import kotlinx.android.synthetic.main.fragment_history.view.*


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class HistoryFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {
    private var v: View? = null
    private var historyList: List<HistoryItem>? = null
    private var adapter: HistoryAdapter? = null
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        v = inflater.inflate(R.layout.fragment_history, container, false)

        val toolbar: Toolbar? = v?.findViewById(R.id.toolbar)
        if (toolbar != null) {
            toolbar?.title = "History"
        }
        v?.swipeRefreshLayout?.setOnRefreshListener(this)
        v?.recyclerView?.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        getData()
        return v
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HistoryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onRefresh() {
        getData()
        v?.swipeRefreshLayout?.isRefreshing = false
    }

    private fun getData() {
        val history = HistoryManager(requireActivity())
        historyList = history?.getAll(requireActivity())
        v?.recyclerView?.adapter = HistoryAdapter(historyList!!, requireActivity())

    }

}