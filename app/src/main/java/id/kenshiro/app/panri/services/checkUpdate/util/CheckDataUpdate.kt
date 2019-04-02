package id.kenshiro.app.panri.services.checkUpdate.util

import android.content.Context
import android.os.Message
import com.google.firebase.storage.FirebaseStorage
import id.kenshiro.app.panri.api.ProgressApiCallback
import id.kenshiro.app.panri.params.PublicConfig
import id.kenshiro.app.panri.services.api.CheckUpdateApi
import java.io.File

class CheckDataUpdate : CheckUpdateApi {
    private lateinit var firebaseStorage: FirebaseStorage
    override fun checkUpdate(ctx: Context?, progress: ProgressApiCallback) {
        if (ctx == null) return
        firebaseStorage = FirebaseStorage.getInstance()
        val storageReference = firebaseStorage.reference
        val pathReference = storageReference.child(PublicConfig.CloudConfig.DATA_CONF_FILE_NAME)
        val fileDataConf = File(ctx.filesDir, PublicConfig.CloudConfig.DATA_CONF_FILE_NAME)
        val fileStream = pathReference.getFile(fileDataConf)
        fileStream.addOnSuccessListener {
            if (it.error != null && fileDataConf.exists()) {
                progress.onFailure(it, null, it.error)
                return@addOnSuccessListener
            }
            progress.onCompleted(it, fileDataConf)
        }
        fileStream.addOnFailureListener {
            val message = Message()
            message.obj = "Failure when get the File of Properties"
            progress.onFailure(it, message, it)
        }
        fileStream.addOnProgressListener {
            progress.onProgress(it, null, ((it.bytesTransferred.toDouble() * 100) / it.totalByteCount), true)
        }
    }
}