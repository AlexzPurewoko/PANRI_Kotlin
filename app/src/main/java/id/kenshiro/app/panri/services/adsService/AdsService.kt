package id.kenshiro.app.panri.services.adsService

import android.app.Service
import android.content.Intent
import android.os.IBinder
import java.util.concurrent.locks.ReentrantLock

class AdsService : Service() {

    internal val resourceLock: ReentrantLock = ReentrantLock()
    internal var isInterrupted: Boolean = false
    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }
}
