package com.mizzugi.kensiro.app.panri.activity

import android.content.pm.ActivityInfo
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.mizzugi.kensiro.app.panri.BaseActivity
import com.mizzugi.kensiro.app.panri.R
import com.mizzugi.kensiro.app.panri.fragment.DiagnoseHelperFragment
import com.mizzugi.kensiro.app.panri.fragment.ImageFarmerManualAnimations
import com.mizzugi.kensiro.app.panri.fragment.ShowListDiseaseFragment
import com.mizzugi.kensiro.app.panri.fragment.ShowResultDiagnoseFragment
import com.mizzugi.kensiro.app.panri.plugin.ItemClickSupport
import com.mizzugi.kensiro.app.panri.plugin.LoadingDialogPlugin
import com.mizzugi.kensiro.app.panri.plugin.callbacks.OnRequestDialog
import com.mizzugi.kensiro.app.panri.viewmodel.ShowResultDiagnoseViewModel
import id.apwdevs.library.text.style.CustomTypefaceSpan
import kotlinx.android.synthetic.main.activity_disease_info.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DiseaseInfo : BaseActivity(), OnRequestDialog, ItemClickSupport.OnItemClickListener,
    ShowResultDiagnoseFragment.OnResultCallbacks {

    private lateinit var farmerFragment: ImageFarmerManualAnimations
    private lateinit var listDiseaseFragment: ShowListDiseaseFragment
    private lateinit var resultFragment: ShowResultDiagnoseFragment
    private lateinit var currentDisplayFragmentName: String
    private lateinit var dialogShow: LoadingDialogPlugin

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_disease_info)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setMyActionBar()
        display(savedInstanceState)
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
                listDiseaseFragment = ShowListDiseaseFragment()
                //replaceFragment(this, R.id.content_frame_content, contentFragment)
                add(
                    R.id.content_frame_content,
                    listDiseaseFragment,
                    listDiseaseFragment.javaClass.name
                )
                show(listDiseaseFragment)

                // frame 1
                farmerFragment = ImageFarmerManualAnimations.newInstance(2500)
                add(R.id.content_frame_farmer, farmerFragment, farmerFragment.javaClass.name)
                show(farmerFragment)
                commit()
                currentDisplayFragmentName = listDiseaseFragment.javaClass.name
                GlobalScope.launch {
                    while (!farmerFragment.isFinished) delay(200)
                    updateText(getText(R.string.actinfo_string_speechfarmer_1).toString())
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
                    listDiseaseFragment = getFragment(
                        it,
                        ShowListDiseaseFragment::class.java.name
                    ) as ShowListDiseaseFragment? ?: ShowListDiseaseFragment().apply {
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
                    ) as ImageFarmerManualAnimations? ?: createFarmerAnimFragment().apply {
                        beginTransaction().add(R.id.content_frame_farmer, this, this.javaClass.name)
                    }
                    beginTransaction().apply {
                        show(farmerFragment)
                        when (currentDisplayFragmentName) {
                            listDiseaseFragment.javaClass.name -> {
                                hide(resultFragment)
                                show(listDiseaseFragment)
                            }
                            resultFragment.javaClass.name -> {
                                hide(listDiseaseFragment)
                                show(resultFragment)
                            }
                        }

                    }.commit()
                }
            }
        }
    }

    private fun updateText(s: String) {
        farmerFragment.update(s)
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        supportFragmentManager.putFragment(outState, resultFragment.javaClass.name, resultFragment)
        supportFragmentManager.putFragment(
            outState,
            listDiseaseFragment.javaClass.name,
            listDiseaseFragment
        )
        supportFragmentManager.putFragment(outState, farmerFragment.javaClass.name, farmerFragment)
        outState.putString(KEY_CURRENT_DISPLAYED_FRAGMENT, currentDisplayFragmentName)
    }

    private fun createResultFragment(): ShowResultDiagnoseFragment =
        ShowResultDiagnoseFragment.newInstance(
            ShowResultDiagnoseViewModel.BottomCardTextData(
                getString(R.string.actdiagnose_string_klikcaramenanggulangi),
                "KEMBALI KE MENU AWAL"
            )
        )

    private fun createFarmerAnimFragment(): ImageFarmerManualAnimations =
        ImageFarmerManualAnimations.newInstance(
            2500
        )


    override fun requestDialog(isShowed: Boolean) {
        if (isShowed)
            dialogShow.showDialog()
        else
            dialogShow.stopDialog()
    }

    override fun onItemClicked(recyclerView: RecyclerView, position: Int, v: View) {
        supportFragmentManager.beginTransaction().apply {
            hide(listDiseaseFragment)
            show(resultFragment)
            resultFragment.show(position + 1)
            currentDisplayFragmentName = resultFragment.javaClass.name
        }.commit()
        updateText(getText(R.string.actinfo_string_speechfarmer_2).toString())
    }

    override fun onFinal(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            resultFragment.overrideBackKeyPressed()
            hide(resultFragment)
            show(listDiseaseFragment)
            currentDisplayFragmentName = listDiseaseFragment.javaClass.name
        }.commit()
        GlobalScope.launch {
            delay(100)
            updateText(getText(R.string.actinfo_string_speechfarmer_1).toString())
        }
    }

    override fun onCaseChanged(currentCase: ShowResultDiagnoseViewModel.CurrentBtnCondition?) {
        when (currentCase) {
            ShowResultDiagnoseViewModel.CurrentBtnCondition.CASE_1 -> {
                updateText(getText(R.string.actinfo_string_speechfarmer_2).toString())
            }
            ShowResultDiagnoseViewModel.CurrentBtnCondition.CASE_2 -> {
                updateText("INI ADALAH CARA PENANGGULANGAN DARI HAMA ATAU PENYAKIT YANG MENYERANG PADI ANDA")
            }
            null -> return
        }
    }

    override fun onBackPressed() {
        if (resultFragment.isVisible) {
            if (!resultFragment.overrideBackKeyPressed()) {
                supportFragmentManager.beginTransaction().apply {
                    hide(resultFragment)
                    show(listDiseaseFragment)
                    currentDisplayFragmentName = listDiseaseFragment.javaClass.name
                }.commit()
                updateText(getText(R.string.actinfo_string_speechfarmer_1).toString())
            }
            return
        }
        super.onBackPressed()
    }

    companion object {
        private const val KEY_CURRENT_DISPLAYED_FRAGMENT = "CURRENT_DISPLAYED"
    }
}
