package com.mizzugi.kensiro.app.panri.roomDatabase.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mizzugi.kensiro.app.panri.plugin.PublicContract

@Entity(tableName = PublicContract.DatabaseContract.TABLE_IMAGE_DISEASE)
data class GambarPenyakitEntity(
    @PrimaryKey
    @ColumnInfo(name = "num")
    val num: Int,

    @ColumnInfo(name = "path_gambar")
    val pathGambar: String,

    @ColumnInfo(name = "count_img")
    val countImg: Int
)