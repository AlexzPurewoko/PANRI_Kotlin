package id.kenshiro.app.panri.services.adsService.util

import android.content.Context
import com.mylexz.utils.PropertiesData
import id.kenshiro.app.panri.params.PublicConfig
import id.kenshiro.app.panri.services.api.PrepareAndBuildDataApi
import id.kenshiro.app.panri.services.data.AdsData
import java.io.File
import java.io.FileInputStream
import java.text.SimpleDateFormat
import java.util.*

class PrepareBuildDataForNext : PrepareAndBuildDataApi {

    private var map: HashMap<String, HashMap<String, String?>?>? = null
    private val listAdsData: MutableList<AdsData> = mutableListOf()
    override fun readConfig(ctx: Context?) {
        if (ctx == null) return
        val fileNewConfig = File(ctx.filesDir, PublicConfig.CloudConfig.AVAILADS_CONF_FILENAME)
        val propertiesData = PropertiesData(fileNewConfig)
        propertiesData.use {
            it.attach()
            val returnedHashMap = it.getAllHashMap() ?: return
            map = returnedHashMap.clone() as HashMap<String, HashMap<String, String?>?>?
        }
    }

    override fun prepareData(ctx: Context?, placeIn: Int) {
        // basic checking
        if (map == null) return
        if (ctx == null) return

        // if placeIn is not in PLACE_ADS_ON_MAINACTIVITY and PLACE_ADS_ON_HOWTORESOLVE return is necessary
        // because to ensure the ads isn't placed into another place except above
        if (placeIn != PublicConfig.AdsConfig.PLACE_ADS_ON_MAINACTIVITY && placeIn != PublicConfig.AdsConfig.PLACE_ADS_ON_HOWTORESOLVE) return

        // initialize all params
        val paramsPlacedIn = PublicConfig.AdsConfig.PARAMS_PLACE_IN
        val paramsStartAdsDate = PublicConfig.AdsConfig.PARAMS_START_ADS_DATE
        val paramsEndAdsDate = PublicConfig.AdsConfig.PARAMS_END_ADS_DATE
        val PARAMS_ADS_NAME = PublicConfig.AdsConfig.PARAMS_ADS_NAME
        val PARAMS_URL = PublicConfig.AdsConfig.PARAMS_URL
        val PARAMS_URL_TYPE = PublicConfig.AdsConfig.PARAMS_URL_TYPE
        val PARAMS_IMAGES_PATH = PublicConfig.AdsConfig.PARAMS_IMAGES_PATH
        val PARAMS_DESCRIPTION = PublicConfig.AdsConfig.PARAMS_DESCRIPTION
        val PARAMS_PUBLISHER = PublicConfig.AdsConfig.PARAMS_PUBLISHER
        val PARAMS_DURATION = PublicConfig.AdsConfig.PARAMS_DURATION
        val PARAMS_ICON_PUBLISHER = PublicConfig.AdsConfig.PARAMS_ICON_PUBLISHER
        val adsFilePath = File(ctx.filesDir, PublicConfig.PathConfig.ADS_FOLDER_NAME)
        adsFilePath.mkdir()

        // select the target of adsID to be show
        // by placeIn property and ensure that this ads is not deprecated
        // get the list of key
        val keys = map!!.keys

        // iterate it with loops
        for (key in keys) {
            // if its reached RootConfig, then will continue
            if (key == PublicConfig.AdsConfig.ROOT_CONFIG) continue

            // select the configuration of this adsID
            val adsMapConf = map!![key] ?: continue

            // if placeIn parameter is not equal with this ads configuration
            // then will be continue to other keys
            val adsPlacedIn = adsMapConf[paramsPlacedIn]!!.toInt()
            if (adsPlacedIn != placeIn) continue

            // compute the Date whereused, check if its Ads is potentially deprecated
            // if its deprecated then will go next key
            val endAdsDate = adsMapConf[paramsEndAdsDate]
            if (isAdsDeprecated(endAdsDate)) continue

            // extract all necessary config for this ads
            val description = adsMapConf[PARAMS_DESCRIPTION]
            val startAdsDate = adsMapConf[paramsStartAdsDate]
            val adsName = adsMapConf[PARAMS_ADS_NAME]
            val adsUrl = adsMapConf[PARAMS_URL]
            val adsImagePath = adsMapConf[PARAMS_IMAGES_PATH]
            val adsIconPublisher = adsMapConf[PARAMS_ICON_PUBLISHER]
            val adsPublisher = adsMapConf[PARAMS_PUBLISHER]
            val adsDuration = adsMapConf[PARAMS_DURATION]
            val adsTypeUrl = adsMapConf[PARAMS_URL_TYPE]!!.toInt()

            // gets the byte array of gif image
            val imgStream = FileInputStream(File(adsFilePath, adsImagePath))
            val byteArray = ByteArray(imgStream.available())
            imgStream.read(byteArray)
            imgStream.close()

            // compose into the AdsData
            val adsData = AdsData(
                byteArray,
                File(adsFilePath, "$key/$adsIconPublisher").absolutePath,
                adsPublisher,
                startAdsDate,
                adsName,
                adsUrl,
                description,
                adsDuration,
                adsTypeUrl,
                adsPlacedIn
            )

            // push up into the list
            listAdsData.add(adsData)
        }


    }

    override fun buildAndFinish(): List<AdsData>? {
        return listAdsData
    }

    private fun isAdsDeprecated(endAdsDate: String?, datePattern: String = "dd/MM/yyyy"): Boolean {
        val sdf = SimpleDateFormat(datePattern, Locale.getDefault()).parse(endAdsDate)
        val endDate = Calendar.getInstance()
        endDate.time = sdf
        val endDay = endDate.get(Calendar.DAY_OF_MONTH)
        val endMonth = endDate.get(Calendar.MONTH)
        val endYear = endDate.get(Calendar.YEAR)

        // gets time now
        val timeNow = Calendar.getInstance(TimeZone.getDefault())
        timeNow.time = Date(System.currentTimeMillis())
        val dayNow = timeNow.get(Calendar.DAY_OF_MONTH)
        val monthNow = timeNow.get(Calendar.MONTH)
        val yearNow = timeNow.get(Calendar.YEAR)

        // comparing the date
        return (yearNow < endYear) && (monthNow < endMonth) && (dayNow <= endDay)
    }
}