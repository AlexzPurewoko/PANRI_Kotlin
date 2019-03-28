package id.kenshiro.app.panri.services.api

import android.content.Context
import id.kenshiro.app.panri.api.ProgressApiCallback

/**
 * This class for internal usage only.
 *
 * Usage for check an update data
 */
interface CheckUpdateApi {

    /**
     * Check an update of data application
     * That download configuration file from server
     * and directly write into disk, @param progress used
     * when gathering the download process of configuration file
     *
     * @param ctx Context for this application.
     * @param progress Callbacks for gathering download process
     */
    fun checkUpdate(ctx: Context?, progress: ProgressApiCallback)

}