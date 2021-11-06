package com.qr.scanner.fragment

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.qr.scanner.R
import com.qr.scanner.adapter.ViewPagerAdapter
import com.qr.scanner.model.Result
import com.qr.scanner.viewmodel.HistoryViewModel
import kotlinx.android.synthetic.main.fragment_view_pager.*
import kotlinx.android.synthetic.main.toolbar.*

class ViewPagerFragment : Fragment() {

    private val viewModel by lazy {
        ViewModelProvider(this).get(HistoryViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_pager, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (toolbar != null) {
            toolbar?.title = "History"
            toolbar?.inflateMenu(R.menu.history_menu)
            toolbar?.setOnMenuItemClickListener { onOptionsItemSelected(it) }
        }
        val viewPagerAdapter = ViewPagerAdapter(childFragmentManager)
        viewPagerAdapter.add(HistoryFragment(), "All History")
        viewPagerAdapter.add(GenerateHistoryFragment(), "Generate")

        viewpager?.adapter = viewPagerAdapter
        tab_layout?.setupWithViewPager(viewpager)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_clear_history -> {
                confirmDelete()
            }
        }
        return super.onOptionsItemSelected(item)

    }

    private fun confirmDelete() {
        AlertDialog.Builder(requireContext(), R.style.DialogAlertTheme)
            .setTitle(requireContext().resources.getString(R.string.delete_all_history))
            .setCancelable(false)
            .setPositiveButton(
                android.R.string.yes
            ) { _, _ ->
                viewModel?.deleteAll()
            }
            .setNegativeButton(
                android.R.string.no
            ) { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .show()
    }

}