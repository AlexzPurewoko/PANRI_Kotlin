package id.kenshiro.app.panri.services.checkUpdate

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.os.Message
import android.util.Log
import com.crashlytics.android.Crashlytics
import id.kenshiro.app.panri.api.ProgressApiCallback
import id.kenshiro.app.panri.services.checkUpdate.task.CheckDataTask
import id.kenshiro.app.panri.services.checkUpdate.task.RemoteDataUpService
import io.fabric.sdk.android.Fabric
import java.lang.ref.WeakReference

class CheckUpdateService : Service() {

    private lateinit var remoteService: Thread
    private lateinit var checkDataTask: Thread

    internal var isTaskInterrupted: Boolean = false
    override fun onBind(intent: Intent): IBinder {
        return Binder()
    }

    override fun onCreate() {
        super.onCreate()
        Fabric.with(this)
        remoteService = Thread(RemoteDataUpService(this), "RemoteDataUpdaterService")
        checkDataTask = Thread(CheckDataTask(this, ProgressTaskCallback(this)), "CheckDataUpdateTask")
        remoteService.start()
        checkDataTask.start()
    }

    override fun onDestroy() {
        if (remoteService.isAlive)
            remoteService.interrupt()
        if (checkDataTask.isAlive)
            checkDataTask.interrupt()
        super.onDestroy()
    }

    inner class ProgressTaskCallback(instanceService: CheckUpdateService) : ProgressApiCallback {
        private val instanceServiceWeak = WeakReference<CheckUpdateService>(instanceService)
        override fun onStarted(instanceClass: Any?) {

        }

        override fun onProgress(instanceClass: Any?, anyObject: Any?, percent: Double, isProcessing: Boolean) {

        }

        override fun onCompleted(instanceClass: Any?, returnedObj: Any?) {

        }

        override fun onFailure(instanceClass: Any?, message: Message?, e: Throwable?) {
            val serviceInstance = instanceServiceWeak.get() ?: return
            val logError = "Cause : ${e?.cause} -> ${e?.message}, ERR Code : ${message?.what}"
            Log.e("CheckUpdateService", logError)
            Crashlytics.setString("CheckUpdateServiceError", logError)
            Crashlytics.logException(e)

            synchronized(serviceInstance.isTaskInterrupted) {
                serviceInstance.isTaskInterrupted = true
            }
            serviceInstance.stopSelf()
        }

    }
}
