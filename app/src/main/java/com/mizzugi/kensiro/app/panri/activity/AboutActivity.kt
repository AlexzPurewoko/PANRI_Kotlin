package com.mizzugi.kensiro.app.panri.activity

import android.content.pm.ActivityInfo
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.view.Menu
import android.view.MenuItem
import androidx.core.content.ContextCompat
import com.mizzugi.kensiro.app.panri.BaseActivity
import com.mizzugi.kensiro.app.panri.BuildConfig
import com.mizzugi.kensiro.app.panri.R
import com.mizzugi.kensiro.app.panri.plugin.DialogPlugin
import id.apwdevs.library.text.style.CustomTypefaceSpan
import kotlinx.android.synthetic.main.activity_about.*
import kotlinx.android.synthetic.main.content_about.*

class AboutActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setMyActionBar()
        setLayout()
    }

    private fun setLayout() {
        actabout_id_txtjudul.apply {
            setTypeface(
                Typeface.createFromAsset(assets, "Gecko_PersonalUseOnly.ttf"),
                Typeface.BOLD
            )
        }
        actabout_id_txtversion.append(BuildConfig.VERSION_NAME)
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.about_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.privacy_policy -> {
                DialogPlugin.showDialogAboutSKPP(this, "kp.spanf", "Kebijakan Privasi").show()
                return true
            }
            R.id.app_usage_policy -> {
                DialogPlugin.showDialogAboutSKPP(this, "sk.spanf", "Syarat & Ketentuan").show()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
