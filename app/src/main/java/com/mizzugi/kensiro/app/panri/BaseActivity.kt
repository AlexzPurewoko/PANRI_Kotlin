package com.mizzugi.kensiro.app.panri

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import id.apwdevs.library.SystemBarTintManager

@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity() {

    protected var translucentStatus: Boolean = false
        set(value) {

            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
                val winParams = window.attributes
                val bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                if (value) {
                    winParams.flags = winParams.flags or bits
                } else {
                    winParams.flags = winParams.flags and bits.inv()
                }
                window.attributes = winParams
                systemTint = SystemBarTintManager(this)
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && value) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            }
            field = value
        }
    private var systemTint: SystemBarTintManager? = null
    fun TOAST(time: Int, format: String, vararg args: Any) {
        Toast.makeText(this, String.format(format, *args), time).show()
    }

    fun LOGI(tag: String, format: String, vararg p: Any) {

        Log.i(tag, String.format(format, *p))
    }

    fun LOGI(tag: String, message: String, exception: Throwable) {

        Log.i(tag, message, exception)
    }

    fun LOGE(tag: String, format: String, vararg p: Any) {

        Log.e(tag, String.format(format, *p))
    }

    fun LOGE(tag: String, message: String, exception: Throwable) {

        Log.e(tag, message, exception)
    }

    fun LOGD(tag: String, format: String, vararg p: Any) {

        Log.d(tag, String.format(format, *p))
    }

    fun LOGD(tag: String, message: String, exception: Throwable) {

        Log.d(tag, message, exception)
    }

    fun LOGV(tag: String, format: String, vararg p: Any) {

        Log.v(tag, String.format(format, *p))
    }

    fun LOGV(tag: String, message: String, exception: Throwable) {

        Log.v(tag, message, exception)
    }

    fun LOGW(tag: String, format: String, vararg p: Any) {
        Log.w(tag, String.format(format, *p))
    }

    fun LOGW(tag: String, message: String, exception: Throwable) {
        Log.w(tag, message, exception)
    }

    protected fun setStatusBarColor(color: Int) {
        if (!translucentStatus) return
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            systemTint?.setStatusBarTintColor(color)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = color
        }
    }

    protected fun setStatusBarColorResource(@ColorRes resColor: Int) {
        setStatusBarColor(ContextCompat.getColor(applicationContext, resColor))
    }
}