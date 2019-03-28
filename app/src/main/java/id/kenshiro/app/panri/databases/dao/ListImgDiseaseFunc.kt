/**
 * Copyright @2019 Alexzander Purwoko Widiantoro <purwoko908@gmail.com> HOPEProjects
 *
 * This file is a part of PANRI Android Application
 * Except @author, you must not be able to modify, copying, change this file, because its closed source programs
 * This file is patented by any copyright law
 */

package id.kenshiro.app.panri.databases.dao

import androidx.room.Dao
import androidx.room.Query
import id.kenshiro.app.panri.databases.entity.ListImgDisease

@Dao
interface ListImgDiseaseFunc {
    @Query("SELECT * FROM gambar_penyakit")
    fun getAll(): List<ListImgDisease>

    @Query("SELECT * FROM gambar_penyakit WHERE no LIKE (:id)")
    fun getByNum(id: Int): List<ListImgDisease>

    /*@Query("SELECT path_gambar FROM gambar_penyakit")
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    fun getAllPathImage(): List<ListImgDisease>*/
}