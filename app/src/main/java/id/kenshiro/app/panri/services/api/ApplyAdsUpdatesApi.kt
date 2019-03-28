package id.kenshiro.app.panri.services.api

import android.content.Context
import id.kenshiro.app.panri.api.ProgressApiCallback

/**
 * This API is for internal usage only.
 *
 * Usage for : AdsServices
 */
interface ApplyAdsUpdatesApi {

    /**
     * Select files to be downloaded '
     *
     * @param ctx Application Context
     * @param config Configuration Map
     * @noreturn
     */
    fun applyItemToBeDownloaded(ctx: Context?, config: HashMap<String, HashMap<String, String?>?>?)

    /**
     * update into local cache
     *
     * @param ctx Application Context
     * @param progress Callbacks
     */
    fun update(ctx: Context?, progress: ProgressApiCallback?)

    /**
     * Apply updates into cache (files)
     *
     * @param ctx Application Context
     */
    fun applyUpdate(ctx: Context?)
}