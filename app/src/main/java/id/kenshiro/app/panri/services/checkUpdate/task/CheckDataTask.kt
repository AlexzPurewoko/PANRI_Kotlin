package id.kenshiro.app.panri.services.checkUpdate.task

import android.content.Context
import android.os.Message
import android.os.Process
import android.util.Log
import com.google.firebase.storage.BuildConfig
import com.google.firebase.storage.FileDownloadTask
import id.kenshiro.app.panri.api.CheckConnectionApi
import id.kenshiro.app.panri.api.ProgressApiCallback
import id.kenshiro.app.panri.params.CheckUpdateDataParams
import id.kenshiro.app.panri.params.PublicConfig
import id.kenshiro.app.panri.plugin.ConnectionChecker
import id.kenshiro.app.panri.services.api.ApplyAnyUpdates
import id.kenshiro.app.panri.services.api.CheckUpdateApi
import id.kenshiro.app.panri.services.api.ReadConfigUpdateApi
import id.kenshiro.app.panri.services.checkUpdate.CheckUpdateService
import id.kenshiro.app.panri.services.checkUpdate.util.ApplyConfUpdate
import id.kenshiro.app.panri.services.checkUpdate.util.CheckDataUpdate
import id.kenshiro.app.panri.services.checkUpdate.util.NotPossibleUpdate
import id.kenshiro.app.panri.services.checkUpdate.util.ReadConfigData
import java.io.File
import java.lang.ref.WeakReference
import java.net.ConnectException

class CheckDataTask(instanceService: CheckUpdateService, progressApiCallback: ProgressApiCallback) : Runnable {

    private val instanceServiceWeak = WeakReference<CheckUpdateService>(instanceService)
    private val progressWeak = WeakReference<ProgressApiCallback>(progressApiCallback)
    override fun run() {
        Process.setThreadPriority(PublicConfig.ServiceThrPriority.ON_CHECKUPDATE_SERVICE + 3)
        val connectionApi: CheckConnectionApi = ConnectionChecker()
        val isConnected = connectionApi.isConnected(
            instanceServiceWeak.get()?.baseContext,
            CheckUpdateDataParams.MAX_SERVER_TIMEOUT,
            CheckUpdateDataParams.SERVER_URLS_TO_CHECK
        )
        val serviceContext = instanceServiceWeak.get()

        val progressApi = progressWeak.get()

        // if not connected, then service will be interrupted
        if (!isConnected) {

            if (serviceContext != null || progressApi != null) {
                val message = Message()
                message.what = CheckUpdateDataParams.MESSAGE_CONNECTION_REFUSED
                synchronized(serviceContext!!.isTaskInterrupted) {
                    serviceContext.isTaskInterrupted = true
                    progressApi!!.onFailure(
                        this,
                        message,
                        ConnectException("Caonnot connect into ${CheckUpdateDataParams.SERVER_URLS_TO_CHECK} ERR: CONNECTION_REFUSED")
                    )
                }
            }
            return
        }

        val checkUpdatesApi: CheckUpdateApi = CheckDataUpdate()
        checkUpdatesApi.checkUpdate(serviceContext, object : ProgressApiCallback {
            override fun onStarted(instanceClass: Any?) {
                progressWeak.get()?.onStarted(instanceClass)
            }

            override fun onProgress(instanceClass: Any?, anyObject: Any?, percent: Double, isProcessing: Boolean) {
                val taskSnapshot = instanceClass as FileDownloadTask.TaskSnapshot
                if (serviceContext == null) {
                    // pause it until service is available again
                    taskSnapshot.task.pause()
                    val message = Message()
                    message.what = CheckUpdateDataParams.MESSAGE_SERVICECONTEXT_REACHED_NULL
                    onFailure(this@CheckDataTask, message, Exception("The Context is null, so download will be paused"))
                    return
                }
                synchronized(serviceContext.isTaskInterrupted) {
                    if (serviceContext.isTaskInterrupted) {
                        // pause it until service is available again
                        taskSnapshot.task.pause()
                        val message = Message()
                        message.what = CheckUpdateDataParams.MESSAGE_TASK_INTERRUPTED
                        onFailure(
                            this@CheckDataTask,
                            message,
                            Exception("The Task is interrupted by other condition, so will be paused")
                        )
                        return@synchronized
                    }
                }
                Log.i(
                    "CheckDataTask",
                    "Progress Download percent: $percent bytes_transferred: ${taskSnapshot.bytesTransferred}"
                )
            }

            override fun onCompleted(instanceClass: Any?, returnedObj: Any?) {
                if (serviceContext == null) {
                    return
                }
                val dataVersion = serviceContext.getSharedPreferences(
                    PublicConfig.SharedPrefConf.NAME,
                    Context.MODE_PRIVATE
                ).getString(
                    PublicConfig.SharedPrefConf.KEY_DATA_LIBRARY_VERSION,
                    PublicConfig.KeyExtras.UNDEFINED_KEY
                )
                val oldConfParams = hashMapOf<String, String>(
                    PublicConfig.DataFileConf.APK_VERSION to "${BuildConfig.VERSION_CODE}",
                    PublicConfig.DataFileConf.DATA_VERSION to dataVersion
                )
                readAndFindingUpdates(
                    returnedObj as File,
                    oldConfParams
                )
                progressWeak.get()?.onCompleted(instanceClass, returnedObj)
            }

            override fun onFailure(instanceClass: Any?, message: Message?, e: Throwable?) {
                progressWeak.get()?.onFailure(instanceClass, message, e)
            }

        })
    }

    private fun readAndFindingUpdates(fileDataConf: File, oldConfigParams: HashMap<String, String>) {
        val readConfigUpdate: ReadConfigUpdateApi = ReadConfigData()
        readConfigUpdate.readConfig(fileDataConf, oldConfigParams, instanceServiceWeak.get())
        val possibleUpdate = readConfigUpdate.recommendUpdate()
        val applyUpdate: ApplyAnyUpdates
        var updateCalculations: HashMap<String, String?>? = null
        when (possibleUpdate) {
            true -> {
                applyUpdate = ApplyConfUpdate()
                updateCalculations = readConfigUpdate.calculateUpdate()
            }
            false -> applyUpdate = NotPossibleUpdate()
        }
        applyUpdate.applyUpdate(instanceServiceWeak.get(), updateCalculations)
        if (instanceServiceWeak.get() != null) {
            synchronized(instanceServiceWeak.get()!!) {
                instanceServiceWeak.get()!!.isTaskInterrupted = true
            }
            // stop service if done
            instanceServiceWeak.get()!!.stopSelf()
        }
    }
}