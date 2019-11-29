package com.mizzugi.kensiro.app.panri.plugin.onMain

import android.graphics.Typeface
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.cardview.widget.CardView
import com.mizzugi.kensiro.app.panri.BaseActivity
import com.mizzugi.kensiro.app.panri.R
import com.mizzugi.kensiro.app.panri.activity.DiseaseInfo
import com.mizzugi.kensiro.app.panri.activity.HowToResolveActivity
import com.mizzugi.kensiro.app.panri.activity.ImageDiagnose
import com.mizzugi.kensiro.app.panri.activity.StartDiagnose
import com.mizzugi.kensiro.app.panri.plugin.SwitchActivity

object InitialSectOpIntentModule {

    private val listMenuOption: List<MenuOption> = listOf(
        MenuOption(
            R.drawable.ic_actmain_diagnose,
            R.string.actmain_string_startdiagnose,
            StartDiagnose::class.java
        ),
        MenuOption(
            R.drawable.ic_actmain_imgdiagnose,
            R.string.actmain_string_diagnosagambar,
            ImageDiagnose::class.java
        ),
        MenuOption(
            R.drawable.ic_actmain_howto,
            R.string.actmain_string_howto,
            HowToResolveActivity::class.java
        ),
        MenuOption(
            R.drawable.ic_actmain_aboutpenyakit,
            R.string.actmain_string_aboutpenyakit,
            DiseaseInfo::class.java
        )
    )

    fun setup(activity: BaseActivity, holder: LinearLayout) {
        for (menuOption in listMenuOption) {
            val cardView = LayoutInflater.from(activity).inflate(
                R.layout.cardview_adapter,
                holder,
                false
            ) as CardView
            cardView.apply {
                val layout = LinearLayout.inflate(
                    activity,
                    R.layout.module_content_op_incard,
                    null
                ) as LinearLayout
                // text img
                (layout.getChildAt(1) as TextView).apply {
                    setTypeface(
                        Typeface.createFromAsset(activity.assets, "Gill_SansMT.ttf"),
                        Typeface.BOLD_ITALIC
                    )
                    setText(menuOption.resString)
                }

                // image
                (layout.getChildAt(0) as ImageView).apply {
                    setImageResource(menuOption.drawableImage)
                }

                setContentPadding(10, 10, 10, 10)
                addView(layout)
                setOnClickListener {
                    SwitchActivity.switchTo(activity, menuOption.cls, null)
                }
            }
            holder.addView(cardView)
        }
    }

    data class MenuOption(
        @DrawableRes val drawableImage: Int,
        @StringRes val resString: Int,
        val cls: Class<*>
    )
}