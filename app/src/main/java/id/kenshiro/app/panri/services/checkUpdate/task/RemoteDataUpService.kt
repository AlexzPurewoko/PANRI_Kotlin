package id.kenshiro.app.panri.services.checkUpdate.task

import android.app.ActivityManager
import android.content.Context
import android.os.Process
import id.kenshiro.app.panri.params.PublicConfig
import id.kenshiro.app.panri.services.checkUpdate.CheckUpdateService
import java.lang.ref.WeakReference

// used for checking an activity lifecycle usage
// and if app is killed, this Task will be detect and interrupt all threads
class RemoteDataUpService(instanceService: CheckUpdateService) : Runnable {

    private val instanceWeakService = WeakReference<CheckUpdateService>(instanceService)
    override fun run() {
        Process.setThreadPriority(PublicConfig.ServiceThrPriority.ON_CHECKUPDATE_SERVICE + 2)
        while (!Thread.interrupted()) {
            val instanceService = instanceWeakService.get() ?: break
            if (!isAppRunning()) {
                synchronized(instanceService.isTaskInterrupted) {
                    instanceService.isTaskInterrupted = true
                }
                // wait a thread for preparing to stop and stop it!
                Thread.sleep(2500)
                instanceService.stopSelf()
                break
            }
            Thread.sleep(500)
        }
    }

    private fun isAppRunning(): Boolean {
        val service = instanceWeakService.get() ?: return false
        val manager = service.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningTaskInfo = manager.getRunningTasks(10)
        for (componentInfo in runningTaskInfo) {
            val infoComponent = componentInfo.topActivity
            if (infoComponent.packageName == service.packageName) return true
        }
        return false
    }
}