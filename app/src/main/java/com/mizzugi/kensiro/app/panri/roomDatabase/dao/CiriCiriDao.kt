package com.mizzugi.kensiro.app.panri.roomDatabase.dao

import androidx.room.Dao
import androidx.room.Query
import com.mizzugi.kensiro.app.panri.plugin.PublicContract
import com.mizzugi.kensiro.app.panri.roomDatabase.entity.CiriCiriEntity

@Dao
interface CiriCiriDao {
    companion object {
        private const val tableName = PublicContract.DatabaseContract.TABLE_CIRI_CIRI
    }

    @Query("SELECT * FROM $tableName")
    fun getAllCiri(): List<CiriCiriEntity>
}