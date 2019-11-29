package com.mizzugi.kensiro.app.panri.activity

import android.content.pm.ActivityInfo
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.mizzugi.kensiro.app.panri.BaseActivity
import com.mizzugi.kensiro.app.panri.R
import com.mizzugi.kensiro.app.panri.fragment.*
import com.mizzugi.kensiro.app.panri.plugin.LoadingDialogPlugin
import com.mizzugi.kensiro.app.panri.plugin.SwitchActivity
import com.mizzugi.kensiro.app.panri.plugin.callbacks.OnRequestDialog
import com.mizzugi.kensiro.app.panri.viewmodel.ShowResultDiagnoseViewModel
import id.apwdevs.library.text.style.CustomTypefaceSpan
import kotlinx.android.synthetic.main.activity_image_diagnose.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ImageDiagnose : BaseActivity(), OnRequestDialog, ImageDiagnoseFragment.OnFragmentImgDiagnose,
    ShowResultDiagnoseFragment.OnResultCallbacks, OnDiagnoseImageNotSelected.OnBtnDialogClicked {

    private lateinit var farmerFragment: ImageFarmerManualAnimations
    private lateinit var imageDiagnoseFragment: ImageDiagnoseFragment
    private lateinit var resultFragment: ShowResultDiagnoseFragment
    private lateinit var currentDisplayFragmentName: String
    private lateinit var dialogShow: LoadingDialogPlugin
    private lateinit var onNotSelectedFragment: OnDiagnoseImageNotSelected

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_diagnose)
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
                imageDiagnoseFragment = ImageDiagnoseFragment()
                //replaceFragment(this, R.id.content_frame_content, contentFragment)
                add(
                    R.id.content_frame_content,
                    imageDiagnoseFragment,
                    imageDiagnoseFragment.javaClass.name
                )
                show(imageDiagnoseFragment)

                // frame 4
                onNotSelectedFragment = OnDiagnoseImageNotSelected()
                add(
                    R.id.content_frame_content,
                    onNotSelectedFragment,
                    onNotSelectedFragment.javaClass.name
                )
                hide(onNotSelectedFragment)

                // frame 1
                farmerFragment = ImageFarmerManualAnimations.newInstance(2500)
                add(R.id.content_frame_farmer, farmerFragment, farmerFragment.javaClass.name)
                show(farmerFragment)

                commit()
                currentDisplayFragmentName = imageDiagnoseFragment.javaClass.name

                GlobalScope.launch {
                    while (!farmerFragment.isFinished) delay(200)
                    farmerFragment.update(getString(R.string.actdiagnose_string_speechfarmer_img_1))
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
                    imageDiagnoseFragment = getFragment(
                        it,
                        ImageDiagnoseFragment::class.java.name
                    ) as ImageDiagnoseFragment? ?: ImageDiagnoseFragment().apply {
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

                    onNotSelectedFragment = getFragment(
                        it,
                        OnDiagnoseImageNotSelected::class.java.name
                    ) as OnDiagnoseImageNotSelected? ?: OnDiagnoseImageNotSelected().apply {
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
                        beginTransaction().add(
                            R.id.container_frame_farmer,
                            this,
                            this.javaClass.name
                        )
                    }

                    beginTransaction().apply {
                        show(farmerFragment)
                        when (currentDisplayFragmentName) {
                            imageDiagnoseFragment.javaClass.name -> {
                                hide(resultFragment)
                                show(imageDiagnoseFragment)
                            }
                            resultFragment.javaClass.name -> {
                                hide(imageDiagnoseFragment)
                                show(resultFragment)
                            }
                            onNotSelectedFragment.javaClass.name -> {
                                hide(imageDiagnoseFragment)
                                show(resultFragment)
                                show(onNotSelectedFragment)
                            }
                        }
                    }.commit()
                }
            }
        }
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

    override fun requestDialog(isShowed: Boolean) {
        if (isShowed)
            dialogShow.showDialog()
        else
            dialogShow.stopDialog()
    }

    override fun onBtnClicked(buttonType: ImageDiagnoseFragment.ImgDiagnoseBtnType, position: Int) {
        when (buttonType) {
            ImageDiagnoseFragment.ImgDiagnoseBtnType.BTN_YES_CLICKED -> {
                supportFragmentManager.beginTransaction().apply {
                    hide(imageDiagnoseFragment)
                    show(resultFragment)
                    resultFragment.show(position)
                    currentDisplayFragmentName = resultFragment.javaClass.name
                }.commit()
                farmerFragment.update(getString(R.string.actdiagnose_string_speechfarmer_img_2))
            }
            ImageDiagnoseFragment.ImgDiagnoseBtnType.BTN_NO_CLICKED -> {
                farmerFragment.update(getString(R.string.actdiagnose_string_speechfarmer_img_1))
            }
        }
    }

    override fun onIsAfterLastListPosition(position: Int, sizeList: Int) {
        supportFragmentManager.beginTransaction().apply {
            show(onNotSelectedFragment)
            currentDisplayFragmentName = onNotSelectedFragment.javaClass.name
        }.commit()
    }

    override fun onFinal(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            hide(resultFragment)
            show(imageDiagnoseFragment)
            currentDisplayFragmentName = imageDiagnoseFragment.javaClass.name
        }.commit()
        imageDiagnoseFragment.resetCounter()
        farmerFragment.update(getString(R.string.actdiagnose_string_speechfarmer_img_1))
    }

    override fun onCaseChanged(currentCase: ShowResultDiagnoseViewModel.CurrentBtnCondition?) {
        when (currentCase) {
            ShowResultDiagnoseViewModel.CurrentBtnCondition.CASE_1 -> {
                farmerFragment.update(getText(R.string.actdiagnose_string_speechfarmer_2).toString())
            }
            ShowResultDiagnoseViewModel.CurrentBtnCondition.CASE_2 -> {
                farmerFragment.update("INI ADALAH CARA PENANGGULANGAN DARI HAMA ATAU PENYAKIT YANG MENYERANG PADI ANDA")
            }
            null -> return
        }
    }

    override fun onClicked(btnPosition: ImageDiagnoseFragment.ImgDiagnoseBtnType) {
        when (btnPosition) {
            ImageDiagnoseFragment.ImgDiagnoseBtnType.BTN_YES_CLICKED -> {
                supportFragmentManager.beginTransaction().apply {
                    hide(onNotSelectedFragment)
                    show(imageDiagnoseFragment)
                    currentDisplayFragmentName = imageDiagnoseFragment.javaClass.name
                }.commit()
                imageDiagnoseFragment.resetCounter()
                farmerFragment.update(getText(R.string.actdiagnose_string_speechfarmer_img_1).toString())
            }
            ImageDiagnoseFragment.ImgDiagnoseBtnType.BTN_NO_CLICKED -> {
                SwitchActivity.switchToMain(this)
            }
        }
    }


    private fun createResultFragment(): ShowResultDiagnoseFragment =
        ShowResultDiagnoseFragment.newInstance(
            ShowResultDiagnoseViewModel.BottomCardTextData(
                getString(R.string.actdiagnose_string_klikcaramenanggulangi),
                getString(R.string.actdiagnose_string_klikbalikdiagnosa)
            )
        )

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        supportFragmentManager.putFragment(outState, resultFragment.javaClass.name, resultFragment)
        supportFragmentManager.putFragment(
            outState,
            imageDiagnoseFragment.javaClass.name,
            imageDiagnoseFragment
        )
        supportFragmentManager.putFragment(
            outState,
            onNotSelectedFragment.javaClass.name,
            onNotSelectedFragment
        )
        supportFragmentManager.putFragment(outState, farmerFragment.javaClass.name, farmerFragment)
        outState.putString(KEY_CURRENT_DISPLAYED_FRAGMENT, currentDisplayFragmentName)
    }

    override fun onBackPressed() {
        if (resultFragment.isVisible) {
            if (!resultFragment.overrideBackKeyPressed())
                supportFragmentManager.beginTransaction().apply {
                    hide(resultFragment)
                    show(imageDiagnoseFragment)
                    currentDisplayFragmentName = imageDiagnoseFragment.javaClass.name
                }.commit()
            farmerFragment.update(getText(R.string.actdiagnose_string_speechfarmer_img_1).toString())
            return
        } else if (imageDiagnoseFragment.isVisible) {
            if (onNotSelectedFragment.isVisible) return
            else if (imageDiagnoseFragment.overrideBackButtonPressed()) return
        }
        super.onBackPressed()
    }

    companion object {
        private const val KEY_CURRENT_DISPLAYED_FRAGMENT = "CURRENT_DISPLAYED"
    }
}
