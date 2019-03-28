package id.kenshiro.app.panri.services.api

import android.content.Context
import id.kenshiro.app.panri.api.ProgressApiCallback

/**
 * This API is for internal usage only.
 *
 * Usage for : UpdateDataFromCloud
 */
interface ExecuteAllTasks {

    /**
     * Execute all tasks that given from @param listTasks and
     * return any progress into @param progress
     *
     * @param ctx Application Context
     * @param listTasks List of tasks downloaded file
     * @param progress An API Callbacks
     */
    fun executeAll(ctx: Context?, listTasks: Array<Runnable>, progress: ProgressApiCallback?)
}