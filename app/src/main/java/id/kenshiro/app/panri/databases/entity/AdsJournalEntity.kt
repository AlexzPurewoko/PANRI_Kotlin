package id.kenshiro.app.panri.databases.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "journal_download")
data class AdsJournalEntity(
    @PrimaryKey val id_num: Int,
    @ColumnInfo(name = "state") val state: String?,
    @ColumnInfo(name = "file_to_download") val fileToDownload: String?,
    @ColumnInfo(name = "file_to_save") val fileToSave: String?
)