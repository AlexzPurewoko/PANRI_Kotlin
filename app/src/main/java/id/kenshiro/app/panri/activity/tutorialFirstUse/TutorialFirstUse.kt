/**
 * Copyright @2019 Alexzander Purwoko Widiantoro <purwoko908@gmail.com> HOPEProjects
 *
 * This file is a part of PANRI Android Application
 * Except @author, you must not be able to modify, copying, change this file, because its closed source programs
 * This file is patented by any copyright law
 */

package id.kenshiro.app.panri.activity.tutorialFirstUse

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.crashlytics.android.Crashlytics
import id.kenshiro.app.panri.MainActivity
import id.kenshiro.app.panri.R
import id.kenshiro.app.panri.activity.tutorialFirstUse.fragmentList.*
import id.kenshiro.app.panri.params.TutorialFirstUseActivityParams
import id.kenshiro.app.panri.utility.gone
import id.kenshiro.app.panri.utility.visible
import io.fabric.sdk.android.Fabric
import kotlinx.android.synthetic.main.acttutor_firstuse_main.*
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.backgroundResource

class TutorialFirstUse : AppCompatActivity() {

    private lateinit var btnPrev: Button
    private lateinit var btnNext: Button
    private lateinit var dotIndicatorLayout: LinearLayout
    private lateinit var listDot: Array<LinearLayout?>
    private lateinit var viewHolder: ViewPager

    private var currentPosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.acttutor_firstuse_main)
        // initialize Fabrics
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        val fabric = Fabric.Builder(this).kits(Crashlytics()).debuggable(true).build()
        Fabric.with(fabric)
        initializeUI()
    }

    private fun moveIntoMain() {
        val extra = intent.extras
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtras(extra)
        finish()
        startActivity(intent)
    }


    private fun initializeUI() {
        // filling the fields before computing
        btnPrev = acttutor_id_btnprev
        btnNext = acttutor_id_btnnext
        viewHolder = acttutor_id_placeholder

        // sets the btnPrev to gone
        btnPrev.gone()

        //sets button click listener
        btnPrev.setOnClickListener {
            onEventOcurred(it, TutorialFirstUseActivityParams.ON_BTN_BACK_CLICKED)
        }
        btnNext.setOnClickListener {
            onEventOcurred(it, TutorialFirstUseActivityParams.ON_BTN_NEXT_CLICKED)
        }

        // sets the fragment pager adapter
        viewHolder.adapter = ViewPagerStateAdapter(supportFragmentManager, object : OnBtnFragmentClicked {
            override fun onClicked(source: Fragment, btn: View) {
                onEventOcurred(btn, TutorialFirstUseActivityParams.ON_FINALIZE_BTN_CLICKED)
            }

        })

        // sets the callback
        viewHolder.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> {
                        btnPrev.gone()
                    }
                    1, 2 -> {
                        btnNext.visible()
                        btnPrev.visible()
                    }
                    3 -> {
                        btnNext.gone()
                    }
                }

                updateUi(position)
                currentPosition = position
            }

        })


        // sets the dotImage Configuration
        dotIndicatorLayout = acttutor_id_layoutIndicators

        // sets the indicator
        listDot = arrayOfNulls(TutorialFirstUseActivityParams.DOT_COUNT_INDICATORS)
        val marginIndicators = Math.round(resources.getDimension(R.dimen.acttutor_indicator_marginlr))
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(marginIndicators, 0, marginIndicators, 4)
        for (index in listDot.indices) {
            listDot[index] = LinearLayout(this)
            listDot[index]?.backgroundResource = R.drawable.indicator_unselected_item_oval
            listDot[index]?.gravity = Gravity.RIGHT or Gravity.BOTTOM or Gravity.END
            dotIndicatorLayout.addView(listDot[index], layoutParams)
        }
        listDot[currentPosition]?.backgroundResource =
            TutorialFirstUseActivityParams.INDICATOR_DRAWABLE_SELECTION[currentPosition]
        setDisplayBackgrounds(TutorialFirstUseActivityParams.COLOR_RES_LISTS[currentPosition])
        // tinting status bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val selectedColors = TutorialFirstUseActivityParams.LIST_STATUS_BAR_COLORS[0]
            window.statusBarColor =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) getColor(selectedColors) else resources.getColor(
                    selectedColors
                )
        }
    }

    private fun updateUi(position: Int) {
        // tinting status bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val selectedColors = TutorialFirstUseActivityParams.LIST_STATUS_BAR_COLORS[position]
            window.statusBarColor =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) getColor(selectedColors) else resources.getColor(
                    selectedColors
                )
        }

        // sets the background
        setDisplayBackgrounds(TutorialFirstUseActivityParams.COLOR_RES_LISTS[position])

        // sets the current indicators
        for (dot in listDot) {
            dot?.backgroundResource = R.drawable.indicator_unselected_item_oval
        }
        listDot[position]?.backgroundResource = TutorialFirstUseActivityParams.INDICATOR_DRAWABLE_SELECTION[position]
    }

    private fun onEventOcurred(sourceView: View, typeOfEvent: Int) {
        when (typeOfEvent) {
            TutorialFirstUseActivityParams.ON_BTN_NEXT_CLICKED -> {
                viewHolder.setCurrentItem(++currentPosition, true)

            }
            TutorialFirstUseActivityParams.ON_BTN_BACK_CLICKED -> {
                viewHolder.setCurrentItem(--currentPosition, true)
            }
            TutorialFirstUseActivityParams.ON_FINALIZE_BTN_CLICKED -> {
                moveIntoMain()
            }
        }
    }

    private fun setDisplayBackgrounds(resColors: Int) {
        acttutor_id_mainlayout.backgroundColor =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) getColor(resColors) else resources.getColor(resColors)
    }

    private inner class ViewPagerStateAdapter(fm: FragmentManager, listener: OnBtnFragmentClicked) :
        FragmentStatePagerAdapter(fm) {

        private val listFragments: Array<Fragment> = arrayOf(
            FragmentFirstUse1.newInstance(),
            FragmentFirstUse2.newInstance(),
            FragmentFirstUse3.newInstance(),
            FragmentFirstUse4.newInstance(listener)
        )

        override fun getItem(position: Int): Fragment {
            return listFragments[position]
        }

        override fun getCount(): Int {
            return TutorialFirstUseActivityParams.COUNT_ALL_FRAGMENTS
        }

    }

}
