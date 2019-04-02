package id.kenshiro.app.panri.services.api

import android.content.Context
import java.io.File

/**
 * This API is for internal usage only.
 *
 * Usage for : CheckScheduledUpdateService
 */
interface ReadConfigUpdateApi {

    /**
     * Read a file configuration from cloud reserved
     * and from old configuration file. Then its params
     * will be stored into a field by sub-class that
     * implement this interface
     *
     * @param newConfig New config of File that given from internet
     * @param oldConfig Old configuration map : Overrides
     *      apkVersion = ""
     *      dataVersion = ""
     *
     *      These parameters could be added into map
     * @param ctx Application context
     *
     * @return void
     */
    fun readConfig(newConfig: File?, oldConfig: HashMap<String, String>, ctx: Context?)

    /**
     * Check whether data should be upgrade or not, and return
     * A Boolean, while @return true indicates this data should be upgrade
     * otherwise not.
     *
     * @noparams
     * @return true if data is should be upgraded
     * @return false if data cannot be upgrade because of some reason
     */
    fun recommendUpdate(): Boolean

    /**
     * Calculate the files that should be download and update,
     * and also configuration
     *
     * @noparam
     * @return null if any Npe
     */
    fun calculateUpdate(): HashMap<String, String?>?
}