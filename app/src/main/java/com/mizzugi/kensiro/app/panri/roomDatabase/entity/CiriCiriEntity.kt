package com.mizzugi.kensiro.app.panri.roomDatabase.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.mizzugi.kensiro.app.panri.plugin.PublicContract

@Entity(tableName = PublicContract.DatabaseContract.TABLE_CIRI_CIRI)
data class CiriCiriEntity(

    @PrimaryKey
    @ColumnInfo(name = "num", typeAffinity = ColumnInfo.INTEGER)
    val num: Int,

    @ColumnInfo(name = "ciri", typeAffinity = ColumnInfo.TEXT)
    val ciri: String,

    @ColumnInfo(name = "usefirst", typeAffinity = ColumnInfo.INTEGER)
    val useFirst: Boolean,

    @ColumnInfo(name = "ask", typeAffinity = ColumnInfo.INTEGER)
    val ask: Boolean,

    @ColumnInfo(name = "listused", typeAffinity = ColumnInfo.TEXT)
    val listUsed: String?,

    @ColumnInfo(name = "pointo", typeAffinity = ColumnInfo.TEXT)
    val pointo: String?
) {
    @Ignore
    val listPointToFlags = mutableListOf<Int>()

    @Ignore
    val listUsedFlags = mutableListOf<Int>()

    @Ignore
    var listUsedModeFlags: ListUsedMode = ListUsedMode.MODE_SEQUENCE

    @Ignore
    fun setup() {
        listUsed?.apply {
            if (isEmpty()) return@apply
            var splitter = ','
            for (c in this) {
                if (c == '-') {
                    listUsedModeFlags = ListUsedMode.MODE_BIND
                    splitter = '-'
                    break
                } else if (c == ',') {
                    listUsedModeFlags = ListUsedMode.MODE_SEQUENCE
                    splitter = ','
                    break
                }
            }
            val strList = split(splitter)
            for (item in strList) {
                listUsedFlags.add(item.toInt())
            }
        }

        pointo?.apply {
            val strList = split(",")
            for (item in strList) {
                listPointToFlags.add(item.toInt())
            }
        }
    }

    enum class ListUsedMode {
        MODE_SEQUENCE,
        MODE_BIND
    }
}