package com.mizzugi.kensiro.app.panri.roomDatabase.dao

import androidx.room.Dao
import androidx.room.Query
import com.mizzugi.kensiro.app.panri.plugin.PublicContract
import com.mizzugi.kensiro.app.panri.roomDatabase.entity.GambarPenyakitEntity

@Dao
interface GambarPenyakitDao {
    companion object {
        private const val tableName = PublicContract.DatabaseContract.TABLE_IMAGE_DISEASE
    }

    @Query("SELECT count_img FROM $tableName WHERE num LIKE :keyId")
    fun getCountImageFrom(keyId: Int): Int

    @Query("SELECT path_gambar FROM $tableName WHERE num LIKE :keyId")
    fun getPathImageFrom(keyId: Int): String

    @Query("SELECT * FROM $tableName")
    fun getAllDiseaseImage(): List<GambarPenyakitEntity>

    @Query("SELECT count_img FROM $tableName")
    fun getAvailCountImage(): List<Int>

    @Query("SELECT path_gambar FROM $tableName")
    fun getAvailImage(): List<String>
}