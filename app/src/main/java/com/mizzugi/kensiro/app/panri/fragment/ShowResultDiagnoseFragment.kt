package com.mizzugi.kensiro.app.panri.fragment

import android.content.Context
import android.graphics.Point
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.mizzugi.kensiro.app.panri.R
import com.mizzugi.kensiro.app.panri.adapter.ImageGridViewAdapter
import com.mizzugi.kensiro.app.panri.plugin.callbacks.OnRequestDialog
import com.mizzugi.kensiro.app.panri.plugin.gone
import com.mizzugi.kensiro.app.panri.plugin.visible
import com.mizzugi.kensiro.app.panri.viewmodel.ShowResultDiagnoseViewModel
import java.io.File
import kotlin.math.roundToInt

class ShowResultDiagnoseFragment private constructor() : Fragment() {

    private lateinit var titleText: TextView
    private lateinit var latinText: TextView
    private lateinit var mContentScroll: ScrollView
    private lateinit var mDescContent: LinearLayout
    private lateinit var mResolveContent: LinearLayout


    private lateinit var baseGeneralLayout: LinearLayout
    private lateinit var baseHowToResolveLayout: CardView
    private lateinit var baseGejalaLayout: CardView
    private lateinit var baseTextCardBottom: TextView

    private lateinit var mGridView: LinearLayout
    private val mText: HashMap<ShowResultDiagnoseViewModel.WebContentType, TextView> = hashMapOf()

    private lateinit var klikBawah: CardView
    var onRequestDialog: OnRequestDialog? = null
    private var mShowResultDiagnoseViewModel: ShowResultDiagnoseViewModel? = null
    private var mImageViewDisease: ImageGridViewAdapter? = null

    private var onResultCallbacks: OnResultCallbacks? = null
    var prepareFinished = false

