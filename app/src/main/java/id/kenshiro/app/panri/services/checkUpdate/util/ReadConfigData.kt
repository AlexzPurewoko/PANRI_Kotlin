package id.kenshiro.app.panri.services.checkUpdate.util

import android.content.Context
import com.mylexz.utils.PropertiesData
import id.kenshiro.app.panri.BuildConfig
import id.kenshiro.app.panri.params.PublicConfig
import id.kenshiro.app.panri.services.api.ReadConfigUpdateApi
import java.io.File

// This class is untested!!!

class ReadConfigData : ReadConfigUpdateApi {


    private var map: HashMap<String, HashMap<String, String?>?>? = null
    private var oldConfig: HashMap<String, String>? = null
    private var newVersion: Int = 0
    override fun readConfig(newConfig: File?, oldConfig: HashMap<String, String>, ctx: Context?) {
        if (newConfig == null) return
        val prop = PropertiesData(newConfig)
        prop.attach()
        this.map = prop.getAllHashMap()?.clone() as HashMap<String, HashMap<String, String?>?>
        this.oldConfig = oldConfig
        prop.close()
    }

    override fun recommendUpdate(): Boolean {
        if (map == null || map!!.isEmpty()) return false
        // first checking minApkVersion on the root config
        val rootConfig = PublicConfig.DataFileConf.ROOT_CONFIG
        val minApkVersionParams = PublicConfig.DataFileConf.MIN_APK_VERSION
        val supportedApkVersionParams = PublicConfig.DataFileConf.SUPPORTED_APK_VERSION
        val supportedMinApkVersion = map!![rootConfig]!![minApkVersionParams]!!.toInt()

        // if this supported apk version is greater than this apk version,
        // will return false, because any features doesn't support of this apk version
        if (supportedMinApkVersion > BuildConfig.VERSION_CODE) return false

        // observing data

        for (key in map!!.keys) {
            if (key == rootConfig) continue
            val dataVersion = key.toInt()
            val selectedHashMap = map!![key]!!
            val minSupportedApkVersion = selectedHashMap[supportedApkVersionParams]!!
            val supportedApkVersion = minSupportedApkVersion.toInt()
            if (supportedApkVersion <= BuildConfig.VERSION_CODE && dataVersion > oldConfig!![PublicConfig.DataFileConf.DATA_VERSION]!!.toInt() && dataVersion > newVersion) {
                newVersion = dataVersion
            }
        }

        return newVersion != 0

    }

