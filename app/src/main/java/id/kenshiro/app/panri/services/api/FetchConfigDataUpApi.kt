package id.kenshiro.app.panri.services.api

import android.content.Context
import id.kenshiro.app.panri.api.ProgressApiCallback

/**
 * This API is for internal usage only.
 *
 * Usage for : UpdateDataFromCloud
 */
interface FetchConfigDataUpApi {

    /**
     * Fetch config from a server, and store its value into sub-class field
     *
     * @param ctx Application context
     * @param progress API Progress Callbacks
     * @return void
     */
    fun fetchConfig(ctx: Context?, progress: ProgressApiCallback?)

    /**
     * Check Whether the data is possible to be update
     *
     * @noparams
     * @return true if possible and otherwise is false
     */
    fun possibleUpdate(): Boolean

    /**
     * Return a config that has been processed from @see fetchConfig()
     *
     * @noparams
     * @return null if any error occurs
     */
    fun getConfig(): HashMap<String, HashMap<String, String?>?>?
}