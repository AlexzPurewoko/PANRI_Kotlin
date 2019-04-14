package id.kenshiro.app.panri.services.adsService.util

import com.mylexz.utils.PropertiesData
import id.kenshiro.app.panri.params.PublicConfig
import id.kenshiro.app.panri.services.api.ReadAndCompareAdsConfApi
import org.apache.commons.io.FileUtils
import java.io.File

class ConfigureAdsConf : ReadAndCompareAdsConfApi {

    private var newConf: HashMap<String, HashMap<String, String?>?>? = null
    private var oldConfig: HashMap<String, HashMap<String, String?>?>? = null
    private var fileOldConfig: File? = null
    private var fileNewConfig: File? = null

    override fun readConfig(oldConfig: File?, newConfig: File?) {
        if (newConfig == null) return
        val prop = PropertiesData(newConfig)
        prop.use {
            it.attach()
            newConf = it.getAllHashMap()?.clone() as HashMap<String, HashMap<String, String?>?>
        }
        PropertiesData(oldConfig!!).use {
            it.attach()
            this@ConfigureAdsConf.oldConfig = it.getAllHashMap()?.clone() as HashMap<String, HashMap<String, String?>?>
        }
        fileOldConfig = oldConfig
        fileNewConfig = newConfig

    }

    override fun isReqUpdate(): Boolean {
        if (oldConfig.isNullOrEmpty() || newConf.isNullOrEmpty()) return false
        val intOldVersion = getVersion(oldConfig!!)
        val intNewVersion = getVersion(newConf!!)
        return intOldVersion < intNewVersion
    }

    override fun getConfigProp(): HashMap<String, HashMap<String, String?>?>? {
        return newConf
    }

    override fun updateConfig() {
        if (fileNewConfig == null) return
        if (fileOldConfig == null) return
        FileUtils.deleteQuietly(fileOldConfig)
        FileUtils.moveFile(fileNewConfig, fileOldConfig)
    }

    fun getVersion(config: HashMap<String, HashMap<String, String?>?>): Int {
        val rootConfHmap = config[PublicConfig.AdsConfig.ROOT_CONFIG] ?: return 0
        val versionStr = rootConfHmap[PublicConfig.AdsConfig.PARAMS_VERSION] ?: return 0
        return versionStr.toInt()
    }
}