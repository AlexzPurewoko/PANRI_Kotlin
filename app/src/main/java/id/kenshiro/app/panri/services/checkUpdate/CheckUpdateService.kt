package id.kenshiro.app.panri.services.checkUpdate

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder

class CheckUpdateService : Service() {

    override fun onBind(intent: Intent): IBinder {
        return Binder()
    }

}
