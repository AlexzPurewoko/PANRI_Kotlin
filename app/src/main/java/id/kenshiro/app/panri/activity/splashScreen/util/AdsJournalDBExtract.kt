package id.kenshiro.app.panri.activity.splashScreen.util

import id.kenshiro.app.panri.activity.splashScreen.api.FileExtractions
import id.kenshiro.app.panri.params.PublicConfig
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

class AdsJournalDBExtract : FileExtractions {
    // extract into database folder
    @Throws(IOException::class)
    override fun extractFile(dir: File, source: InputStream) {
        dir.mkdir()
        val file = File(dir, PublicConfig.Assets.DB_ADS_JOURNAL_NAME)
        val fos = FileOutputStream(file)
        var read: Int
        while (true) {
            read = source.read()
            if (read == -1) break
            fos.write(read)
        }
        fos.flush()
        fos.close()
    }
}