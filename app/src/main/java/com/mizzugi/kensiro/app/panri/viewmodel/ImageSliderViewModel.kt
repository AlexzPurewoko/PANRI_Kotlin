package com.mizzugi.kensiro.app.panri.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import android.view.Gravity
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.mizzugi.kensiro.app.panri.R
import kotlinx.coroutines.*

class ImageSliderViewModel(application: Application) : AndroidViewModel(application) {

    val currentIndicatorPosition: MutableLiveData<Int> = MutableLiveData()
    //private val currentTextLeftIndicator: MutableLiveData<String> = MutableLiveData()
    var timeUpdateBetweenImage: Long = 6000

    private var deferredAutoUpdate: Deferred<Boolean>? = null

    init {
        currentIndicatorPosition.value = 0
    }

    fun playAnimation() {
        if (deferredAutoUpdate == null) {
            deferredAutoUpdate = GlobalScope.async {
                while (isActive) {
                    delay(timeUpdateBetweenImage)
                    if (!isActive) break
                    var currentPos = currentIndicatorPosition.value ?: 0
                    currentIndicatorPosition.postValue(
                        if (++currentPos >= DOT_INDICATOR_COUNT) 0
                        else currentPos
                    )
                }
                false
            }
            deferredAutoUpdate?.start()
        } else {
            Log.e("AutoUpdate", "You must stop the animation running first then play it again!")
            throw IllegalAccessException("AutoUpdate: The Animation is currently playing, You must stop first, then play it again")
        }
    }

    fun stopAnimation() {
        deferredAutoUpdate?.cancel()
        deferredAutoUpdate = null
    }

    fun click() {
        GlobalScope.launch {
            stopAnimation()
            var currentPos = currentIndicatorPosition.value ?: 0
            if (++currentPos == DOT_INDICATOR_COUNT)
                currentPos = 0
            currentIndicatorPosition.postValue(currentPos)
            playAnimation()
        }
    }

    @SuppressLint("RtlHardcoded")
    fun initializeIndicatorDots(
        activity: FragmentActivity,
        mDotsReference: MutableList<LinearLayout>,
        indicatorHolder: LinearLayout
    ) {
        for (i in 0 until DOT_INDICATOR_COUNT) {
            val layouts = LinearLayout(activity).apply {
                setBackgroundResource(IndicatorType.UNSELECTED.drawableRes)
                gravity = Gravity.RIGHT or Gravity.BOTTOM
            }
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 4, 4)
            }
            mDotsReference.add(layouts)
            indicatorHolder.addView(layouts, params)
        }
        setSelectedIndicator(mDotsReference[0])
    }


    override fun onCleared() {
        stopAnimation()
    }

    fun setSelectedIndicator(mDot: LinearLayout) {
        mDot.setBackgroundResource(IndicatorType.SELECTED.drawableRes)
    }

    fun setUnselectedIndicator(mDot: LinearLayout) {
        mDot.setBackgroundResource(IndicatorType.UNSELECTED.drawableRes)
    }

    enum class IndicatorType(@DrawableRes val drawableRes: Int) {
        UNSELECTED(R.drawable.indicator_unselected_item_oval),
        SELECTED(R.drawable.indicator_selected_item_oval)
    }

    companion object {
        val LIST_ALL_IMAGE = intArrayOf(
            R.drawable.viewpager_area_1,
            R.drawable.viewpager_area_2,
            R.drawable.viewpager_area_3,
            R.drawable.viewpager_area_4
        )
        val DOT_INDICATOR_COUNT = LIST_ALL_IMAGE.size

    }
}