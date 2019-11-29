package com.mizzugi.kensiro.app.panri.plugin.onSplashScreen

import android.app.Application
import android.content.Context
import androidx.annotation.WorkerThread
import androidx.core.content.edit
import com.mizzugi.kensiro.app.panri.BuildConfig
import com.mizzugi.kensiro.app.panri.plugin.PublicContract
import com.mizzugi.kensiro.app.panri.plugin.UnzipFile
import org.apache.commons.io.FileUtils
import java.io.File

object Modules {

    // returned the condition of appVersion that saved into sharedPreferences
    @WorkerThread
    fun checkAndSaveAppVersion(application: Application): Int {
        val version = BuildConfig.VERSION_CODE
        return application.getSharedPreferences(
            PublicContract.SHARED_PREF_NAME,
            Context.MODE_PRIVATE
        ).let {
            if (!it.contains(PublicContract.KEY_APP_VERSION)) return@let PublicContract.APP_IS_FIRST_USAGE

            val currAppVersion = it.getInt(PublicContract.KEY_APP_VERSION, 0)
            when {
                currAppVersion < version -> {
                    it.edit {
                        putInt(PublicContract.KEY_APP_VERSION, version)
                    }
                    return@let PublicContract.APP_IS_NEWER_VERSION
                }
                currAppVersion > version -> return@let PublicContract.APP_IS_OLDER_VERSION
                else -> return@let PublicContract.APP_IS_SAME_VERSION
            }
        }
    }


    fun checkDbVersion(ctx: Context): Int =
        ctx.getSharedPreferences(PublicContract.SHARED_PREF_NAME, Context.MODE_PRIVATE).let {

            val dbVersionLeast = getStringFromAssets(ctx, "db_version").toInt()
            val dbCurrentVersion: Int =
                it.getString(PublicContract.KEY_DATA_LIBRARY_VERSION, null)?.toInt() ?: 0
            if (dbVersionLeast > dbCurrentVersion) {
                it.edit(commit = true) {
                    putString(PublicContract.KEY_DATA_LIBRARY_VERSION, dbVersionLeast.toString())
                }
                return@let PublicContract.DB_IS_NEWER_VERSION
            }
            return@let 0
        }

    fun cleanCache(fileCache: File) {
        fileCache.apply {
            delete()
            mkdir()
        }
    }

    fun updateAppDataInApp(ctx: Context, filesDir: File?) {
        filesDir?.apply {
            FileUtils.deleteDirectory(File(this, "data"))
            FileUtils.deleteDirectory(File(this, "data_hama_html"))
            FileUtils.deleteQuietly(File(this, "database_penyakitpadi.db"))
            mkdirs()

            extractData(ctx, filesDir, "data_panri.zip")
        }
    }

    fun configureData(ctx: Context) {
        ctx.getSharedPreferences(PublicContract.SHARED_PREF_NAME, Context.MODE_PRIVATE).apply {
            val dbVersion = getStringFromAssets(ctx, "db_version")
            edit(commit = true) {
                if (!contains(PublicContract.KEY_DATA_LIBRARY_VERSION))
                    putString(PublicContract.KEY_DATA_LIBRARY_VERSION, dbVersion)


                if (!contains(PublicContract.KEY_APP_VERSION))
                    putInt(PublicContract.KEY_APP_VERSION, BuildConfig.VERSION_CODE)


                if (!contains(PublicContract.KEY_SHARED_DATA_CURRENT_IMG_NAVHEADER))
                    putInt(PublicContract.KEY_SHARED_DATA_CURRENT_IMG_NAVHEADER, 0)


                if (!contains(PublicContract.KEY_AUTOCHECKUPDATE_APPDATA))
                    putBoolean(PublicContract.KEY_AUTOCHECKUPDATE_APPDATA, true)


                if (!contains(PublicContract.KEY_VERSION_BOOL_NEW))
                    putInt(PublicContract.KEY_VERSION_BOOL_NEW, PublicContract.DB_IS_SAME_VERSION)


                if (!contains(PublicContract.KEY_VERSION_ON_CLOUD))
                    putString(PublicContract.KEY_VERSION_ON_CLOUD, "undefined")
            }
        }
    }

    fun getStringFromAssets(ctx: Context, fileName: String): String {
        val inputStream = ctx.assets.open(fileName)
        val buf = ByteArray(inputStream.available())
        inputStream.read(buf)
        inputStream.close()
        return String(buf)

    }


    // private extended functions
    fun extractData(ctx: Context, dirsOut: File, fileName: String) {

        val inputStream = ctx.assets.open(fileName)
        UnzipFile.unzip(inputStream, dirsOut)
        inputStream.close()
        moveDataBaseFile(ctx)
    }

    fun moveDataBaseFile(ctx: Context) {
        val dest = File(
            File("/data/data/${ctx.packageName}/databases").apply { mkdir() },
            "database_penyakitpadi.db"
        )
        if (dest.exists()) FileUtils.deleteQuietly(dest)
        FileUtils.moveFile(File(ctx.filesDir, "database_penyakitpadi.db"), dest)
    }

    fun finalizing(ctx: Context) {
        ctx.getSharedPreferences(PublicContract.SHARED_PREF_NAME, Context.MODE_PRIVATE).apply {

            // increment the navheader number
            edit(commit = true) {
                putInt(
                    PublicContract.KEY_SHARED_DATA_CURRENT_IMG_NAVHEADER,
                    getInt(PublicContract.KEY_SHARED_DATA_CURRENT_IMG_NAVHEADER, 0).let {
                        if (it == 4) return@let 0
                        else
                            return@let it + 1
                    }
                )
            }

            // do another if necessary...
        }
    }

}