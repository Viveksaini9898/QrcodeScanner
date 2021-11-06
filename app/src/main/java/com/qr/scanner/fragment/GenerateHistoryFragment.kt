package com.qr.scanner.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.qr.scanner.R
import com.qr.scanner.adapter.GenerateHistoryAdapter
import com.qr.scanner.viewmodel.HistoryViewModel
import kotlinx.android.synthetic.main.fragment_generate_history.messageLayout
import kotlinx.android.synthetic.main.fragment_generate_history.recyclerView
import kotlinx.android.synthetic.main.fragment_generate_history.swipeRefreshLayout

class GenerateHistoryFragment : Fragment(),SwipeRefreshLayout.OnRefreshListener {

    private val viewModel by lazy {
        ViewModelProvider(this).get(HistoryViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_generate_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swipeRefreshLayout?.setOnRefreshListener(this)
        recyclerView?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        getData()
    }

    override fun onRefresh() {
        getData()
        swipeRefreshLayout?.isRefreshing = false
    }

    private fun getData() {

        val adapter = GenerateHistoryAdapter(requireActivity(),viewModel)
        viewModel.getGenerate()?.observe(this) {
            if (it?.isEmpty()!!){
                recyclerView?.visibility = View.GONE
                messageLayout?.visibility = View.VISIBLE
            }else {
                messageLayout?.visibility = View.GONE
                recyclerView?.visibility = View.VISIBLE
                adapter.submitList(it)
            }
        }
        recyclerView?.adapter = adapter

    }
}