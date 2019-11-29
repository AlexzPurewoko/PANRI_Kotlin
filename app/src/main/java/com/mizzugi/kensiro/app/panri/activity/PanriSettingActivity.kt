package com.mizzugi.kensiro.app.panri.activity

import android.content.pm.ActivityInfo
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import androidx.core.content.ContextCompat
import com.mizzugi.kensiro.app.panri.BaseActivity
import com.mizzugi.kensiro.app.panri.R
import com.mizzugi.kensiro.app.panri.fragment.PanriSettingFragment
import id.apwdevs.library.text.style.CustomTypefaceSpan
import kotlinx.android.synthetic.main.activity_panri_setting.*

class PanriSettingActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_panri_setting)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setMyActionBar()
        supportFragmentManager.beginTransaction()
            .replace(R.id.content_frame_setting, PanriSettingFragment()).commit()
    }

    private fun setMyActionBar() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            toolbar.title = SpannableString(title).apply {
                setSpan(
                    CustomTypefaceSpan(
                        Typeface.createFromAsset(assets, "Gecko_PersonalUseOnly.ttf")
                    ), 0, title.length, 0
                )
            }
        }
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimaryDark)
    }
}