    /**
     * Calculates all necessary files/package to be downloaded from server
     *
     * @noparam
     * @return hashMap {
     *      PublicConfig.DataFileConf.PARAMS_DESCRIPTION               -> The Description of upgradable contents            (String) <list of configuration, separated with (,)>
     *      PublicConfig.DataFileConf.PARAMS_FILE_TO_DOWNLOAD          -> List all absolute path on the server              (String) <list of configuration, separated with (,)>
     *      PublicConfig.DataFileConf.PARAMS_PATH_FILE_CONFIG          -> List all of file types                            (String) <list of configuration, separated with (,)>
     *      PublicConfig.DataFileConf.PARAMS_NAME_FILE_TO_DOWNLOAD     -> List of all filename that have been downloaded    (String) <list of configuration, separated with (,)>
     *      PublicConfig.DataFileConf.DATA_VERSION                     -> The new Version of Data that have been downloaded (String)
     * }
     */
    override fun calculateUpdate(): HashMap<String, String?>? {
        if (newVersion == 0) return null

        val paramsDescription = PublicConfig.DataFileConf.PARAMS_DESCRIPTION
        val paramsFileToBeDownload = PublicConfig.DataFileConf.PARAMS_FILE_TO_DOWNLOAD
        val paramsPathFileConfig = PublicConfig.DataFileConf.PARAMS_PATH_FILE_CONFIG
        val paramsFileNameToDL: String = PublicConfig.DataFileConf.PARAMS_NAME_FILE_TO_DOWNLOAD
        val paramsDataPathFolder = PublicConfig.DataFileConf.DATA_CLOUD_PATH
        val rootConfig = PublicConfig.DataFileConf.ROOT_CONFIG

        val dataOldVersion = oldConfig!![PublicConfig.DataFileConf.DATA_VERSION]!!.toInt()
        val description = map!!["$newVersion"]!![paramsDescription]
        val listFiles = HashMap<Int, MutableList<String>?>()
        val listTypes = HashMap<Int, MutableList<String>?>()
        var currPointerVersion: Int

        // select all necessary downloaded files
        for (key in map!!.keys) {
            if (key == rootConfig) continue
            currPointerVersion = key.toInt()
            if (currPointerVersion in (dataOldVersion + 1)..newVersion) {
                val filesInThisVersion = map!![key]!![paramsFileToBeDownload]!!.split(",").toMutableList()
                val typeFilesNow = map!![key]!![paramsPathFileConfig]!!.split(",").toMutableList()
                if (listTypes.size == 0) {
                    listTypes[currPointerVersion] = typeFilesNow
                    listFiles[currPointerVersion] = filesInThisVersion
                    continue
                }
                for (version in listFiles.keys) {
                    // scan if any multiple files
                    val listFilePrevVersion = listFiles[version] ?: continue
                    val typeFilesPrev = listTypes[version] ?: continue

                    var indexNow = 0
                    var isFilesNowPopped = false
                    while (indexNow < filesInThisVersion.size) {
                        val fileInNowVersion = filesInThisVersion[indexNow]

                        var indexPrev = 0
                        while (indexPrev < listFilePrevVersion.size) {
                            val fileInPrevVersion = listFilePrevVersion[indexPrev]
                            // if same, then will be replaced with newer version
                            if (fileInNowVersion.equals(fileInPrevVersion, ignoreCase = false)) {
                                if (currPointerVersion > version) {
                                    typeFilesPrev.removeAt(indexPrev)
                                    listFilePrevVersion.removeAt(indexPrev)
                                } else {
                                    typeFilesNow.removeAt(indexNow)
                                    filesInThisVersion.removeAt(indexNow)
                                    isFilesNowPopped = true
                                    break
                                }
                            }
                            indexPrev++
                        }
                        if (!isFilesNowPopped) indexNow++
                        else {
                            indexNow = 0
                            isFilesNowPopped = false
                        }
                    }


                }
                // add into memory
                // if not empty, then will add into hashmap
                if (!filesInThisVersion.isNullOrEmpty()) {
                    listFiles[currPointerVersion] = filesInThisVersion
                    listTypes[currPointerVersion] = typeFilesNow
                }
            }
        }

        // compose a List of Fullpath of File
        val rootConfigHashMap = map!![rootConfig]!!
        val listPathFile: String = composePath(listTypes, listFiles, rootConfigHashMap[paramsDataPathFolder]!!)
        val listTypeStr: String = composeTypes(listTypes)
        val listAllFile: String = composeFiles(listFiles)

        return hashMapOf(
            paramsDescription to description,
            paramsFileToBeDownload to listPathFile,
            paramsPathFileConfig to listTypeStr,
            paramsFileNameToDL to listAllFile,
            PublicConfig.DataFileConf.DATA_VERSION to "$newVersion"
        )

    }

    private fun composeFiles(listFiles: HashMap<Int, MutableList<String>?>): String {
        val sbuf = StringBuffer()
        for (key in listFiles.keys) {
            val values = listFiles[key]!!
            for (str in values) {
                sbuf.append(str)
                sbuf.append(",")
            }
        }
        sbuf.deleteCharAt(sbuf.length - 1)
        return sbuf.toString()
    }

    private fun composeTypes(listTypes: HashMap<Int, MutableList<String>?>): String {
        val sbuf = StringBuffer()
        for (key in listTypes.keys) {
            val values = listTypes[key]!!
            for (str in values) {
                sbuf.append(str)
                sbuf.append(",")
            }
        }
        sbuf.deleteCharAt(sbuf.length - 1)
        return sbuf.toString()
    }

    private fun composePath(
        listTypes: HashMap<Int, MutableList<String>?>,
        listFiles: HashMap<Int, MutableList<String>?>,
        dataPathFolder: String
    ): String {
        val sbuf = StringBuffer()
        for (key in listFiles.keys) {
            val listsFile = listFiles[key] ?: continue
            val listsTypes = listTypes[key] ?: continue
            for ((index, file) in listsFile.withIndex()) {
                val types = listsTypes[index]
                sbuf.append("$dataPathFolder/$key/$types/$file,")
            }
        }
        sbuf.deleteCharAt(sbuf.length - 1)
        return sbuf.toString()
    }
}