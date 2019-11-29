package com.mizzugi.kensiro.app.panri.roomDatabase.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mizzugi.kensiro.app.panri.plugin.PublicContract

@Entity(tableName = PublicContract.DatabaseContract.TABLE_LIST_ID_IMAGE)
data class ListGambarEntity(

    @PrimaryKey
    @ColumnInfo(name = "num")
    val num: Int,

    @ColumnInfo(name = "gambarid")
    val idGambar: String

)