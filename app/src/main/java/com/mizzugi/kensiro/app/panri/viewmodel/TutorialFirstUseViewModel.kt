package com.mizzugi.kensiro.app.panri.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.mizzugi.kensiro.app.panri.R

class TutorialFirstUseViewModel(application: Application) : AndroidViewModel(application) {

    val mCurrentFragmentPosition: MutableLiveData<Int> = MutableLiveData()
    var changeType = 0

    init {
        mCurrentFragmentPosition.value = 0
    }

    fun onBtnClicked(clickedType: Int) {
        var currentValue = mCurrentFragmentPosition.value ?: 0

        when (clickedType) {
            ON_BTN_BACK_CLICKED -> {
                if (--currentValue <= 0) {
                    currentValue = 0
                }
            }
            ON_BTN_NEXT_CLICKED -> {
                ++currentValue
            }
        }
        setCurrentFragmentPosition(BTN_CHANGE, currentValue)
    }

    fun setCurrentFragmentPosition(changeType: Int, newPosition: Int) {
        this.changeType = changeType
        mCurrentFragmentPosition.postValue(newPosition)
    }

    companion object {
        const val ON_BTN_BACK_CLICKED = 0xaaf
        const val ON_BTN_NEXT_CLICKED = 0xbeef

        const val BTN_CHANGE = 0x9a
        const val SWIPE_CHANGE = 0x8aa

        // fragment section


        const val COUNT_ALL_FRAGMENTS = 4
        const val mDotCount = COUNT_ALL_FRAGMENTS

        val colorResListsBtn = intArrayOf(
            R.color.frag_color_green,
            R.color.frag_color_green,
            R.color.colorPrimary,
            R.color.colorPrimary
        )

        val drawableSelectedPositionRes = intArrayOf(
            R.drawable.indicator_selected_item_oval_green,
            R.drawable.indicator_selected_item_oval_green,
            R.drawable.indicator_selected_item_oval,
            R.drawable.indicator_selected_item_oval
        )

        val colorStatusBars = intArrayOf(
            R.color.frag_color_green_dark,
            R.color.frag_color_green_dark,
            R.color.colorPrimaryDark,
            R.color.colorPrimaryDark
        )


    }
}