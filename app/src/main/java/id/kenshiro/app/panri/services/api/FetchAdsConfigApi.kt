package id.kenshiro.app.panri.services.api

import android.content.Context
import id.kenshiro.app.panri.api.ProgressApiCallback

/**
 * This API is for internal usage only.
 *
 * Usage for : AdsServices
 */
interface FetchAdsConfigApi {

    /**
     * Fetch Ads configuration from server.
     *
     * @param ctx application Context
     * @param progress Callbacks
     */
    fun fetchConfig(ctx: Context?, progress: ProgressApiCallback?)
}
