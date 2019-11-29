package com.mizzugi.kensiro.app.panri.activity

import android.content.pm.ActivityInfo
import android.graphics.Point
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.mizzugi.kensiro.app.panri.R
import com.mizzugi.kensiro.app.panri.adapter.ImageGridViewAdapter
import com.mizzugi.kensiro.app.panri.plugin.LoadingDialogPlugin
import com.mizzugi.kensiro.app.panri.viewmodel.GalleryActivityViewModel
import id.apwdevs.library.text.style.CustomTypefaceSpan
import kotlinx.android.synthetic.main.activity_gallery.*
import kotlinx.android.synthetic.main.content_gallery.*
import java.io.File
import kotlin.math.roundToInt

class GalleryActivity : AppCompatActivity() {

    private lateinit var mViewModel: GalleryActivityViewModel
    private var adapterGrid: ImageGridViewAdapter? = null
    private lateinit var dialogShowHelper: LoadingDialogPlugin

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setMyActionBar()

        dialogShowHelper = LoadingDialogPlugin(this)
        dialogShowHelper.buildLoadingLayout()
        mViewModel = ViewModelProviders.of(this).get(GalleryActivityViewModel::class.java)
        mViewModel.apply {
            finishedLoading.observe(this@GalleryActivity, Observer {
                if (!it) dialogShowHelper.showDialog()
                else dialogShowHelper.stopDialog()
            })

            dataPathImage.observe(this@GalleryActivity, Observer {
                val p = Point()
                windowManager.defaultDisplay.getSize(p)
                val dimen = resources.getDimension(R.dimen.margin_img_penyakit).roundToInt()
                if (adapterGrid == null) {
                    adapterGrid = ImageGridViewAdapter(
                        this@GalleryActivity,
                        p,
                        File(filesDir, "data/images/list"),
                        actgallery_id_gridimage
                    )

                }
                adapterGrid?.apply {
                    columnCount = 2
                    setMargin(dimen, dimen, dimen, dimen, dimen, 0)
                    setListLocationFileImages(it, "")
                    buildAndShow()
                }
            })

        }
        mViewModel.loadImage()
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
