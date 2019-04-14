package id.kenshiro.app.panri.databases.dao

import androidx.room.Dao
import androidx.room.Query
import id.kenshiro.app.panri.databases.entity.AdsJournalEntity

@Dao
interface AdsJournalDao {
    @Query("SELECT * FROM journal_download WHERE state LIKE :state")
    fun getAllFilterTasks(state: String): List<AdsJournalEntity>

    @Query("SELECT * FROM journal_download")
    fun getAll(): List<AdsJournalEntity>

    @Query("INSERT INTO journal_download('state','file_to_download','file_to_save') VALUES ('PENDING',:fileToDownload, :fileToSave)")
    fun insertNewTasks(fileToDownload: String, fileToSave: String)

    @Query("UPDATE journal_download SET state=:state WHERE id_num LIKE :num_id")
    fun updateStateTask(num_id: Int, state: String)

    @Query("DELETE FROM journal_download WHERE id_num LIKE :num_id")
    fun removeTasks(num_id: Int)
}