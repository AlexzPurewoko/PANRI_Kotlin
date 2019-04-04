package id.kenshiro.app.panri.activity.splashScreen.util

import android.content.Context
import androidx.core.content.edit
import id.kenshiro.app.panri.BuildConfig
import id.kenshiro.app.panri.params.PublicConfig
import java.io.IOException

object DataConf {
    @Throws(IOException::class)
    fun configureData(ctx: Context?) {
        if (ctx == null) return
        ctx.getSharedPreferences(PublicConfig.SharedPrefConf.NAME, Context.MODE_PRIVATE).edit {
            val dbVersion = getFromAssets(PublicConfig.Assets.DB_VERSION_ASSET_NAME, ctx)
            val adsVersion = PublicConfig.KeyExtras.UNDEFINED_KEY

            // stores data Version
            putString(PublicConfig.SharedPrefConf.KEY_DATA_LIBRARY_VERSION, dbVersion)

            // stores ads version
            putString(PublicConfig.SharedPrefConf.KEY_ADS_VERSION, adsVersion)

            // stores boolean flags auto check App Data Update
            putBoolean(PublicConfig.SharedPrefConf.KEY_AUTOCHECKUPDATE_APPDATA, true)

            // stores current app version
            putInt(PublicConfig.SharedPrefConf.KEY_APP_VERSION, BuildConfig.VERSION_CODE)

            // stores current position of Navigation Bar's header
            putInt(PublicConfig.SharedPrefConf.KEY_SHARED_DATA_CURRENT_IMG_NAVHEADER, 0)

            // Stores the flags of App Data Condition
            // and if any update, it will be a value of DB_REQUEST_UPDATE
            putInt(PublicConfig.SharedPrefConf.KEY_VERSION_BOOL_NEW, PublicConfig.AppFlags.DB_IS_SAME_VERSION)

            // the Data Version on Cloud that request update
            // undefined is if Service is not yet started
            putString(PublicConfig.SharedPrefConf.KEY_DATA_VERSION_ON_CLOUD, PublicConfig.KeyExtras.UNDEFINED_KEY)
        }
    }

    @Throws(IOException::class)
    fun getFromAssets(assetName: String, ctx: Context): String {
        val stream = ctx.assets.open(assetName)
        val byte = ByteArray(stream.available())
        stream.read(byte)
        stream.close()
        return String(byte)
    }
}