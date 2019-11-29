package com.mizzugi.kensiro.app.panri.plugin.onMain

import android.widget.LinearLayout
import com.mizzugi.kensiro.app.panri.BaseActivity
import com.mizzugi.kensiro.app.panri.activity.MainActivity

object Modules {

    fun initializeSectOptIntent(activity: BaseActivity, holder: LinearLayout) {
        InitialSectOpIntentModule.setup(activity, holder)
    }

    fun validateVersion(activity: MainActivity) {
        ValidateVersionModule.validateVersion(activity)
    }
}
