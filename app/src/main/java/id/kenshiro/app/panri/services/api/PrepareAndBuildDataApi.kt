package id.kenshiro.app.panri.services.api

import android.content.Context
import id.kenshiro.app.panri.services.data.AdsData

/**
 * This API is for internal usage only.
 *
 * Usage for : AdsServices
 */
interface PrepareAndBuildDataApi {

    /**
     * Read newConfig and oldConfig ads configuration.
     * That source file dir given from @param ctx, and
     * store it into field in sub-class
     *
     * @param ctx Application Context
     * @return void
     */
    fun readConfig(ctx: Context?)

    /**
     * This function is to ensure all data have been prepared.
     * Such as gif Images that could be move into another activity task
     * throught broadcastReceiver
     *
     * @param ctx Application Context
     * @param placeIn The place of ads for beeing placed
     * @return void
     */
    fun prepareData(ctx: Context?, placeIn: Int)

    /**
     * Build and pack the data into Array of AdsData Model
     *
     * @noparams
     * @return List<AdsData> the List of Ads Data
     */
    fun buildAndFinish(): List<AdsData>?
}