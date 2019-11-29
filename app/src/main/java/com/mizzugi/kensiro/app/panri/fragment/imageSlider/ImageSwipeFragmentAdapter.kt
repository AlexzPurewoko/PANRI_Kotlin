package com.mizzugi.kensiro.app.panri.fragment.imageSlider

import android.graphics.Point
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.mizzugi.kensiro.app.panri.R
import com.mizzugi.kensiro.app.panri.adapter.CustomViewPager
import com.mizzugi.kensiro.app.panri.viewmodel.ImageSliderViewModel

class ImageSwipeFragmentAdapter private constructor() : Fragment() {

    private lateinit var customViewPager: CustomViewPager
    private lateinit var textIndicatorViewPager: TextView
    private lateinit var layoutIndicator: LinearLayout

    private lateinit var mImageSliderAdapter: ImageSwipeAdapter


    private lateinit var mImageSliderViewModel: ImageSliderViewModel

    private val mDots: MutableList<LinearLayout> = mutableListOf()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.image_slider_main_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.apply {
            customViewPager = findViewById(R.id.actmain_id_viewpagerimg)
            textIndicatorViewPager = findViewById(R.id.actmain_id_textIndicatorViewPager)
            layoutIndicator = findViewById(R.id.actmain_id_layoutIndicators)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setTextIndicator()
        setupViewModel()
        prepareViewPager()

    }

    override fun onPause() {
        super.onPause()
        mImageSliderViewModel.stopAnimation()
    }

    override fun onResume() {
        super.onResume()
        mImageSliderViewModel.playAnimation()
    }

    private fun prepareViewPager() {
        val point = requireArguments().getParcelable<Point>(KEY_RECT_SIZE)

        mImageSliderAdapter = ImageSwipeAdapter(
            requireFragmentManager(),
            ImageSliderViewModel.LIST_ALL_IMAGE,
            requireNotNull(point)
        )
        customViewPager.apply {
            adapter = mImageSliderAdapter
            setCurrentItem(0, true)
            mOnClickEvent = View.OnClickListener {
                mImageSliderViewModel.click()
            }

        }
    }

    private fun setupViewModel() {
        mImageSliderViewModel = ViewModelProviders.of(this).get(ImageSliderViewModel::class.java)
        mImageSliderViewModel.apply {
            currentIndicatorPosition.observe(this@ImageSwipeFragmentAdapter, Observer {
                val prevPos =
                    if (it - 1 < 0) ImageSliderViewModel.DOT_INDICATOR_COUNT - 1 else it - 1
                Log.d("PositionIndicator", "prevPos : $prevPos, pos $it.")
                setUnselectedIndicator(mDots[prevPos])
                setSelectedIndicator(mDots[it])

                customViewPager.setCurrentItem(it, true)
            })
            initializeIndicatorDots(requireActivity(), mDots, layoutIndicator)
            timeUpdateBetweenImage = requireArguments().getLong(KEY_TIME_IMAGE)
        }
    }

    private fun setTextIndicator() {
        textIndicatorViewPager.apply {
            setTypeface(
                Typeface.createFromAsset(requireContext().assets, "Gill_SansMT.ttf"),
                Typeface.ITALIC
            )
            text = "MUDAHKAN HIDUPMU KENALI PENYAKIT PADIMU"
        }
    }

    companion object {
        private const val KEY_RECT_SIZE = "RECT_REQUIRE_SIZE"
        private const val KEY_TIME_IMAGE = "TIME_BETWEEN_IMAGE"


        @JvmStatic
        fun newInstance(rectSize: Point, timeBetweenImage: Long): ImageSwipeFragmentAdapter =
            ImageSwipeFragmentAdapter().apply {
                arguments = Bundle().apply {
                    putParcelable(KEY_RECT_SIZE, rectSize)
                    putLong(KEY_TIME_IMAGE, timeBetweenImage)
                }
            }
    }
}