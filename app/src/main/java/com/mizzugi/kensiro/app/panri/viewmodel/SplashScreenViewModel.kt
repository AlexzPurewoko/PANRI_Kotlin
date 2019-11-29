package com.mizzugi.kensiro.app.panri.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mizzugi.kensiro.app.panri.plugin.PublicContract
import com.mizzugi.kensiro.app.panri.plugin.onSplashScreen.Modules
import id.apwdevs.library.SimpleDiskLruCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

class SplashScreenViewModel(application: Application) : AndroidViewModel(application) {
    private var fileCache: File
    private lateinit var diskCache: SimpleDiskLruCache
    private val isAllowedCheckDBOnline: Boolean
        get() = (getApplication() as Context).getSharedPreferences(
            PublicContract.SHARED_PREF_NAME,
            Context.MODE_PRIVATE
        ).getBoolean(PublicContract.KEY_AUTOCHECKUPDATE_APPDATA, true)

    val appVersion: MutableLiveData<Int> = MutableLiveData()
    val dbVersion: MutableLiveData<Int> = MutableLiveData()
    val finishedLoading: MutableLiveData<Boolean> = MutableLiveData()
    val isLoading: MutableLiveData<Boolean> = MutableLiveData()
    val statusTextLoading: MutableLiveData<String> = MutableLiveData()

    init {
        appVersion.value = 0
        dbVersion.value = 0
        finishedLoading.value = false
        isLoading.value = false
        statusTextLoading.value = "Mempersiapkan data..."

        fileCache = File(application.cacheDir, "cache")
        fileCache.mkdir()
    }

    fun run() {
        viewModelScope.launch(Dispatchers.IO) {
            finishedLoading.postValue(false)
            val versionApp = Modules.checkAndSaveAppVersion(getApplication())
            appVersion.postValue(versionApp)
            diskCache = SimpleDiskLruCache.getsInstance(fileCache)

            val ctx = getApplication() as Context

            when (versionApp) {
                PublicContract.APP_IS_NEWER_VERSION -> {
                    Modules.cleanCache(fileCache)
                    Modules.updateAppDataInApp(ctx, ctx.filesDir)

                    dbVersion.postValue(Modules.checkDbVersion(ctx))
                }

                PublicContract.APP_IS_FIRST_USAGE -> {
                    ctx.filesDir.mkdir()
                    Modules.extractData(ctx, ctx.filesDir, "data_panri.zip")
                    Modules.configureData(ctx)
                }

                PublicContract.APP_IS_SAME_VERSION -> {

                }
            }
            statusTextLoading.postValue("Hampir siap...")

            delay(500)

            diskCache.close()

            Modules.finalizing(ctx)
            statusTextLoading.postValue("Selesai...")
            finishedLoading.postValue(true)
        }
    }
}