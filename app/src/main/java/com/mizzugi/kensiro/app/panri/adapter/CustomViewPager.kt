package com.mizzugi.kensiro.app.panri.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

class CustomViewPager : ViewPager {
    var mOnClickEvent: OnClickListener? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        ev?.apply {
            if (action == MotionEvent.ACTION_UP) {
                mOnClickEvent?.onClick(this@CustomViewPager)
                return true
            }
        }
        return super.onTouchEvent(ev)
    }
}