/**
 * Copyright @2019 Alexzander Purwoko Widiantoro <purwoko908@gmail.com> HOPEProjects
 *
 * This file is a part of PANRI Android Application
 * Except @author, you must not be able to modify, copying, change this file, because its closed source programs
 * This file is patented by any copyright law
 */

package id.kenshiro.app.panri.databases.data

import androidx.room.Database
import androidx.room.RoomDatabase
import id.kenshiro.app.panri.databases.dao.ListImgDiseaseFunc
import id.kenshiro.app.panri.databases.entity.ListImgDisease

@Database(entities = arrayOf(ListImgDisease::class), version = 1)
abstract class ListImgDiseaseDb : RoomDatabase() {
    abstract fun getListImgFunc(): ListImgDiseaseFunc
}