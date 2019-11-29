package com.mizzugi.kensiro.app.panri.fragment

import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mizzugi.kensiro.app.panri.R
import com.mizzugi.kensiro.app.panri.fragment.adapter.AdapterDiagnoseRA
import com.mizzugi.kensiro.app.panri.plugin.ItemClickSupport
import com.mizzugi.kensiro.app.panri.plugin.callbacks.OnRequestDialog
import com.mizzugi.kensiro.app.panri.plugin.gone
import com.mizzugi.kensiro.app.panri.plugin.visible
import com.mizzugi.kensiro.app.panri.viewmodel.DiagnoseHelperViewModel

class DiagnoseHelperFragment : Fragment() {

    private lateinit var rootView: RelativeLayout
    private lateinit var mAskLayout: RelativeLayout
    private lateinit var mBtnBottom: LinearLayout

    private lateinit var btnYes: Button
    private lateinit var btnNo: Button
    private lateinit var mDescCiri: TextView

    private lateinit var mListFirstPage: RecyclerView

    private var mDiagnoseHelperViewModel: DiagnoseHelperViewModel? = null

    private var onRequestDialog: OnRequestDialog? = null

    private var diseaseResult: DiseaseResult? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = RelativeLayout(context).apply {
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        setBackgroundColor(Color.parseColor("#af878787"))

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rootView = view as RelativeLayout
        buildLayout(requireContext())

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mDiagnoseHelperViewModel =
            ViewModelProviders.of(this).get(DiagnoseHelperViewModel::class.java)
        mDiagnoseHelperViewModel?.apply {
            finishedLoading.observe(this@DiagnoseHelperFragment, Observer {
                onRequestDialog?.requestDialog(!it)
            })

            viewMode.observe(this@DiagnoseHelperFragment, Observer {
                diseaseResult?.onStateChanged(it)
                when (it) {
                    DiagnoseHelperViewModel.ViewMode.VIEW_MODE_ASK -> {
                        mListFirstPage.gone()
                        mAskLayout.visible()
                    }
                    DiagnoseHelperViewModel.ViewMode.VIEW_MODE_LIST -> {
                        mListFirstPage.visible()
                        mAskLayout.gone()
                        mListFirstPage.scrollToPosition(0)
                    }
                    else -> {
                    }
                }
            })


            modeListData.observe(this@DiagnoseHelperFragment, Observer {
                val point = Point()
                requireActivity().windowManager.defaultDisplay.getSize(point)
                val adapterRecycler = AdapterDiagnoseRA(it, point)
                mListFirstPage.apply {
                    adapter = adapterRecycler
                    visible()
                }
                mAskLayout.gone()
                ItemClickSupport.removeFrom(mListFirstPage)
                ItemClickSupport.addTo(mListFirstPage)?.onItemClickListener =
                    object : ItemClickSupport.OnItemClickListener {
                        override fun onItemClicked(
                            recyclerView: RecyclerView,
                            position: Int,
                            v: View
                        ) {
                            if (onFirst) {
                                onFirst = false
                                onItemCardTouch(position)
                            } else {
                                countPositionData = tempListNum.size
                                onItemCardTouch(position)
                            }
                        }

                    }
            })

            currentAskStr.observe(this@DiagnoseHelperFragment, Observer {
                mDescCiri.text = it
            })

            onDiseaseSelectedStatus.observe(this@DiagnoseHelperFragment, Observer {
                if (it) {
                    var name = ""
                    allDiseaseName?.apply {
                        for (disease in this) {
                            if (disease.num == currKeyId) {
                                name = disease.nama
                                break
                            }
                        }
                    }
                    diseaseResult?.onResult(
                        mListFirstPage,
                        mAskLayout,
                        name,
                        currKeyId,
                        currPercentage
                    )
                    //loadFirst()
                }
            })
            setup()
        }
    }

    private fun buildLayout(context: Context): View {
        createAskLayout(context)
        createListFirstPage(context)
        rootView.addView(mListFirstPage)
        rootView.addView(mAskLayout)
        mListFirstPage.visible()
        mAskLayout.gone()
        return rootView
    }

    private fun createListFirstPage(context: Context) {
        mListFirstPage = RecyclerView(context).apply {
            layoutParams = RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(Color.TRANSPARENT)
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun createAskLayout(context: Context) {
        mAskLayout = LayoutInflater.from(context).inflate(
            R.layout.fragment_dialog_ask,
            rootView,
            false
        ) as RelativeLayout
        mBtnBottom = LayoutInflater.from(context).inflate(
            R.layout.fragment_adapter_btnbottom_diag,
            mAskLayout,
            false
        ) as LinearLayout
        /*val paramsbtn = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            addRule(RelativeLayout.ABOVE, R.id.adapter_id_imgdiag_layout)
        }*/
        btnYes = mBtnBottom.findViewById(R.id.actimgdiagnose_buttonya) as Button
        btnNo = mBtnBottom.findViewById(R.id.actimgdiagnose_buttontidak) as Button
        btnYes.setOnClickListener {
            onBtnClick(OnBtnClickType.BTN_YES)
        }
        btnNo.setOnClickListener {
            onBtnClick(OnBtnClickType.BTN_NO)
        }
        mDescCiri = mAskLayout.findViewById(R.id.actdiagnose_id_contentforask)
        mDescCiri.apply {
            setTextColor(Color.BLACK)
            textSize = 16.5f
        }
        mAskLayout.addView(mBtnBottom)
    }

    private fun onBtnClick(btnClickType: OnBtnClickType) {
        mDiagnoseHelperViewModel?.onBtnPressed(btnClickType)
    }

    override fun onDetach() {
        super.onDetach()
        diseaseResult = null
        onRequestDialog = null
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is OnRequestDialog) {
            onRequestDialog = context
        }

        if (context is DiseaseResult) {
            diseaseResult = context
        }
    }


    fun overrideBackButtonPressed(): Boolean {
        return mDiagnoseHelperViewModel?.pushBack() ?: false
    }

    interface DiseaseResult {

        fun onStateChanged(viewType: DiagnoseHelperViewModel.ViewMode?)
        fun onResult(
            list: RecyclerView,
            mAskLayout: RelativeLayout,
            diseaseName: String?,
            keyId: Int,
            percentage: Double
        )
    }

    enum class OnBtnClickType {
        BTN_YES,
        BTN_NO
    }
}