package com.mizzugi.kensiro.app.panri.activity

import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Point
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.transition.Fade
import androidx.transition.TransitionManager
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.firebase.FirebaseApp
import com.mizzugi.kensiro.app.panri.R
import com.mizzugi.kensiro.app.panri.plugin.PublicContract
import com.mizzugi.kensiro.app.panri.plugin.invisible
import com.mizzugi.kensiro.app.panri.plugin.visible
import com.mizzugi.kensiro.app.panri.viewmodel.SplashScreenViewModel
import com.mizzugi.kensiro.app.panri.workers.CheckDatabaseWorker
import kotlinx.android.synthetic.main.activity_splash_screen.*

class SplashScreenActivity : AppCompatActivity() {
    private var appCondition = 0
    private var dbCondition = 0
    private lateinit var splashScreenViewModel: SplashScreenViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        FirebaseApp.initializeApp(applicationContext)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        initializingViews()
        tintingStatusBar()
        configViewModel()
    }

    private fun configViewModel() {
        splashScreenViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(SplashScreenViewModel::class.java)

        splashScreenViewModel.apply {
            statusTextLoading.observe(this@SplashScreenActivity, Observer {
                actsplash_id_txtsplash_indicator.text = it
            })

            appVersion.observe(this@SplashScreenActivity, Observer {
                appCondition = it
            })

            dbVersion.observe(this@SplashScreenActivity, Observer {
                dbCondition = it
            })

            finishedLoading.observe(this@SplashScreenActivity, Observer {
                if (it) {
                    WorkManager.getInstance(applicationContext).enqueue(
                        OneTimeWorkRequestBuilder<CheckDatabaseWorker>().apply {
                            setConstraints(Constraints.Builder().apply {
                                setRequiredNetworkType(NetworkType.CONNECTED)
                                setRequiresCharging(false)
                                setRequiresBatteryNotLow(true)
                                setRequiresDeviceIdle(false)
                            }.build())
                        }.build()
                    )
                    //WorkManager.getInstance(applicationContext).
                    TransitionManager.beginDelayedTransition(
                        actsplash_id_bawah_layout,
                        Fade(Fade.IN).apply {
                            duration = 1200
                        })
                    actsplash_id_linear_indicator.invisible()

                    if (appCondition == PublicContract.APP_IS_FIRST_USAGE)
                        actsplash_id_btnlanjut.visible()
                    else {
                        finish()
                        startActivity(
                            Intent(this@SplashScreenActivity, MainActivity::class.java).apply {
                                putExtras(Bundle().apply {
                                    putInt(PublicContract.APP_CONDITION_KEY, appCondition)
                                })
                            }
                        )
                        overridePendingTransition(
                            R.anim.fade_in,
                            R.anim.fade_out
                        )
                    }
                }
            })

            run()
        }

    }

    private fun tintingStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(
                this@SplashScreenActivity,
                R.color.color_status_white_dark
            )
        }
    }

    private fun initializingViews() {
        // txt judul
        actsplash_id_txtjudul.apply {
            setTypeface(
                Typeface.createFromAsset(assets, "Gecko_PersonalUseOnly.ttf"),
                Typeface.BOLD
            )
        }

        // btn lanjut
        actsplash_id_btnlanjut.apply {

            //calculating size btn
            val point = Point()
            windowManager.defaultDisplay.getSize(point)
            /// edit this size for max / min the size of btn
            val marginBtnFactor = 8
            minimumWidth = point.x - ((point.x / marginBtnFactor) * 2)
            setTypeface(Typeface.createFromAsset(assets, "RifficFree-Bold.ttf"), Typeface.BOLD)

            setOnClickListener {
                finish()
                startActivity(
                    Intent(this@SplashScreenActivity, TutorialFirstUseActivity::class.java).also {
                        it.putExtras(
                            Bundle().apply {
                                putInt(PublicContract.APP_CONDITION_KEY, appCondition)
                                putInt(PublicContract.DB_CONDITION_KEY, dbCondition)
                            }
                        )
                    } // edited later
                )
                overridePendingTransition(
                    R.anim.fade_in,
                    R.anim.fade_out
                )
            }

            actsplash_id_txtsplash_indicator.text = "Mempersiapkan data..."
        }
    }

}
