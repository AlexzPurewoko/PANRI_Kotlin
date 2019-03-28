package id.kenshiro.app.panri.activity.splashScreen.util.cache

import android.content.Context
import android.os.Process
import android.util.Log
import com.mylexz.utils.SimpleDiskLruCache
import id.kenshiro.app.panri.activity.splashScreen.api.CreateCache
import id.kenshiro.app.panri.activity.splashScreen.util.BgThreadFactoryCallback
import id.kenshiro.app.panri.activity.splashScreen.util.ManagedThreadPool
import id.kenshiro.app.panri.params.PublicConfig
import java.io.File

object AcquireCache {
    fun configure(ctx: Context): ManagedThreadPool {
        var threadName: String? = null
        val managedThreadPool = ManagedThreadPool.getInstance()
        managedThreadPool.setBgThreadCallback(object : BgThreadFactoryCallback {

            var x: Int = 0
            override fun onBuildThread(source: Thread, runnable: Runnable?) {
                source.priority += Process.THREAD_PRIORITY_MORE_FAVORABLE * ++x
                source.name = threadName
            }

            override fun onUncaughtException(source: Thread, throwable: Throwable) {
                Log.e("AcquireCache", "UncaughtException on thread ${source.name}", throwable)
            }

        })

        val listCacheOp: Array<CreateCache> = arrayOf(
            CreateImgCacheOnMain(),
            CreateImgCacheOnLDiseases()
        )
        val fileSource = File(ctx.cacheDir, PublicConfig.PathConfig.IMAGE_CACHE_PATHNAME)
        fileSource.mkdirs()
        val diskLruCache = SimpleDiskLruCache.getsInstance(fileSource)

        for (classCache in listCacheOp) {
            val runnable = Runnable {
                classCache.create(ctx.cacheDir, ctx, diskLruCache)
            }
            threadName = "Thread${classCache.javaClass.name}"
            managedThreadPool.addRunnable(runnable)
        }
        diskLruCache.close()
        return managedThreadPool
    }

    fun validateImgCache(ctx: Context?): Boolean {
        if (ctx == null) return false
        val file = File(ctx.cacheDir, PublicConfig.PathConfig.IMAGE_CACHE_PATHNAME)
        if (file.listFiles() == null) return false
        return false
    }
}