    var currentBtnCase: ShowResultDiagnoseViewModel.CurrentBtnCondition? = null
        set(value) {

            Log.e("Obsssssss2", "is VM null $mShowResultDiagnoseViewModel")
            mShowResultDiagnoseViewModel?.setCase(value)
            field = value
        }
        get() = mShowResultDiagnoseViewModel?.currentBtnCount?.value

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_resultdiagnose, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.apply {
            // gets all views
            titleText = findViewById(R.id.actdiagnose_id_judulpenyakit)
            latinText = findViewById(R.id.actdiagnose_id_namalatin)

            mContentScroll = findViewById(R.id.adapter_id_scrollresultdiagnose)
            mGridView = findViewById(R.id.actgallery_id_gridimage)
            // prepare ScrollView
            mDescContent = findViewById(R.id.actdiagnose_id_results1)
            mResolveContent = findViewById(R.id.actdiagnose_id_results2)

            // prepare webView
            baseGeneralLayout = findViewById(R.id.actdiagnose_id_umumcard_baselayout)
            baseGejalaLayout = findViewById(R.id.actdiagnose_id_gejalacard)
            baseHowToResolveLayout = findViewById(R.id.actdiagnose_id_howtocard)

            klikBawah = findViewById(R.id.actdiagnose_id_klikbawah)
            klikBawah.apply {
                baseTextCardBottom = (getChildAt(0) as TextView).apply {
                    typeface = Typeface.createFromAsset(context.assets, "Comic_Sans_MS3.ttf")
                }
                setOnClickListener {
                    // later........
                    when (mShowResultDiagnoseViewModel?.currentBtnCount?.value) {
                        ShowResultDiagnoseViewModel.CurrentBtnCondition.CASE_1 -> {
                            mShowResultDiagnoseViewModel?.increment()
                        }
                        ShowResultDiagnoseViewModel.CurrentBtnCondition.CASE_2 -> {
                            onResultCallbacks?.onFinal(this@ShowResultDiagnoseFragment)
                        }
                        null -> return@setOnClickListener
                    }

                }
            }

            /// sets into Comic SAns
            titleText.apply {
                setTypeface(
                    Typeface.createFromAsset(context.assets, "Comic_Sans_MS3.ttf"),
                    Typeface.BOLD
                )
                gravity = Gravity.CENTER
            }
            latinText.apply {
                setTypeface(
                    Typeface.createFromAsset(context.assets, "Comic_Sans_MS3.ttf"),
                    Typeface.ITALIC
                )
                gravity = Gravity.CENTER
            }
            /// sets the visibility
            mResolveContent.gone()
            mDescContent.visible()
            mText.apply {
                put(
                    ShowResultDiagnoseViewModel.WebContentType.UMUM_TYPE,
                    findViewById(R.id.actdiagnose_id_umum)
                )
                put(
                    ShowResultDiagnoseViewModel.WebContentType.GEJALA_TYPE,
                    findViewById(R.id.actdiagnose_id_gejala)
                )
                put(
                    ShowResultDiagnoseViewModel.WebContentType.RESOLVE_TYPE,
                    findViewById(R.id.actdiagnose_id_caraatasi)
                )
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mShowResultDiagnoseViewModel =
            ViewModelProviders.of(this).get(ShowResultDiagnoseViewModel::class.java)
        mShowResultDiagnoseViewModel?.apply {
            loadedTitleText.observe(this@ShowResultDiagnoseFragment, Observer {
                titleText.text = it
            })
            loadedLatinText.observe(this@ShowResultDiagnoseFragment, Observer {
                latinText.text = it
            })

            loadedImagePath.observe(this@ShowResultDiagnoseFragment, Observer {
                loadImage(it)
            })

            finishedLoading.observe(this@ShowResultDiagnoseFragment, Observer {
                onRequestDialog?.requestDialog(!it)
            })

            loadedUmumPath.observe(this@ShowResultDiagnoseFragment, Observer {
                mText[ShowResultDiagnoseViewModel.WebContentType.UMUM_TYPE]?.text = it
            })

            loadedGejalaPath.observe(this@ShowResultDiagnoseFragment, Observer {
                mText[ShowResultDiagnoseViewModel.WebContentType.GEJALA_TYPE]?.text = it
            })

            loadedResolvePath.observe(this@ShowResultDiagnoseFragment, Observer {
                mText[ShowResultDiagnoseViewModel.WebContentType.RESOLVE_TYPE]?.text = it
            })

            currentBtnCount.observe(this@ShowResultDiagnoseFragment, Observer {
                when (it) {
                    ShowResultDiagnoseViewModel.CurrentBtnCondition.CASE_1 -> {
                        mDescContent.visible()
                        mResolveContent.gone()
                        baseTextCardBottom.text = bottomCardTextData?.btnCase1
                    }
                    ShowResultDiagnoseViewModel.CurrentBtnCondition.CASE_2 -> {
                        mResolveContent.visible()
                        mDescContent.gone()
                        baseTextCardBottom.text = bottomCardTextData?.btnCase2
                    }
                    else -> return@Observer
                }
                mContentScroll.pageScroll(0)
                onResultCallbacks?.onCaseChanged(it)
            })
            requireArguments().apply {
                bottomCardTextData = getParcelable(BOTTOM_CARD_DATA_KEY)
            }
        }
        prepareFinished = true
    }

    private fun loadImage(imgList: List<String>?) {
        if (imgList.isNullOrEmpty()) return
        val p = Point()
        requireActivity().windowManager.defaultDisplay.getSize(p)
        if (mImageViewDisease == null)
            mImageViewDisease = ImageGridViewAdapter(
                requireContext(),
                p,
                File(requireContext().filesDir, "data/images/list"),
                mGridView
            )
        mImageViewDisease?.apply {
            columnCount = 2
            mGridView.removeAllViews()
            setListLocationFileImages(imgList, "show_diagnose")
            val dimen =
                requireContext().resources.getDimension(R.dimen.margin_img_penyakit).roundToInt()
            setMargin(
                0,
                dimen,
                dimen,
                dimen,
                dimen,
                requireContext().resources.getDimension(R.dimen.content_imggrid_padding).roundToInt() + 2
            )
            buildAndShow()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnResultCallbacks)
            onResultCallbacks = context
        if (context is OnRequestDialog)
            onRequestDialog = context
    }

    override fun onDetach() {
        super.onDetach()
        onResultCallbacks = null
        onRequestDialog = null
    }

    fun overrideBackKeyPressed(): Boolean = mShowResultDiagnoseViewModel?.decrement() ?: false

    fun show(keyId: Int) {
        mShowResultDiagnoseViewModel?.load(keyId)
    }

    interface OnResultCallbacks {
        fun onFinal(fragment: Fragment)
        fun onCaseChanged(currentCase: ShowResultDiagnoseViewModel.CurrentBtnCondition?)
    }

    companion object {

        private const val BOTTOM_CARD_DATA_KEY: String = "BOTTOM_CARD_DATA"

        @JvmStatic
        fun newInstance(stringCardData: ShowResultDiagnoseViewModel.BottomCardTextData): ShowResultDiagnoseFragment =
            ShowResultDiagnoseFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(BOTTOM_CARD_DATA_KEY, stringCardData)
                }
            }
    }
}