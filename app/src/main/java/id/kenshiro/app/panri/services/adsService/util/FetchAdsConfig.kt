package id.kenshiro.app.panri.services.adsService.util

import android.content.Context
import android.os.Message
import com.google.firebase.storage.FirebaseStorage
import id.kenshiro.app.panri.api.ProgressApiCallback
import id.kenshiro.app.panri.params.PublicConfig
import id.kenshiro.app.panri.services.api.FetchAdsConfigApi
import java.io.File

class FetchAdsConfig : FetchAdsConfigApi {
    private lateinit var firebaseStorage: FirebaseStorage
    override fun fetchConfig(ctx: Context?, progress: ProgressApiCallback?) {
        if (ctx == null) return
        firebaseStorage = FirebaseStorage.getInstance()
        val storageReference = firebaseStorage.reference
        val pathReference = storageReference.child(PublicConfig.CloudConfig.AVAILADS_CONF_FILENAME)
        val adsDataConf = File(ctx.filesDir, PublicConfig.CloudConfig.AVAILADS_CONF_FILENAME_ALIAS)
        val fileStream = pathReference.getFile(adsDataConf)
        fileStream.addOnSuccessListener {
            if (it.error != null && adsDataConf.exists()) {
                progress?.onFailure(it, null, it.error)
                return@addOnSuccessListener
            }
            progress?.onCompleted(it, adsDataConf)
        }
        fileStream.addOnFailureListener {
            val message = Message()
            message.obj = "Failure when get the File of Properties"
            progress?.onFailure(it, message, it)
        }
        fileStream.addOnProgressListener {
            progress?.onProgress(it, null, ((it.bytesTransferred.toDouble() * 100) / it.totalByteCount), true)
        }
    }

}