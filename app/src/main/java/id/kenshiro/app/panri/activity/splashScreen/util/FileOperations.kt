package id.kenshiro.app.panri.activity.splashScreen.util

import android.content.Context
import id.kenshiro.app.panri.activity.splashScreen.api.FileExtractions
import id.kenshiro.app.panri.params.PublicConfig
import java.io.File
import java.io.InputStream

object FileOperations {
    fun scheduleExtract(ctx: Context?) {
        if (ctx == null) return

        // place commands here

        val dirs = arrayOf<File>(
            ctx.getDatabasePath(PublicConfig.Assets.DB_ASSET_NAME).parentFile,
            ctx.getDatabasePath(PublicConfig.Assets.DB_ASSET_NAME).parentFile
        )
        val streams = arrayOf<InputStream>(
            ctx.assets.open(PublicConfig.Assets.DB_ASSET_NAME),
            ctx.assets.open(PublicConfig.Assets.DB_ADS_JOURNAL_NAME)
        )
        val extractFunc = arrayOf<FileExtractions>(
            DBExtract(),
            AdsJournalDBExtract()
        )

        // operate
        for ((index, values) in extractFunc.withIndex()) {
            values.extractFile(dirs[index], streams[index])
            streams[index].close()
        }

        // OKAY
    }
}