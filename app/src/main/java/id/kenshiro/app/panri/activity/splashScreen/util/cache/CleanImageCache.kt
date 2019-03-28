package id.kenshiro.app.panri.activity.splashScreen.util.cache

import android.content.Context
import id.kenshiro.app.panri.activity.splashScreen.api.CleanCacheApi
import id.kenshiro.app.panri.params.PublicConfig
import org.apache.commons.io.FileUtils
import java.io.File

class CleanImageCache : CleanCacheApi {
    override fun clean(ctx: Context?) {
        if (ctx == null) return
        val file = File(ctx.cacheDir, PublicConfig.PathConfig.IMAGE_CACHE_PATHNAME)
        FileUtils.deleteDirectory(file)
        file.mkdir()
    }

}