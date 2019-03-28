package id.kenshiro.app.panri.activity.splashScreen.util.cache

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import androidx.room.Room
import com.mylexz.utils.SimpleDiskLruCache
import id.kenshiro.app.panri.R
import id.kenshiro.app.panri.activity.splashScreen.api.CreateCache
import id.kenshiro.app.panri.databases.data.ListImgDiseaseDb
import id.kenshiro.app.panri.params.PublicConfig
import id.kenshiro.app.panri.plugin.DecodeBitmapHelper
import java.io.ByteArrayOutputStream
import java.io.File

class CreateImgCacheOnLDiseases : CreateCache {
    override fun create(dir: File, ctx: Context?, diskCache: SimpleDiskLruCache) {
        if (ctx == null) return
        val roomDb = Room.databaseBuilder(ctx, ListImgDiseaseDb::class.java, PublicConfig.Assets.DB_ASSET_NAME)
        roomDb.enableMultiInstanceInvalidation()
        val dbQuery = roomDb.build()
        val listImgFunc = dbQuery.getListImgFunc().getAll()
        val assetMgr = ctx.assets

        val sizeImages = Math.round(ctx.resources.getDimension(R.dimen.actmain_dimen_opimg_incard_wh))

        for (imageFunc in listImgFunc) {
            val buf = imageFunc.pathImage?.split(",") ?: continue
            val name = buf[0]
            createImgCache(assetMgr, diskCache, name, sizeImages)
        }
        System.gc()
    }

    private fun createImgCache(assetMgr: AssetManager, diskCache: SimpleDiskLruCache, nameId: String, sizeImages: Int) {
        val stream = assetMgr.open("${PublicConfig.Assets.LIST_DISEASE_IMGPATH_CARD}/$nameId.jpg")
        val bitmap = DecodeBitmapHelper.decodeBitmapStream(stream)
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, sizeImages, sizeImages, false)
        stream.close()

        val bos = ByteArrayOutputStream()
        var scaling: Float = (bitmap.height / sizeImages).toFloat()
        scaling = if (scaling < 1.0f) 1.0f else scaling
        scaledBitmap.compress(
            Bitmap.CompressFormat.WEBP,
            Math.round(PublicConfig.Config.QUALITY_FACTOR_LIMGDISEASE / scaling),
            bos
        )

        synchronized(diskCache) {
            diskCache.put(nameId, bos.toByteArray())
        }

        bos.close()
        bitmap.recycle()
        scaledBitmap.recycle()
    }

}