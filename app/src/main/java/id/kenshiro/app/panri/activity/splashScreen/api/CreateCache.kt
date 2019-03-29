/**
 * Copyright @2019 Alexzander Purwoko Widiantoro <purwoko908@gmail.com> HOPEProjects
 *
 * This file is a part of PANRI Android Application
 * Except @author, you must not be able to modify, copying, change this file, because its closed source programs
 * This file is patented by any copyright law
 */

package id.kenshiro.app.panri.activity.splashScreen.api

import android.content.Context
import com.mylexz.utils.SimpleDiskLruCache
import java.io.File
import java.io.IOException

interface CreateCache {
    @Throws(IOException::class)
    fun create(dir: File, otaUpdatesDisk: File, ctx: Context?, diskCache: SimpleDiskLruCache)
}