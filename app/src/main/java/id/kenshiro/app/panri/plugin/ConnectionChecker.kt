/**
 * Copyright @2019 Alexzander Purwoko Widiantoro <purwoko908@gmail.com> HOPEProjects
 *
 * This file is a part of PANRI Android Application
 * Except @author, you must not be able to modify, copying, change this file, because its closed source programs
 * This file is patented by any copyright law
 */

package id.kenshiro.app.panri.plugin

import android.content.Context
import id.kenshiro.app.panri.api.CheckConnectionApi
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class ConnectionChecker : CheckConnectionApi {
    override fun isConnected(ctx: Context?, timeOut: Int, url: String): Boolean {
        if (ctx == null)
            throw Exception("Context cannot be null")
        var connected = false
        try {
            val buildedUrl = URL(url)
            val httpUrl = buildedUrl.openConnection() as HttpURLConnection
            httpUrl.setRequestProperty("User-Agent", "test")
            httpUrl.setRequestProperty("Connection", "close")
            httpUrl.connectTimeout = timeOut
            connected = httpUrl.responseCode == HttpURLConnection.HTTP_OK
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return connected

    }
}