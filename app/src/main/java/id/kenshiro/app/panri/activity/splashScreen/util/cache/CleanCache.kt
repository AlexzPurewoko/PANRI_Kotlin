package id.kenshiro.app.panri.activity.splashScreen.util.cache

import android.content.Context
import id.kenshiro.app.panri.activity.splashScreen.api.CleanCacheApi

object CleanCache {
    fun clean(ctx: Context?) {
        if (ctx == null) return

        // add in here
        val listClean = arrayOf<CleanCacheApi>(
            CleanImageCache()
        )

        for (value in listClean) {
            value.clean(ctx)
        }
        // OKAY
    }
}