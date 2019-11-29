package com.mizzugi.kensiro.app.panri.fragment

import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.mizzugi.kensiro.app.panri.R
import com.mizzugi.kensiro.app.panri.adapter.CustomViewPager
import com.mizzugi.kensiro.app.panri.fragment.imageSlider.ImageSwipeAdapter
import com.mizzugi.kensiro.app.panri.plugin.callbacks.OnRequestDialog
import com.mizzugi.kensiro.app.panri.plugin.visible
import com.mizzugi.kensiro.app.panri.viewmodel.ImageDiagnoseViewModel
import kotlin.math.roundToInt

class ImageDiagnoseFragment : Fragment() {

    private lateinit var mParentLayout: RelativeLayout
    private lateinit var mScrollContent: ScrollView
    private lateinit var mChildContent: LinearLayout
    private lateinit var mCardContent: CardView

    private lateinit var textTitle: TextView
    private lateinit var customViewPager: CustomViewPager
    private lateinit var linearIndicator: LinearLayout
    private lateinit var btnYes: Button
    private lateinit var btnNo: Button
    private lateinit var textCiri: TextView
    private lateinit var base: LinearLayout

    private val mDots: MutableList<LinearLayout> = mutableListOf()


    private lateinit var mBottomBtn: LinearLayout
    private var onFragmentDiagnose: OnFragmentImgDiagnose? = null
    private var onRequestDialog: OnRequestDialog? = null

    private var mImageDiagnoseViewModel: ImageDiagnoseViewModel? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentImgDiagnose)
            onFragmentDiagnose = context
        if (context is OnRequestDialog)
            onRequestDialog = context

    }

    override fun onDetach() {
        super.onDetach()
        onRequestDialog = null
        onFragmentDiagnose = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return RelativeLayout(inflater.context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mParentLayout = view as RelativeLayout
        mParentLayout.apply {
            mScrollContent = ScrollView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                gravity = Gravity.TOP
            }

            mChildContent = LinearLayout(context).apply {
                gravity = Gravity.TOP
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
                orientation = LinearLayout.VERTICAL
                visible()
            }

            mScrollContent.addView(mChildContent)
            addView(mScrollContent, RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                addRule(RelativeLayout.ABOVE, R.id.adapter_id_imgdiag_layout)
                addRule(RelativeLayout.ALIGN_PARENT_TOP, 1)
            })
            mBottomBtn = LayoutInflater.from(context).inflate(
                R.layout.fragment_adapter_btnbottom_diag,
                this,
                false
            ) as LinearLayout
            addView(mBottomBtn)

        }

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        buildCardLayout()
        mImageDiagnoseViewModel =
            ViewModelProviders.of(this).get(ImageDiagnoseViewModel::class.java)
        mImageDiagnoseViewModel?.apply {
            titleText.observe(this@ImageDiagnoseFragment, Observer {
                textTitle.text = it
            })
            contentText.observe(this@ImageDiagnoseFragment, Observer {
                textCiri.text = it
            })
            finishedLoading.observe(this@ImageDiagnoseFragment, Observer {
                onRequestDialog?.requestDialog(!it)
            })
            dataImageID.observe(this@ImageDiagnoseFragment, Observer {
                stop()
                linearIndicator.removeAllViews()
                mDots.clear()
                initializeIndicatorDots(requireActivity(), mDots, linearIndicator)
                customViewPager.apply {

                    val reqSize = Point()
                    requireActivity().windowManager.defaultDisplay.getSize(reqSize)
                    reqSize.y =
                        requireActivity().resources.getDimension(R.dimen.actmain_dimen_viewpager_height)
                            .roundToInt()
                    adapter = ImageSwipeAdapter(
                        requireFragmentManager(),
                        it.toTypedArray(),
                        reqSize
                    ).apply {
                    }
                    mOnClickEvent = View.OnClickListener {
                        clickImage()
                    }
                    currentItem = 0

                }
                play()
            })
            currentIndicatorPosition.observe(this@ImageDiagnoseFragment, Observer {
                if (dataImageID.value.isNullOrEmpty()) return@Observer
                val prevPos = if (it - 1 < 0) (dataImageID.value?.size ?: 0) - 1 else it - 1
                Log.d("PositionIndicator", "prevPos : $prevPos, pos $it.")
                setUnselectedIndicator(mDots[prevPos])
                setSelectedIndicator(mDots[it])
                customViewPager.setCurrentItem(it, true)
            })

            isOnLast.observe(this@ImageDiagnoseFragment, Observer {
                if (it) {
                    onFragmentDiagnose?.onIsAfterLastListPosition(this.mCurrentPos, mDataSize)
                    Log.e("IsOnTheLast", "OnLast")
                }
            })
            click()
        }
    }

    private fun buildCardLayout() {
        mCardContent = LayoutInflater.from(requireContext()).inflate(
            R.layout.fragment_adapter_imgdiagnose,
            mScrollContent,
            false
        ) as CardView
        mCardContent.apply {
            textTitle = findViewById(R.id.actimgdiagnose_judulpenyakit)
            customViewPager = findViewById(R.id.actimgdiagnose_id_viewpagerimg)
            btnYes = mBottomBtn.findViewById(R.id.actimgdiagnose_buttonya)
            btnNo = mBottomBtn.findViewById(R.id.actimgdiagnose_buttontidak)
            base = findViewById(R.id.adapterdim_basecontent)
            textCiri = findViewById(R.id.imgdiagnose_ciriciri)
            linearIndicator = findViewById(R.id.actimgdiagnose_id_layoutIndicators)
            btnYes.apply {
                setTypeface(
                    Typeface.createFromAsset(requireContext().assets, "Gill_SansMT.ttf"),
                    Typeface.BOLD
                )
                setTextColor(Color.WHITE)
                setOnClickListener {
                    onFragmentDiagnose?.onBtnClicked(
                        ImgDiagnoseBtnType.BTN_YES_CLICKED,
                        mImageDiagnoseViewModel?.dataCiriDisease?.num ?: 0
                    )
                }
            }

            btnNo.apply {
                setTypeface(
                    Typeface.createFromAsset(
                        requireContext().assets,
                        "Gill_SansMT.ttf"
                    ), Typeface.BOLD
                )
                setTextColor(Color.WHITE)
                setOnClickListener {
                    mImageDiagnoseViewModel?.click()
                    onFragmentDiagnose?.onBtnClicked(
                        ImgDiagnoseBtnType.BTN_NO_CLICKED,
                        mImageDiagnoseViewModel?.dataCiriDisease?.num ?: 0
                    )
                }
            }

        }
        mChildContent.addView(mCardContent)
    }

    override fun onResume() {
        super.onResume()
        mImageDiagnoseViewModel?.play()
    }

    override fun onPause() {
        super.onPause()
        mImageDiagnoseViewModel?.stop()
    }

    fun overrideBackButtonPressed(): Boolean {
        return mImageDiagnoseViewModel?.back() ?: false
    }

    fun resetCounter() {
        mImageDiagnoseViewModel?.resetCounter()
    }


    interface OnFragmentImgDiagnose {
        fun onBtnClicked(buttonType: ImgDiagnoseBtnType, position: Int)
        fun onIsAfterLastListPosition(position: Int, sizeList: Int)
    }

    enum class ImgDiagnoseBtnType {
        BTN_YES_CLICKED,
        BTN_NO_CLICKED
    }

}