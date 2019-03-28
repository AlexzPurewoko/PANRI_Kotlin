/**
 * Copyright @2019 Alexzander Purwoko Widiantoro <purwoko908@gmail.com> HOPEProjects
 *
 * This file is a part of PANRI Android Application
 * Except @author, you must not be able to modify, copying, change this file, because its closed source programs
 * This file is patented by any copyright law
 */

package id.kenshiro.app.panri.plugin

import android.content.res.AssetManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.annotation.DrawableRes
import androidx.annotation.NonNull
import androidx.annotation.Px
import java.io.IOException
import java.io.InputStream

object DecodeBitmapHelper {
    fun decodeAndResizeBitmapsResources(@NonNull res: Resources, @DrawableRes resImageDrawable: Int, @Px reqHeight: Int, @Px reqWidth: Int): Bitmap {
        val optionBitmaps = BitmapFactory.Options()
        optionBitmaps.inJustDecodeBounds = true

        BitmapFactory.decodeResource(res, resImageDrawable, optionBitmaps)

        optionBitmaps.inSampleSize = calculateImageInSampleSize(optionBitmaps, reqHeight, reqWidth)
        optionBitmaps.inJustDecodeBounds = false
        System.gc()
        return BitmapFactory.decodeResource(res, resImageDrawable, optionBitmaps)
    }

    @Throws(IOException::class)
    fun decodeAndResizeBitmapsAssets(@NonNull assets: AssetManager, @NonNull pathImage: String, @Px reqHeight: Int, @Px reqWidth: Int): Bitmap? {
        val optionBitmaps = BitmapFactory.Options()
        optionBitmaps.inJustDecodeBounds = true

        // load image in inputstream
        val imageStream = assets.open(pathImage)
        BitmapFactory.decodeStream(imageStream, null, optionBitmaps)
        imageStream.reset()
        optionBitmaps.inSampleSize = calculateImageInSampleSize(optionBitmaps, reqHeight, reqWidth)
        optionBitmaps.inJustDecodeBounds = false
        val results = BitmapFactory.decodeStream(imageStream, null, optionBitmaps)
        imageStream.close()
        System.gc()
        return results
    }

    @Throws(IOException::class)
    fun decodeAndResizeBitmapStream(@NonNull stream: InputStream, @Px reqHeight: Int, @Px reqWidth: Int): Bitmap? {
        val optionBitmaps = BitmapFactory.Options()
        optionBitmaps.inJustDecodeBounds = true

        // load image in inputstream
        //stream.reset();
        optionBitmaps.inSampleSize = calculateImageInSampleSize(optionBitmaps, reqHeight, reqWidth)
        optionBitmaps.inJustDecodeBounds = false
        val results = BitmapFactory.decodeStream(stream, null, optionBitmaps)
        //imageStream.close();
        System.gc()
        return results
    }

    @Throws(IOException::class)
    fun decodeBitmapStream(@NonNull stream: InputStream): Bitmap {
        return BitmapFactory.decodeStream(stream)
    }

    fun calculateImageInSampleSize(optionBitmaps: BitmapFactory.Options, reqHeight: Int, reqWidth: Int): Int {
        val height = optionBitmaps.outHeight
        val width = optionBitmaps.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfWidth = width / 2
            val halfHeight = height / 2

            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }
}