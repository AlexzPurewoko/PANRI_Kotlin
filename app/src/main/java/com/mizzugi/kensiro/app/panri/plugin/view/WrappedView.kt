package com.mizzugi.kensiro.app.panri.plugin.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import kotlin.math.max


class WrappedView : ViewGroup {
    private var lineHeight = 0

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        assert(MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.UNSPECIFIED)
        val w = MeasureSpec.getSize(widthMeasureSpec) - paddingLeft - paddingRight
        var h = MeasureSpec.getSize(heightMeasureSpec) - paddingTop - paddingBottom

        var lineHeight = 0
        val count = childCount
        var xpos = paddingLeft
        var ypos = paddingRight

        val childHeightMeasureSpec =
            if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST)
                MeasureSpec.makeMeasureSpec(h, MeasureSpec.AT_MOST)
            else
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)

        for (i in 0 until count) {
            getChildAt(i).apply {
                if (visibility != GONE) {
                    measure(
                        MeasureSpec.makeMeasureSpec(w, MeasureSpec.AT_MOST),
                        childHeightMeasureSpec
                    )
                    val lp = layoutParams as LayoutParams
                    lineHeight = max(lineHeight, measuredHeight + lp.verticalSpacing)

                    if (xpos + measuredWidth > w) {
                        xpos = paddingLeft
                        ypos += lineHeight
                    }
                    xpos += measuredWidth + lp.horizontalSpacing
                }
            }
        }

        this.lineHeight = lineHeight
        when (MeasureSpec.getMode(heightMeasureSpec)) {
            MeasureSpec.UNSPECIFIED -> h = ypos + lineHeight
            MeasureSpec.AT_MOST -> if (ypos + lineHeight < h) {
                h = ypos + lineHeight
            }
            else -> {
            }
        }
        setMeasuredDimension(w, h)
    }

    override fun generateDefaultLayoutParams(): ViewGroup.LayoutParams {
        return LayoutParams(2, 2)
    }

    override fun generateLayoutParams(p: ViewGroup.LayoutParams?): ViewGroup.LayoutParams {
        return LayoutParams(2, 2)
    }

    /*
    fun addImageIcon(@DrawableRes resId: Int, otherSettings: (view: ImageView) -> Unit) {
        addView(
            (LayoutInflater.from(context).inflate(
                R.layout.item_icon_socmed,
                this,
                false
            ) as ImageView).apply {
                setImageResource(resId)
                otherSettings(this)
            }
        )
    }

    fun addText(str: CharSequence?, isLink: Boolean = false) {
        if (str.isNullOrEmpty()) return

        addView(
            (LayoutInflater.from(context)
                .inflate(R.layout.item_genre, this, false) as TextView).apply {
                text = str
                linksClickable = isLink
            }
        )
    }
*/

    override fun checkLayoutParams(p: ViewGroup.LayoutParams?): Boolean = p is LayoutParams
    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val count = childCount
        val w = r - l
        var xpos = paddingLeft
        var ypos = paddingTop
        for (i in 0 until count) {
            getChildAt(i).apply {
                if (visibility != View.GONE) {
                    val childW = measuredWidth
                    val childH = measuredHeight
                    val lp = layoutParams as LayoutParams
                    if (xpos + childW > w) {
                        xpos = paddingLeft
                        ypos += lineHeight
                    }
                    layout(xpos, ypos, xpos + childW, ypos + childH)
                    xpos += childW + lp.horizontalSpacing
                }
            }
        }
    }

    companion object {
        class LayoutParams(internal val horizontalSpacing: Int, internal val verticalSpacing: Int) :
            ViewGroup.LayoutParams(0, 0)
    }
}