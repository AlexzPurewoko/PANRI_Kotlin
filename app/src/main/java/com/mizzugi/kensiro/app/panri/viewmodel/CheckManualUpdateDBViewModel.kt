package com.mizzugi.kensiro.app.panri.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.storage.FirebaseStorage
import com.mizzugi.kensiro.app.panri.plugin.CheckConnection
import com.mizzugi.kensiro.app.panri.plugin.PublicContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CheckManualUpdateDBViewModel(application: Application) : AndroidViewModel(application) {

    val finishedLoading: MutableLiveData<Boolean> = MutableLiveData()

    var newDBVersion: String? = null

    var oldDBVersion: String? = null

    var message: Int = 0

    fun check() {
        viewModelScope.launch(Dispatchers.IO) {
            finishedLoading.postValue(false)
            val isConnected = CheckConnection.isConnected(getApplication(), 30000)
            if (isConnected) {
                val fStorage = FirebaseStorage.getInstance()
                val versionReference = fStorage.reference.child("db_version")

                var isFinished = false
                var cloudVersion: String? = null
                versionReference.getBytes(1024)
                    .addOnSuccessListener {
                        cloudVersion = String(it)
                        isFinished = true
                    }.addOnFailureListener {
                        Log.e("CheckDatabaseWorker", "Error when get the value from server...", it)
                        isFinished = true
                        cloudVersion = null
                    }

                while (!isFinished) {
                    delay(350)
                }
                /*if(cloudVersion == null && runAttemptCount < 3) {
                    Log.e("CheckDBWorker", "Retrying Jobs... runAttemptCount is $runAttemptCount")
                    return Result.retry()
                }
                else
                if (cloudVersion == null) {

                }*/
                cloudVersion?.let {
                    val shUpdate = shouldUpdate(it, getApplication())
                    message =
                        if (shUpdate) PublicContract.UPDATE_DB_IS_AVAILABLE else PublicContract.UPDATE_DB_NOT_AVAILABLE
                    newDBVersion = it
                }
            } else {
                message = PublicContract.UPDATE_DB_NOT_AVAILABLE_INTERNET_MISSING
            }

            finishedLoading.postValue(true)
        }


    }

    private fun shouldUpdate(cloudVersion: String, context: Context): Boolean {
        context.getSharedPreferences(PublicContract.SHARED_PREF_NAME, Context.MODE_PRIVATE).apply {
            val appDbVersion = getString(PublicContract.KEY_DATA_LIBRARY_VERSION, "") ?: ""
            oldDBVersion = appDbVersion
            if (appDbVersion.isEmpty() || !appDbVersion.isDigitsOnly())
                return false

            val dbOldVersion = appDbVersion.toInt()
            val newVersion = cloudVersion.trim().toInt()

            when {
                newVersion > dbOldVersion -> return true
                dbOldVersion > newVersion -> return false
                else -> return false
            }
        }
    }
}