package com.mizzugi.kensiro.app.panri.roomDatabase.dao

import androidx.room.Dao
import androidx.room.Query
import com.mizzugi.kensiro.app.panri.plugin.PublicContract
import com.mizzugi.kensiro.app.panri.roomDatabase.entity.ListGambarEntity

@Dao
interface ListGambarIdDao {
    companion object {
        private const val tableName = PublicContract.DatabaseContract.TABLE_LIST_ID_IMAGE
    }

    @Query("SELECT * FROM $tableName")
    fun getAllListImageID(): List<ListGambarEntity>

    @Query("SELECT gambarid FROM $tableName WHERE num LIKE :keyID")
    fun getImageIDFromPosition(keyID: Int): String
}