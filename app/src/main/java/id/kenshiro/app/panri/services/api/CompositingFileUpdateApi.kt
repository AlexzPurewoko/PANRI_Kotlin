package id.kenshiro.app.panri.services.api

import android.content.Context

/**
 * This API is for internal usage only.
 *
 * Usage for : UpdateDataFromCloud
 */
interface CompositingFileUpdateApi {

    /**
     * List a file that could to be downloaded
     *
     * @param ctx Application Context
     * @param params Loaded Configuration file
     * @noreturn
     */
    fun composeFile(ctx: Context?, params: HashMap<String, HashMap<String, String?>>)

    /**
     * Arrange a tasks that should be run on a threadpool or etc
     *
     * @noparams
     * @return array of Runnable Tasks
     */
    fun arrangeUpdateTasks(): Array<Runnable>
}