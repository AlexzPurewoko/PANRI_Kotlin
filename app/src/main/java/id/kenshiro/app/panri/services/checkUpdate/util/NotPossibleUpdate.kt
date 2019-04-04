package id.kenshiro.app.panri.services.checkUpdate.util

import android.content.Context
import androidx.core.content.edit
import id.kenshiro.app.panri.params.PublicConfig
import id.kenshiro.app.panri.services.api.ApplyAnyUpdates

class NotPossibleUpdate : ApplyAnyUpdates {
    override fun applyUpdate(ctx: Context?, updateParams: HashMap<String, String?>?) {
        if (ctx == null) return
        ctx.getSharedPreferences(PublicConfig.SharedPrefConf.NAME, Context.MODE_PRIVATE).edit(commit = true) {
            putInt(PublicConfig.SharedPrefConf.KEY_VERSION_BOOL_NEW, PublicConfig.AppFlags.DB_REQUEST_UPDATE)
            putString(
                PublicConfig.SharedPrefConf.KEY_DB_REQUP_DESCRIPTION,
                "Mohon maaf :(\nData Aplikasi tidak dapat diupdate karena versi aplikasi anda yang terlalu tua.\nDisarankan untuk meng-update aplikasi ini dengan fitur & data yang terbaru!"
            )
        }
    }
}