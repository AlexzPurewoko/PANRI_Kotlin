/**
 * Copyright @2019 Alexzander Purwoko Widiantoro <purwoko908@gmail.com> HOPEProjects
 *
 * This file is a part of PANRI Android Application
 * Except @author, you must not be able to modify, copying, change this file, because its closed source programs
 * This file is patented by any copyright law
 */

package id.kenshiro.app.panri.activity.splashScreen.presenter

import android.content.Context
import android.os.Bundle
import androidx.core.content.edit
import com.mylexz.utils.BuildConfig
import id.kenshiro.app.panri.activity.splashScreen.SplashScreenActivity
import id.kenshiro.app.panri.activity.splashScreen.model.SplashScreenModel
import id.kenshiro.app.panri.activity.splashScreen.util.DataConf
import id.kenshiro.app.panri.activity.splashScreen.util.FileOperations
import id.kenshiro.app.panri.activity.splashScreen.util.StartServices
import id.kenshiro.app.panri.activity.splashScreen.util.cache.AcquireCache
import id.kenshiro.app.panri.activity.splashScreen.util.cache.CleanCache
import id.kenshiro.app.panri.params.PublicConfig
import id.kenshiro.app.panri.params.SplashScreenParams
import id.kenshiro.app.panri.plugin.CoroutineContextProvider
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.lang.ref.WeakReference

class SplashScreenPresenter(
    ctx: SplashScreenActivity,
    model: SplashScreenModel,
    private val contextPool: CoroutineContextProvider = CoroutineContextProvider()
) {
    private val ctxWeak: WeakReference<SplashScreenActivity> = WeakReference(ctx)
    private val modelWeak: WeakReference<SplashScreenModel> = WeakReference(model)
    private var appCondition: Int = 0
    private var dbCondition: Int = 0
    fun runAllCommands() {
        GlobalScope.launch(contextPool.main) {
            ctxWeak.get()?.also {
                it.cacheDir.mkdir()
                File(it.filesDir, PublicConfig.PathConfig.OTA_PATCH_UPDATES_PATH).mkdir()
            }
            modelWeak.get()?.onStartUi()

            // gets App Usage
            getAndCheckAppUsage()

            modelWeak.get()?.onUpdatedTextProgress("Memproses")
            // procedure
            when (appCondition) {
                PublicConfig.AppFlags.APP_IS_SAME_VERSION -> {
                    if (!AcquireCache.validateImgCache(ctxWeak.get())) {
                        if (ctxWeak.get() == null) {
                            modelWeak.get()?.onLoadFailed(Exception("Cannot start loading!, ctxWeak == null"))
                            return@launch
                        } else {
                            val acquireCachePack = AcquireCache.configure(ctxWeak.get()!!)
                            val managedThreadPool = acquireCachePack.managedThreadPool
                            val diskLruCache = acquireCachePack.diskLruCache
                            StartServices.start(ctxWeak.get())
                            while (!managedThreadPool.isAllTaskFinished()) {
                                delay(500)
                            }
                            diskLruCache.close()
                        }

                    }
                }
                PublicConfig.AppFlags.APP_IS_NEWER_VERSION -> {
                    CleanCache.clean(ctxWeak.get())
                    // schedule extracting
                    FileOperations.scheduleExtract(ctxWeak.get())

                    // Acquiring cache and wait all caching threads until finished

                    val acquireCachePack = AcquireCache.configure(ctxWeak.get()!!)
                    val managedThreadPool = acquireCachePack.managedThreadPool
                    val diskLruCache = acquireCachePack.diskLruCache

                    // configures all data
                    DataConf.configureData(ctxWeak.get())

                    StartServices.start(ctxWeak.get())

                    while (!managedThreadPool.isAllTaskFinished())
                        delay(SplashScreenParams.DELAY_WAITING_THREADS)
                    diskLruCache.close()
                }
                PublicConfig.AppFlags.APP_IS_FIRST_USAGE -> {
                    // schedule extracting
                    FileOperations.scheduleExtract(ctxWeak.get())

                    // Acquiring cache and wait all caching threads until finished

                    val acquireCachePack = AcquireCache.configure(ctxWeak.get()!!)
                    val managedThreadPool = acquireCachePack.managedThreadPool
                    val diskLruCache = acquireCachePack.diskLruCache

                    // configures all data
                    DataConf.configureData(ctxWeak.get())

                    StartServices.start(ctxWeak.get())

                    while (!managedThreadPool.isAllTaskFinished())
                        delay(SplashScreenParams.DELAY_WAITING_THREADS)
                    diskLruCache.close()
                }
            }
            modelWeak.get()?.onLoadingFinished(appCondition == PublicConfig.AppFlags.APP_IS_FIRST_USAGE)
            // build a data bundle to push into other activity
            val data = Bundle()
            data.putInt(PublicConfig.KeyExtras.APP_CONDITION_KEY, appCondition)
            data.putInt(PublicConfig.KeyExtras.DB_CONDITION_KEY, dbCondition)
            delay(SplashScreenParams.DELAY_WAITING_LOADING)
            modelWeak.get()?.onFinished(data)
        }
    }

    private fun getAndCheckAppUsage() {
        val apkVersion = BuildConfig.VERSION_CODE
        if (ctxWeak.get() == null) return
        val sharedPreferences =
            ctxWeak.get()!!.getSharedPreferences(PublicConfig.SharedPrefConf.NAME, Context.MODE_PRIVATE)
        sharedPreferences.apply {
            if (!contains(PublicConfig.SharedPrefConf.KEY_APP_VERSION)) {
                appCondition = PublicConfig.AppFlags.APP_IS_FIRST_USAGE
            } else {
                val currAppVersion = getInt(PublicConfig.SharedPrefConf.KEY_APP_VERSION, apkVersion)
                when {
                    currAppVersion < apkVersion -> {
                        appCondition = PublicConfig.AppFlags.APP_IS_NEWER_VERSION
                        edit {
                            putInt(PublicConfig.SharedPrefConf.KEY_APP_VERSION, apkVersion)
                        }
                    }
                    currAppVersion > apkVersion -> appCondition = PublicConfig.AppFlags.APP_IS_OLDER_VERSION
                    else -> appCondition = PublicConfig.AppFlags.APP_IS_SAME_VERSION
                }
            }
        }
    }
}