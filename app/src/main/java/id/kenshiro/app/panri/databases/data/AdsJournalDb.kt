package id.kenshiro.app.panri.databases.data

import androidx.room.Database
import androidx.room.RoomDatabase
import id.kenshiro.app.panri.databases.dao.AdsJournalDao
import id.kenshiro.app.panri.databases.entity.AdsJournalEntity

@Database(entities = [AdsJournalEntity::class], version = 1)
abstract class AdsJournalDb : RoomDatabase() {
    abstract fun getAdsJournalDao(): AdsJournalDao
}