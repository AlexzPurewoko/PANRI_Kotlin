/**
 * Copyright @2019 Alexzander Purwoko Widiantoro <purwoko908@gmail.com> HOPEProjects
 *
 * This file is a part of PANRI Android Application
 * Except @author, you must not be able to modify, copying, change this file, because its closed source programs
 * This file is patented by any copyright law
 */

package id.kenshiro.app.panri.databases.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gambar_penyakit")
data class ListImgDisease(
    @PrimaryKey var no: Int,
    @ColumnInfo(name = "path_gambar") var pathImage: String?,
    @ColumnInfo(name = "count_img") var countAvailImg: String?
)