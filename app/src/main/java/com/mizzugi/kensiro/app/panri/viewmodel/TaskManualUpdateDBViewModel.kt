package com.mizzugi.kensiro.app.panri.viewmodel

import android.app.Application
import android.content.Context
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.storage.FirebaseStorage
import com.mizzugi.kensiro.app.panri.plugin.PublicContract
import com.mizzugi.kensiro.app.panri.plugin.UnzipFile
import com.mizzugi.kensiro.app.panri.plugin.onSplashScreen.Modules
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.FileInputStream
import kotlin.math.roundToInt

class TaskManualUpdateDBViewModel(application: Application) : AndroidViewModel(application) {

    val finishedLoading: MutableLiveData<Boolean> = MutableLiveData()
    val currentProgress: MutableLiveData<Int> = MutableLiveData()

    var stateText: String = ""

    var isCompleted: Boolean = false

    fun run(newDBVersion: String) {
        viewModelScope.launch(Dispatchers.IO) {
            finishedLoading.postValue(false)
            var isFinished = false
            var downloadSuccess = false
            stateText = "Downloading"
            val firebaseStorage = FirebaseStorage.getInstance()
            val storageReference = firebaseStorage.reference
            val pathReference = storageReference.child("data_panri.zip")

            val panriTmp = File.createTempFile("data_panri", "zip")
            pathReference.getFile(panriTmp)
                .addOnProgressListener {
                    val totalByteCount = it.totalByteCount
                    val receivedByteCount = it.bytesTransferred
                    val percent = (receivedByteCount * 100 / totalByteCount).toDouble()
                    currentProgress.postValue(percent.roundToInt())
                }
                .addOnCompleteListener {
                    isFinished = true
                    downloadSuccess = true
                }
                .addOnFailureListener {
                    isFinished = true
                }

            while (!isFinished) delay(400)
            stateText = "Configuring"
            if (downloadSuccess) {
                val ctx = getApplication() as Context
                val disk = ctx.filesDir
                FileUtils.deleteDirectory(File(disk, "data"))
                FileUtils.deleteDirectory(File(disk, "data_hama_html"))
                disk.mkdir()

                val fis = FileInputStream(panriTmp)
                val unzip = UnzipFile.unzip(fis, disk)
                fis.close()
                // move database
                val file = File(
                    "/data/data/${ctx.packageName}/databases",
                    PublicContract.DatabaseContract.DATABASE_FAVORITE_NAME
                )
                FileUtils.deleteQuietly(file)
                Modules.moveDataBaseFile(ctx)

                ctx.getSharedPreferences(PublicContract.SHARED_PREF_NAME, Context.MODE_PRIVATE)
                    .edit(commit = true) {

                        putString(PublicContract.KEY_DATA_LIBRARY_VERSION, newDBVersion)

                        putString(PublicContract.KEY_VERSION_ON_CLOUD, newDBVersion)
                        putInt(
                            PublicContract.KEY_VERSION_BOOL_NEW,
                            PublicContract.DB_IS_SAME_VERSION
                        )
                    }
            }
            isCompleted = downloadSuccess
            finishedLoading.postValue(true)
        }
    }
}