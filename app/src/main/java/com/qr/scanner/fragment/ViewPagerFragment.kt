package com.qr.scanner.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.qr.scanner.R
import com.qr.scanner.adapter.ViewPagerAdapter
import kotlinx.android.synthetic.main.fragment_view_pager.*
import kotlinx.android.synthetic.main.toolbar.*

class ViewPagerFragment : Fragment() {

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
        }
        val viewPagerAdapter = ViewPagerAdapter(childFragmentManager)
        viewPagerAdapter.add(HistoryFragment(),"All History")
        viewPagerAdapter.add(FavoritesFragment(),"Favorite")
        viewPagerAdapter.add(GenerateHistoryFragment(),"Generate")

        viewpager?.adapter = viewPagerAdapter
        tab_layout?.setupWithViewPager(viewpager)
    }
}