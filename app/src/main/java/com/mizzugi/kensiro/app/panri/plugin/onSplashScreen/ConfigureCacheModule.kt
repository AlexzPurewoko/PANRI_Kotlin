package com.mizzugi.kensiro.app.panri.plugin.onSplashScreen

import android.content.Context
import id.apwdevs.library.SimpleDiskLruCache

class ConfigureCacheModule constructor(
    private val ctx: Context,
    private val diskLruCache: SimpleDiskLruCache
) {
/*
    private val isEmptyCacheDirs : Boolean
    get() {
        return File(ctx.filesDir, "cache").apply {
            mkdir()
        }.list().isEmpty()
    }

    fun configureCache() {
        if(isEmptyCacheDirs){
            cachingBitmapsVP()
            cachingListImageCard1()
        }
    }


    private fun cachingBitmapsViewPager() {
        val keyImageLists =
            arrayOf("viewpager_area_1", "viewpager_area_2", "viewpager_area_3", "viewpager_area_4")
        val point = Point()
        ctx.getWindowManager().getDefaultDisplay().getSize(point)
        point.y = Math.round(ctx.resources.getDimension(R.dimen.actmain_dimen_viewpager_height))
        for (key in keyImageLists) {
            val resDrawable = ctx.resources.getIdentifier(key, "drawable", ctx.packageName)
            //gets the Bitmap
            val bitmap = DecodeBitmapHelper.decodeAndResizeBitmapsResources(
                ctx.resources,
                resDrawable,
                point.y,
                point.x
            )
            // creates the scaled bitmaps
            val scaledBitmap = Bitmap.createScaledBitmap(bitmap, point.x, point.y, false)
            //gets the byte of bitmap
            val bos = ByteArrayOutputStream()
            var scaling = (bitmap.getHeight() / point.y).toFloat()
            scaling = if (scaling < 1.0f) 1.0f else scaling
            scaledBitmap.compress(
                Bitmap.CompressFormat.JPEG,
                Math.round(QUALITY_FACTOR / scaling),
                bos
            )
            // put into cache
            try {
                diskCache.put(key, bos.toByteArray())
            } catch (e: IOException) {
                ctx.LOGE("Task.background()", "IOException occured when putting a cache image", e)
            }

            try {
                bos.close()
            } catch (e: IOException) {
                ctx.LOGE(
                    "Task.background()",
                    "IOException occured when releasing ByteOutputStream",
                    e
                )
            }

            bitmap.recycle()
            scaledBitmap.recycle()
            System.gc()
        }
        // for nav header background
    }

 */
}