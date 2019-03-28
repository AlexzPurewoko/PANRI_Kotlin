/**
 * Copyright @2019 Alexzander Purwoko Widiantoro <purwoko908@gmail.com> HOPEProjects
 *
 * This file is a part of PANRI Android Application
 * Except @author, you must not be able to modify, copying, change this file, because its closed source programs
 * This file is patented by any copyright law
 */

package id.kenshiro.app.panri.activity.splashScreen.model

import android.os.Bundle

interface SplashScreenModel {
    fun onStartUi()
    fun onUpdatedTextProgress(text: String)
    fun onLoadFailed(ex: Throwable)
    fun onLoadingFinished(isFirstUsage: Boolean)
    fun onFinished(packData: Bundle)
}