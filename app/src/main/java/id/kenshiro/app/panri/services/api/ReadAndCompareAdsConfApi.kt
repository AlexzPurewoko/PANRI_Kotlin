package id.kenshiro.app.panri.services.api

import java.io.File

/**
 * This API is for internal usage only.
 *
 * Usage for : AdsServices
 */
interface ReadAndCompareAdsConfApi {

    /**
     * Reads the configuration file and store its value into
     * sub-class fields
     *
     * @param oldConfig old Configuration File
     * @param newConfig new Configuration File
     * @return void
     */
    fun readConfig(oldConfig: File?, newConfig: File?)

    /**
     * Compare the version of newConfig and oldConfig
     * if its same return true, otherwise false
     *
     * @return true if same false if not
     */
    fun isSameVersion(): Boolean

    /**
     * Get configuration from a sub-class field.
     *
     * @return null if any error occurs, otherwise return a HasMap
     */
    fun getConfigProp(): HashMap<String, HashMap<String, String?>?>?

    /**
     * Update config files directly into place where oldConfig come in
     * @noreturn
     */
    fun updateConfig()
}