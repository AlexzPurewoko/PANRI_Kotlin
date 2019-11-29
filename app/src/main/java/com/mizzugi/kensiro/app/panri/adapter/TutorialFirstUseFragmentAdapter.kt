package com.mizzugi.kensiro.app.panri.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class TutorialFirstUseFragmentAdapter(
    fm: FragmentManager,
    private val allFragment: Array<Fragment>
) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment = allFragment[position]

    override fun getCount(): Int = allFragment.size
}