package id.kenshiro.app.panri.activity.splashScreen.util.cache

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Point
import android.util.Log
import com.mylexz.utils.SimpleDiskLruCache
import id.kenshiro.app.panri.R
import id.kenshiro.app.panri.activity.splashScreen.api.CreateCache
import id.kenshiro.app.panri.params.PublicConfig
import id.kenshiro.app.panri.plugin.DecodeBitmapHelper
import org.jetbrains.anko.windowManager
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException

class CreateImgCacheOnMain : CreateCache {
    override fun create(dir: File, ctx: Context?, diskCache: SimpleDiskLruCache) {
        if (ctx == null) return
        val keyImageLists = arrayOf(
            "viewpager_area_1",
            "viewpager_area_2",
            "viewpager_area_3",
            "viewpager_area_4"
        )
        val point = Point()
        val resources = ctx.resources
        ctx.windowManager.defaultDisplay.getSize(point)
        point.y = Math.round(resources.getDimension(R.dimen.actmain_dimen_viewpager_height))
        for (key in keyImageLists) {
            createCache(resources, key, point, ctx, diskCache)
        }
    }

    private fun createCache(
        resources: Resources,
        key: String,
        point: Point,
        ctx: Context,
        diskCache: SimpleDiskLruCache
    ) {
        val resDrawable = resources.getIdentifier(key, "drawable", ctx.packageName)
        //gets the Bitmap
        val bitmap = DecodeBitmapHelper.decodeAndResizeBitmapsResources(resources, resDrawable, point.y, point.x)
        // creates the scaled bitmaps
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, point.x, point.y, false)
        //gets the byte of bitmap
        val bos = ByteArrayOutputStream()
        var scaling = (bitmap.height / point.y).toFloat()
        scaling = if (scaling < 1.0f) 1.0f else scaling
        scaledBitmap.compress(
            Bitmap.CompressFormat.JPEG,
            Math.round(PublicConfig.Config.QUALITY_FACTOR_VIEWPAGER / scaling),
            bos
        )
        // put into cache
        try {
            diskCache.put(key, bos.toByteArray())
        } catch (e: IOException) {
            Log.e("Task.background()", "IOException occured when putting a cache image", e)
        }

        try {
            bos.close()
        } catch (e: IOException) {
            Log.e("Task.background()", "IOException occured when releasing ByteOutputStream", e)
        }

        bitmap.recycle()
        scaledBitmap.recycle()
        System.gc()
    }
}