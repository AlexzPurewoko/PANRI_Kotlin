package com.mizzugi.kensiro.app.panri.workers

import android.content.Context
import android.util.Log
import androidx.core.content.edit
import androidx.core.text.isDigitsOnly
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.FirebaseApp
import com.google.firebase.storage.FirebaseStorage
import com.mizzugi.kensiro.app.panri.plugin.PublicContract
import kotlinx.coroutines.delay

class CheckDatabaseWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        FirebaseApp.initializeApp(applicationContext)

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
        else */if (cloudVersion == null) {
            Log.e("CheckDBWorker", "Failure Jobs... runAttemptCount is $runAttemptCount")
            return Result.failure()
        }

        cloudVersion?.let { validateVersion(it) }
        return Result.success()
    }

    private fun validateVersion(cloudVersion: String) {
        applicationContext.getSharedPreferences(
            PublicContract.SHARED_PREF_NAME,
            Context.MODE_PRIVATE
        ).apply {
            val appDbVersion = getString(PublicContract.KEY_DATA_LIBRARY_VERSION, "") ?: ""
            if (appDbVersion.isEmpty() || !appDbVersion.isDigitsOnly())
                return

            val dbOldVersion = appDbVersion.toLong()
            val newVersion = cloudVersion.trim().toLong()

            when {
                newVersion > dbOldVersion -> setDbAction(
                    PublicContract.DB_IS_NEWER_VERSION,
                    cloudVersion
                )
                dbOldVersion > newVersion -> setDbAction(
                    PublicContract.DB_IS_OLDER_IN_APP_VERSION,
                    cloudVersion
                )
                else -> setDbAction(PublicContract.DB_IS_SAME_VERSION, cloudVersion)
            }
        }
    }

    private fun setDbAction(dbCondition: Int, dbNewVersion: String) {
        applicationContext.getSharedPreferences(
            PublicContract.SHARED_PREF_NAME,
            Context.MODE_PRIVATE
        ).edit(commit = true) {
            putString(PublicContract.KEY_VERSION_ON_CLOUD, dbNewVersion)
            putInt(PublicContract.KEY_VERSION_BOOL_NEW, dbCondition)
        }
    }

}