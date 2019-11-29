package com.mizzugi.kensiro.app.panri.activity

import android.content.pm.ActivityInfo
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.util.Log
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.mizzugi.kensiro.app.panri.BaseActivity
import com.mizzugi.kensiro.app.panri.R
import com.mizzugi.kensiro.app.panri.fragment.DiagnoseHelperFragment
import com.mizzugi.kensiro.app.panri.fragment.ImageFarmerManualAnimations
import com.mizzugi.kensiro.app.panri.fragment.ShowResultDiagnoseFragment
import com.mizzugi.kensiro.app.panri.plugin.LoadingDialogPlugin
import com.mizzugi.kensiro.app.panri.plugin.callbacks.OnRequestDialog
import com.mizzugi.kensiro.app.panri.viewmodel.DiagnoseHelperViewModel
import com.mizzugi.kensiro.app.panri.viewmodel.ShowResultDiagnoseViewModel
import id.apwdevs.library.text.style.CustomTypefaceSpan
import kotlinx.android.synthetic.main.activity_start_diagnose.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class StartDiagnose : BaseActivity(), OnRequestDialog, DiagnoseHelperFragment.DiseaseResult,
    ShowResultDiagnoseFragment.OnResultCallbacks {

    private lateinit var farmerFragment: ImageFarmerManualAnimations
    private lateinit var contentFragment: DiagnoseHelperFragment
    private lateinit var resultFragment: ShowResultDiagnoseFragment
    private lateinit var currentDisplayFragmentName: String

    private lateinit var dialogShow: LoadingDialogPlugin

    private var percentage: Int = 0
    private var diseaseName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_diagnose)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setMyActionBar()
        display(savedInstanceState)
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

    private fun display(savedState: Bundle?) {
        dialogShow = LoadingDialogPlugin(this)
        dialogShow.buildLoadingLayout()
        if (savedState == null) {

            supportFragmentManager.beginTransaction().apply {
                // frame 3
                resultFragment = createResultFragment()
                //replaceFragment(this, R.id.content_frame_content, resultFragment)
                add(R.id.content_frame_content, resultFragment, resultFragment.javaClass.name)
                hide(resultFragment)

                // frame 2
                contentFragment = DiagnoseHelperFragment()
                //replaceFragment(this, R.id.content_frame_content, contentFragment)
                add(R.id.content_frame_content, contentFragment, contentFragment.javaClass.name)
                show(contentFragment)

                // frame 1
                farmerFragment = ImageFarmerManualAnimations.newInstance(2500)
                add(R.id.content_frame_farmer, farmerFragment, farmerFragment.javaClass.name)
                show(farmerFragment)
                commit()
                currentDisplayFragmentName = contentFragment.javaClass.name

                GlobalScope.launch {
                    while (!farmerFragment.isFinished) delay(200)
                    farmerFragment.update(getText(R.string.actdiagnose_string_speechfarmer_1).toString())
                }
            }
        } else {
            savedState.let {
                currentDisplayFragmentName = it.getString(
                    KEY_CURRENT_DISPLAYED_FRAGMENT,
                    DiagnoseHelperFragment::class.java.name
                )

                // get all fragment from bundle
                supportFragmentManager.apply {
                    contentFragment = getFragment(
                        it,
                        DiagnoseHelperFragment::class.java.name
                    ) as DiagnoseHelperFragment? ?: DiagnoseHelperFragment().apply {
                        beginTransaction().add(
                            R.id.content_frame_content,
                            this,
                            this.javaClass.name
                        )
                    }

                    resultFragment = getFragment(
                        it,
                        ShowResultDiagnoseFragment::class.java.name
                    ) as ShowResultDiagnoseFragment? ?: createResultFragment().apply {
                        beginTransaction().add(
                            R.id.content_frame_content,
                            this,
                            this.javaClass.name
                        )
                    }

                    farmerFragment = getFragment(
                        it,
                        ImageFarmerManualAnimations::class.java.name
                    ) as ImageFarmerManualAnimations? ?: ImageFarmerManualAnimations.newInstance(
                        2500
                    ).apply {
                        beginTransaction().add(R.id.content_frame_farmer, this, this.javaClass.name)
                    }

                    beginTransaction().apply {
                        show(farmerFragment)
                        when (currentDisplayFragmentName) {
                            contentFragment.javaClass.name -> {
                                hide(resultFragment)
                                show(contentFragment)
                            }
                            resultFragment.javaClass.name -> {
                                hide(contentFragment)
                                show(resultFragment)
                            }
                        }
                    }.commit()
                }
            }
        }
    }

    /*private fun updateFarmer(s: String){
        GlobalScope.launch {
            delay(100)
            farmerFragment.update(s)
        }
    }*/


    override fun onFinal(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            resultFragment.overrideBackKeyPressed()
            hide(resultFragment)
            show(contentFragment)
            commit()
            currentDisplayFragmentName = contentFragment.javaClass.name
            GlobalScope.launch {
                delay(100)
                farmerFragment.update(getText(R.string.actdiagnose_string_speechfarmer_1).toString())
            }
            //updateFarmer(getText(R.string.actdiagnose_string_speechfarmer_1).toString())
        }
    }

    override fun onCaseChanged(currentCase: ShowResultDiagnoseViewModel.CurrentBtnCondition?) {
        //if(resultFragment.isVisible && !contentFragment.isVisible) {
        //Log.e("KKKKK", "onCaseChanged()")
        when (currentCase) {
            ShowResultDiagnoseViewModel.CurrentBtnCondition.CASE_1 -> {
                //farmerFragment.update(getText(R.string.actinfo_string_speechfarmer_2).toString())
                farmerFragment.update(
                    String.format(
                        getString(R.string.actdiagnose_string_speechfarmer_3),
                        diseaseName,
                        percentage
                    )
                )
                /*updateFarmer(String.format(
                    getString(R.string.actdiagnose_string_speechfarmer_3),
                    diseaseName,
                    percentage
                ))*/
            }
            ShowResultDiagnoseViewModel.CurrentBtnCondition.CASE_2 -> {
                farmerFragment.update("INI ADALAH CARA PENANGGULANGAN DARI HAMA ATAU PENYAKIT YANG MENYERANG PADI ANDA")
                //updateFarmer("INI ADALAH CARA PENANGGULANGAN DARI HAMA ATAU PENYAKIT YANG MENYERANG PADI ANDA")
            }
            null -> return
        }
        //}
    }

    override fun requestDialog(isShowed: Boolean) {
        if (isShowed)
            dialogShow.showDialog()
        else
            dialogShow.stopDialog()
    }

    override fun onStateChanged(viewType: DiagnoseHelperViewModel.ViewMode?) {
        //if(contentFragment.isVisible && !resultFragment.isVisible) {
        Log.e("KKKKK", "onStateChanged()")
        when (viewType) {
            DiagnoseHelperViewModel.ViewMode.VIEW_MODE_ASK -> {
                farmerFragment.update(getString(R.string.actdiagnose_string_speechfarmer_2))
                //updateFarmer(getString(R.string.actdiagnose_string_speechfarmer_2))
            }
            DiagnoseHelperViewModel.ViewMode.VIEW_MODE_LIST -> {
                farmerFragment.update(getString(R.string.actdiagnose_string_speechfarmer_1))
                //updateFarmer(getString(R.string.actdiagnose_string_speechfarmer_1))
            }
            null -> {
            }
        }
        //}
    }

    override fun onResult(
        list: RecyclerView,
        mAskLayout: RelativeLayout,
        diseaseName: String?,
        keyId: Int,
        percentage: Double
    ) {
        //replaceFragment(this, R.id.content_frame_content, resultFragment)
        //if(contentFragment.isVisible && !resultFragment.isVisible) {

        //Log.e("KKKKK", "onResult()")
        this.diseaseName = diseaseName
        this.percentage = percentage.roundToInt()
        supportFragmentManager.beginTransaction().apply {
            hide(contentFragment)
            show(resultFragment)
            commit()
            currentDisplayFragmentName = resultFragment.javaClass.name
        }

        GlobalScope.launch {
            delay(150)
            farmerFragment.update(
                String.format(
                    getString(R.string.actdiagnose_string_speechfarmer_3), diseaseName ?: "",
                    percentage.roundToInt()
                )
            )
        }

        /*updateFarmer(String.format(
            getString(R.string.actdiagnose_string_speechfarmer_3), diseaseName ?: "",
            percentage.roundToInt()
        ))*/
        GlobalScope.launch {
            while (!resultFragment.prepareFinished) delay(100)
            resultFragment.show(keyId)
        }
        //}
    }

    override fun onBackPressed() {
        if (resultFragment.isVisible) {
            if (!resultFragment.overrideBackKeyPressed())
            //replaceFragment(this, R.id.content_frame_content, contentFragment)
                supportFragmentManager.beginTransaction().apply {
                    hide(resultFragment)
                    show(contentFragment)
                    commit()
                    currentDisplayFragmentName = contentFragment.javaClass.name
                }
            farmerFragment.update(getText(R.string.actdiagnose_string_speechfarmer_1).toString())
            //updateFarmer(getText(R.string.actdiagnose_string_speechfarmer_1).toString())
            return
        } else if (contentFragment.isVisible && contentFragment.overrideBackButtonPressed()) {
            return
        }
        super.onBackPressed()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        supportFragmentManager.putFragment(outState, resultFragment.javaClass.name, resultFragment)
        supportFragmentManager.putFragment(
            outState,
            contentFragment.javaClass.name,
            contentFragment
        )
        outState.putString(KEY_CURRENT_DISPLAYED_FRAGMENT, currentDisplayFragmentName)
        outState.putInt(KEY_CURRENT_PERCENTAGE, percentage)
        outState.putString(KEY_CURRENT_DISEASE_NAME, diseaseName)
    }

    private fun createResultFragment(): ShowResultDiagnoseFragment =
        ShowResultDiagnoseFragment.newInstance(
            ShowResultDiagnoseViewModel.BottomCardTextData(
                getString(R.string.actdiagnose_string_klikcaramenanggulangi),
                getString(R.string.actdiagnose_string_klikbalikdiagnosa)
            )
        )

    companion object {
        private const val KEY_CURRENT_DISPLAYED_FRAGMENT = "CURRENT_DISPLAYED"
        private const val KEY_CURRENT_PERCENTAGE = "KEY_CURRENT_PERCENTAGE"
        private const val KEY_CURRENT_DISEASE_NAME = "KEY_CURRENT_DISEASE_NAME"
    }
}
