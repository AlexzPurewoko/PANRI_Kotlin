/**
 * Copyright @2019 Alexzander Purwoko Widiantoro <purwoko908@gmail.com>
 *
 * Except @author, you must not be able to modify, copying, change this file, because its closed source programs
 * This file is patented by any copyright law
 */

package id.kenshiro.app.panri.api

import android.os.Message

/**
 * This class is used for managing communication between Tasks and main thread
 */
interface ProgressApiCallback {
    /**
     * Calls when the API implementation function is started
     * @param instanceClass An instance of implemented API
     * @noreturn
     */
    fun onStarted(instanceClass: Any?)

    /**
     * Called when any background task processing
     * Such as download media, and so on
     *
     * @param instanceClass An instance this Task
     * @param anyObject returned objects to this function
     * @param percent the progress of any task
     * @param isProcessing The status of Task process
     *
     * @noreturn
     */
    fun onProgress(instanceClass: Any?, anyObject: Any?, percent: Double, isProcessing: Boolean)

    /**
     * Called when the Task has been finished their jobs
     *
     * @param instanceClass An instance this Task
     * @param returnedObj Task result
     *
     * @noreturn
     */
    fun onCompleted(instanceClass: Any?, returnedObj: Any?)

    /**
     * Called when any exception or error occurs during tasks running.
     *
     * @param instanceClass An instance of this tasks
     * @param message The message of declared error
     * @param e Error Exception Value\
     *
     * @noreturn
     */
    fun onFailure(instanceClass: Any?, message: Message?, e: Throwable?)

}