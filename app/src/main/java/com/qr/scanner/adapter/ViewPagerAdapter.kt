package com.qr.scanner.adapter

import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class ViewPagerAdapter(@NonNull fm: FragmentManager?) : FragmentPagerAdapter(fm!!) {
    private val fragments: MutableList<Fragment> = ArrayList()
    private val fragmentTitle: MutableList<String> = ArrayList()
    fun add(fragment: Fragment, title: String) {
        fragments.add(fragment)
        fragmentTitle.add(title)
    }

    @NonNull
    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }


    override fun getCount(): Int {
        return fragments?.size
    }

    @Nullable
    override fun getPageTitle(position: Int): CharSequence {
        return fragmentTitle[position]
    }
}