package com.mizzugi.kensiro.app.panri.roomDatabase.dao

import androidx.room.Dao
import androidx.room.Query
import com.mizzugi.kensiro.app.panri.plugin.PublicContract
import com.mizzugi.kensiro.app.panri.roomDatabase.entity.ListPenyakit

@Dao
interface ListDiseaseDao {
    companion object {
        private const val tableName = PublicContract.DatabaseContract.TABLE_LIST_DISEASE
    }

    @Query("SELECT count(1) FROM $tableName as jumlah")
    fun dataCount(): Int

    @Query("SELECT * FROM $tableName")
    fun getAllDiseases(): List<ListPenyakit>

    @Query("SELECT nama FROM $tableName")
    fun getAllDiseasesName(): List<String>

    @Query("SELECT nama FROM $tableName WHERE num LIKE :keyId")
    fun getDiseaseNameFromKey(keyId: Int): String?

    @Query("SELECT latin FROM $tableName WHERE num LIKE :keyId")
    fun getDiseaseNameLatinFromKey(keyId: Int): String?

    @Query("SELECT * FROM $tableName WHERE num LIKE :keyId")
    fun getDiseaseFromKey(keyId: Int): ListPenyakit?
}