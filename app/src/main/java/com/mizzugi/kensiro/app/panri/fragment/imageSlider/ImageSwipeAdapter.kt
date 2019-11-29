package com.mizzugi.kensiro.app.panri.fragment.imageSlider

import android.graphics.Point
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class ImageSwipeAdapter private constructor(fm: FragmentManager) :
    FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private var arrayResId: IntArray? = null
    private var arrayAssetPath: Array<String>? = null
    private lateinit var requestedSize: Point
    private lateinit var showMode: ViewImageSelectorAdapter.ImageLocation
    private lateinit var mFragments: MutableList<Fragment>

    constructor(fm: FragmentManager, arrayResId: IntArray, requestedSize: Point) : this(fm) {
        this.arrayResId = arrayResId
        this.requestedSize = requestedSize
        showMode = ViewImageSelectorAdapter.ImageLocation.RESOURCES
        mFragments = mutableListOf()
    }

    constructor(
        fm: FragmentManager,
        arrayAssetPath: Array<String>,
        requestedSize: Point
    ) : this(fm) {
        this.arrayAssetPath = arrayAssetPath
        this.requestedSize = requestedSize
        showMode = ViewImageSelectorAdapter.ImageLocation.ASSETS
        mFragments = mutableListOf()
    }

    override fun getItem(position: Int): Fragment {
        if (position == mFragments.size) {
            val vIMageSelector = ViewImageSelectorAdapter()
            vIMageSelector.requestedImageSize = requestedSize
            when (showMode) {
                ViewImageSelectorAdapter.ImageLocation.ASSETS -> {
                    arrayAssetPath?.let { vIMageSelector.setWithAssetsImage(it[position]) }
                }
                ViewImageSelectorAdapter.ImageLocation.RESOURCES -> {
                    arrayResId?.let { vIMageSelector.setWithResourceImage(it[position]) }
                }
            }
            mFragments.add(vIMageSelector)
        }
        return mFragments[position]
    }

    override fun getCount(): Int = when (showMode) {
        ViewImageSelectorAdapter.ImageLocation.ASSETS -> arrayAssetPath?.size ?: 0
        ViewImageSelectorAdapter.ImageLocation.RESOURCES -> arrayResId?.size ?: 0
    }

}