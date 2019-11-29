package com.mizzugi.kensiro.app.panri.roomDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mizzugi.kensiro.app.panri.plugin.PublicContract
import com.mizzugi.kensiro.app.panri.roomDatabase.dao.CiriCiriDao
import com.mizzugi.kensiro.app.panri.roomDatabase.dao.GambarPenyakitDao
import com.mizzugi.kensiro.app.panri.roomDatabase.dao.ListDiseaseDao
import com.mizzugi.kensiro.app.panri.roomDatabase.dao.ListGambarIdDao
import com.mizzugi.kensiro.app.panri.roomDatabase.entity.CiriCiriEntity
import com.mizzugi.kensiro.app.panri.roomDatabase.entity.GambarPenyakitEntity
import com.mizzugi.kensiro.app.panri.roomDatabase.entity.ListGambarEntity
import com.mizzugi.kensiro.app.panri.roomDatabase.entity.ListPenyakit

@Database(
    entities = [CiriCiriEntity::class, GambarPenyakitEntity::class, ListGambarEntity::class, ListPenyakit::class],
    version = 1,
    exportSchema = false
)
abstract class PanriDatabase : RoomDatabase() {
    abstract fun ciriCiriDao(): CiriCiriDao
    abstract fun listImageDiseaseDao(): GambarPenyakitDao
    abstract fun listImageDiseaseIdDao(): ListGambarIdDao
    abstract fun listDiseaseDao(): ListDiseaseDao


    companion object {

        @Volatile
        private var instance: PanriDatabase? = null

        @JvmStatic
        fun getInstance(context: Context): PanriDatabase =
            instance ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PanriDatabase::class.java,
                    PublicContract.DatabaseContract.DATABASE_FAVORITE_NAME
                ).build()
                this.instance = instance
                return@synchronized instance
            }
    }
}