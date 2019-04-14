/**
 * Copyright @2019 Alexzander Purwoko Widiantoro <purwoko908@gmail.com> HOPEProjects
 *
 * This file is a part of PANRI Android Application
 * Except @author, you must not be able to modify, copying, change this file, because its closed source programs
 * This file is patented by any copyright law
 */

package id.kenshiro.app.panri.activity.splashScreen

import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Point
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.transition.Fade
import androidx.transition.TransitionManager
import com.crashlytics.android.Crashlytics
import id.kenshiro.app.panri.MainActivity
import id.kenshiro.app.panri.R
import id.kenshiro.app.panri.activity.splashScreen.model.SplashScreenModel
import id.kenshiro.app.panri.activity.splashScreen.presenter.SplashScreenPresenter
import id.kenshiro.app.panri.activity.splashScreen.util.DisplayMsg
import id.kenshiro.app.panri.activity.tutorialFirstUse.TutorialFirstUse
import id.kenshiro.app.panri.params.PublicConfig
import id.kenshiro.app.panri.params.SplashScreenParams
import id.kenshiro.app.panri.utility.disable
import id.kenshiro.app.panri.utility.enable
import id.kenshiro.app.panri.utility.gone
import id.kenshiro.app.panri.utility.visible
import io.fabric.sdk.android.Fabric
import kotlinx.android.synthetic.main.actsplash_main.*

class SplashScreenActivity : AppCompatActivity(), SplashScreenModel {

    private lateinit var btnLanjut: Button
    private lateinit var txtStatus: TextView
    private lateinit var linearSwitch: LinearLayout
    private lateinit var mSplashScreenPresenter: SplashScreenPresenter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actsplash_main)
        // initialize Fabrics
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        val fabric = Fabric.Builder(this).kits(Crashlytics()).debuggable(true).build()
        Fabric.with(fabric)

        mSplashScreenPresenter = SplashScreenPresenter(this, this)
        mSplashScreenPresenter.runAllCommands()
    }

    override fun onLoadFailed(ex: Throwable) {
        Crashlytics.setString(SplashScreenParams.LOADING_FAILED, "SplashScreenError : $ex")
        Crashlytics.logException(ex)
        DisplayMsg.displayOnLoadError(this, ex)
    }

    override fun onUpdatedTextProgress(text: String) {
        txtStatus.text = text
    }

    override fun onLoadingFinished(isFirstUsage: Boolean) {
        if (isFirstUsage) {
            val fadeIn = Fade(Fade.IN)
            fadeIn.duration = SplashScreenParams.DURATION_ANIMATE_BTN_NEXT
            TransitionManager.beginDelayedTransition(actsplash_id_bawah_layout, fadeIn)
            linearSwitch.gone()
            btnLanjut.visible()
            btnLanjut.disable()
        }
    }

    override fun onFinished(packData: Bundle) {
        var mIntent: Intent?
        when (packData.getInt(PublicConfig.KeyExtras.APP_CONDITION_KEY)) {
            PublicConfig.AppFlags.APP_IS_FIRST_USAGE -> {
                btnLanjut.enable()
                btnLanjut.setOnClickListener {
                    // move into tutorial activity
                    mIntent = Intent(this, TutorialFirstUse::class.java)
                    mIntent?.putExtras(packData)
                    finish()
                    startActivity(mIntent)
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                }
            }
            else -> {
                mIntent = Intent(this, MainActivity::class.java)
                mIntent?.putExtras(packData)
                finish()
                startActivity(mIntent)
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            }
        }

    }

    override fun onStartUi() {
        btnLanjut = actsplash_id_btnlanjut
        txtStatus = actsplash_id_txtsplash_indicator
        linearSwitch = actsplash_id_linear_indicator

        // Tinting the status bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.statusBarColor = getColor(R.color.color_status_white_dark)
            } else
                window.statusBarColor = resources.getColor(R.color.color_status_white_dark)
        }

        // sets the typeface
        actsplash_id_txtjudul.setTypeface(
            Typeface.createFromAsset(assets, SplashScreenParams.SECT_TITLE_FONTS),
            Typeface.BOLD
        )
        btnLanjut.typeface = Typeface.createFromAsset(assets, SplashScreenParams.SECT_BTNNEXT_FONTS)

        // acquire a size of btn next
        val p = Point()
        windowManager.defaultDisplay.getSize(p)
        btnLanjut.minimumWidth = p.x - ((p.x / SplashScreenParams.MARGIN_BTN_FACTOR_PARAMS) * 2)

        // set the text of status
        txtStatus.text = SplashScreenParams.PREPARE_DATA_TXT

    }

}
