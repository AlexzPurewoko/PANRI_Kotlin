/**
 * Copyright @2019 Alexzander Purwoko Widiantoro <purwoko908@gmail.com> HOPEProjects
 *
 * This file is a part of PANRI Android Application
 * Except @author, you must not be able to modify, copying, change this file, because its closed source programs
 * This file is patented by any copyright law
 */

package id.kenshiro.app.panri.api

import android.content.Context

interface CheckConnectionApi {
    @Throws(Exception::class)
    fun isConnected(ctx: Context?, timeOut: Int, url: String = "http://google.com"): Boolean
}