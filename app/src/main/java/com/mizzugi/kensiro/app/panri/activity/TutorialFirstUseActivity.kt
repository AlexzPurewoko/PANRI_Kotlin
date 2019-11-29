package com.mizzugi.kensiro.app.panri.activity

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.mizzugi.kensiro.app.panri.BaseActivity
import com.mizzugi.kensiro.app.panri.R
import com.mizzugi.kensiro.app.panri.adapter.TutorialFirstUseFragmentAdapter
import com.mizzugi.kensiro.app.panri.fragment.onTutorialFirstUse.FragmentFirstUse1
import com.mizzugi.kensiro.app.panri.fragment.onTutorialFirstUse.FragmentFirstUse2
import com.mizzugi.kensiro.app.panri.fragment.onTutorialFirstUse.FragmentFirstUse3
import com.mizzugi.kensiro.app.panri.fragment.onTutorialFirstUse.FragmentFirstUse4
import com.mizzugi.kensiro.app.panri.plugin.SwitchActivity
import com.mizzugi.kensiro.app.panri.plugin.invisible
import com.mizzugi.kensiro.app.panri.plugin.visible
import com.mizzugi.kensiro.app.panri.viewmodel.TutorialFirstUseViewModel
import kotlinx.android.synthetic.main.activity_tutorial_first_use.*
import kotlin.math.roundToInt

class TutorialFirstUseActivity : BaseActivity(), FragmentFirstUse4.OnFragmentLastFinalRequest {

    private lateinit var mTutorialFirstUseViewModel: TutorialFirstUseViewModel
    private var allFragment = arrayOf(
        FragmentFirstUse1(),
        FragmentFirstUse2(),
        FragmentFirstUse3(),
        FragmentFirstUse4()
    )
    private lateinit var mDots: MutableList<ImageView>
    private lateinit var buttonBack: Button
    private lateinit var buttonNext: Button
    private lateinit var onPageChangeListener: ViewPager.OnPageChangeListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tutorial_first_use)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        mTutorialFirstUseViewModel =
            ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory(application)).get(
                TutorialFirstUseViewModel::class.java
            )
        setupLayout()
        setupFragment()
        setupViewModel()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) window.statusBarColor =
            ContextCompat.getColor(this, TutorialFirstUseViewModel.colorStatusBars[0])

    }

    private fun setupViewModel() {
        mTutorialFirstUseViewModel.mCurrentFragmentPosition.observe(this, Observer {
            configureButton(it)
            when (mTutorialFirstUseViewModel.changeType) {
                TutorialFirstUseViewModel.BTN_CHANGE -> {
                    acttutor_id_placeholder.setCurrentItem(it, true)
                }
                TutorialFirstUseViewModel.SWIPE_CHANGE -> {

                }
                else -> return@Observer
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) window.statusBarColor =
                ContextCompat.getColor(this, TutorialFirstUseViewModel.colorStatusBars[it])
            setLayoutBg(TutorialFirstUseViewModel.colorResListsBtn[it])
            repositionDots(it)
        })
    }

    private fun repositionDots(position: Int) {
        for (index in 0 until TutorialFirstUseViewModel.mDotCount) {
            mDots[index].setBackgroundResource(R.drawable.indicator_unselected_item_oval)
        }
        mDots[position].setBackgroundResource(TutorialFirstUseViewModel.drawableSelectedPositionRes[position])
    }

    private fun configureButton(position: Int) {
        when {
            position <= 0 -> {
                buttonBack.invisible()
                buttonNext.visible()
            }
            position == TutorialFirstUseViewModel.COUNT_ALL_FRAGMENTS - 1 -> {
                buttonNext.invisible()
                buttonBack.visible()
            }
            else -> {
                buttonNext.visible()
                buttonBack.visible()
            }
        }
    }

    private fun setupLayout() {
        buttonBack = acttutor_id_btnprev.apply {
            visibility = View.INVISIBLE
            setOnClickListener {
                mTutorialFirstUseViewModel.onBtnClicked(TutorialFirstUseViewModel.ON_BTN_BACK_CLICKED)
            }
        }
        buttonNext = acttutor_id_btnnext.apply {
            setOnClickListener {
                mTutorialFirstUseViewModel.onBtnClicked(TutorialFirstUseViewModel.ON_BTN_NEXT_CLICKED)
            }
        }
        initializeIndicators()
        setLayoutBg(TutorialFirstUseViewModel.colorResListsBtn[0])
    }


    private fun setupFragment() {
        // sets the fragment4 callbacks
        (allFragment[3] as FragmentFirstUse4).onFinalRequests = this
        acttutor_id_placeholder.apply {
            adapter = TutorialFirstUseFragmentAdapter(supportFragmentManager, allFragment)
            onPageChangeListener = object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {

                }

                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {

                }

                override fun onPageSelected(position: Int) {
                    mTutorialFirstUseViewModel.setCurrentFragmentPosition(
                        TutorialFirstUseViewModel.SWIPE_CHANGE,
                        position
                    )
                }

            }
            addOnPageChangeListener(onPageChangeListener)
        }

    }

    override fun onBtnFinalClicked(results: Fragment, btn: View) {
        finish()
        SwitchActivity.switchTo(this, MainActivity::class.java, intent.extras)
    }

    private fun setLayoutBg(resColor: Int) {
        acttutor_id_mainlayout.setBackgroundColor(ContextCompat.getColor(this, resColor))
    }

    @SuppressLint("RtlHardcoded")
    private fun initializeIndicators() {
        mDots = mutableListOf()

        val marginLR = resources.getDimension(R.dimen.acttutor_indicator_marginlr).roundToInt()
        for (index in 0 until TutorialFirstUseViewModel.mDotCount) {
            val dot = ImageView(this).apply {
                setBackgroundResource(R.drawable.indicator_unselected_item_oval)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(marginLR, 0, marginLR, 4)
                    gravity = Gravity.RIGHT or Gravity.BOTTOM or Gravity.END
                }
            }
            mDots.add(
                dot
            )
            acttutor_id_layoutIndicators.addView(mDots[index])
        }
        mDots[0].setBackgroundResource(TutorialFirstUseViewModel.drawableSelectedPositionRes[0])
    }

    override fun onDestroy() {
        super.onDestroy()
        acttutor_id_placeholder.removeOnPageChangeListener(onPageChangeListener)
    }
}
