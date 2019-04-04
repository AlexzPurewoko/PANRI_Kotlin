package id.kenshiro.app.panri.services.checkUpdate.util

import android.content.Context
import androidx.core.content.edit
import id.kenshiro.app.panri.params.PublicConfig
import id.kenshiro.app.panri.services.api.ApplyAnyUpdates

class ApplyConfUpdate : ApplyAnyUpdates {
    override fun applyUpdate(ctx: Context?, updateParams: HashMap<String, String?>?) {
        if (ctx == null || updateParams == null) return
        ctx.getSharedPreferences(PublicConfig.SharedPrefConf.NAME, Context.MODE_PRIVATE).edit {
            val paramsDescription = PublicConfig.DataFileConf.PARAMS_DESCRIPTION
            val paramsFileToBeDownload = PublicConfig.DataFileConf.PARAMS_FILE_TO_DOWNLOAD
            val paramsPathFileConfig = PublicConfig.DataFileConf.PARAMS_PATH_FILE_CONFIG
            val paramsFileNameToDL: String = PublicConfig.DataFileConf.PARAMS_NAME_FILE_TO_DOWNLOAD

            val description = updateParams[paramsDescription]
            val fileToDL = updateParams[paramsFileToBeDownload]
            val fileType = updateParams[paramsPathFileConfig]
            val fileName = updateParams[paramsFileNameToDL]
            val dataNewestVersion = updateParams[PublicConfig.DataFileConf.DATA_VERSION]
            putString(PublicConfig.SharedPrefConf.KEY_DB_REQUP_DESCRIPTION, description)
            putString(PublicConfig.SharedPrefConf.KEY_DB_REQUP_LIST_FILETODL, fileToDL)
            putString(PublicConfig.SharedPrefConf.KEY_DB_REQUP_LIST_FILETYPE, fileType)
            putString(PublicConfig.SharedPrefConf.KEY_DB_REQUP_LIST_FILENAME, fileName)
            putString(PublicConfig.SharedPrefConf.KEY_DATA_VERSION_ON_CLOUD, dataNewestVersion)
            putInt(PublicConfig.SharedPrefConf.KEY_VERSION_BOOL_NEW, PublicConfig.AppFlags.DB_REQUEST_UPDATE)

        }
    }

}