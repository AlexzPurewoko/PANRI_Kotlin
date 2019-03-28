package id.kenshiro.app.panri.services.api

import android.content.Context

/**
 * This API is for internal usage only.
 *
 * Usage for : CheckScheduledUpdateService
 */
interface ApplyAnyUpdates {

    /**
     * Apply and write any update configuration into disks.
     * uses @param updateParams to read a config
     * Hence is a property of configuration file :
     *      updateDescription = ""
     *      updateVersion = ""
     *      fileToBeDownload = "" // full path into file in cloud
     *      pathFileConfig = ""
     *      oldVersion = ""
     *
     * @param ctx Current application Context
     * @param updateParams Selected parameters to be written
     *
     * @return void
     */
    fun applyUpdate(ctx: Context?, updateParams: HashMap<String, String?>?)
}