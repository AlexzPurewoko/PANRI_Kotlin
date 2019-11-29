package com.mizzugi.kensiro.app.panri.plugin

import android.content.Intent
import android.os.Bundle
import com.mizzugi.kensiro.app.panri.BaseActivity
import com.mizzugi.kensiro.app.panri.activity.MainActivity

object SwitchActivity {
    fun switchTo(activity: BaseActivity, cls: Class<*>, args: Bundle?) {
        activity.apply {
            //finish()
            startActivity(Intent(activity, cls).apply {
                args?.let { putExtras(it) }
            })
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }

    fun switchToMain(activity: BaseActivity) {
        switchTo(activity, MainActivity::class.java, Bundle().apply {
            putInt(PublicContract.DB_CONDITION_KEY, PublicContract.DB_IS_SAME_VERSION)
            putInt(PublicContract.APP_CONDITION_KEY, PublicContract.APP_IS_SAME_VERSION)
        })
    }
}