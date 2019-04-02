package id.kenshiro.app.panri.services.checkUpdate.util

import android.content.Context
import id.kenshiro.app.panri.BuildConfig
import id.kenshiro.app.panri.params.PublicConfig
import id.kenshiro.app.panri.services.api.ReadConfigUpdateApi
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.io.File

class ReadConfigDataTest {

    companion object {
        const val DATA_DESCRIPTION = "This update doesn't include any description"
        const val FILE_PATH_TODL =
            "Data/20181212/images/mas_roman.jpg,Data/20181212/images/img.jpg,Data/20190123/html/list_diseases.html,Data/20190123/database/database.db,Data/20190123/html/page.html"
        const val TYPE_FILE_HASHMAP = "images,images,html,database,html"
        const val LIST_ALL_FILENAMES = "mas_roman.jpg,img.jpg,list_diseases.html,database.db,page.html"
        const val NEWEST_VERSION = "20190123"
    }

    val dataVersion: String = "20181115"
    val oldConfig: HashMap<String, String> = hashMapOf(
        PublicConfig.DataFileConf.APK_VERSION to "${BuildConfig.VERSION_CODE}",
        PublicConfig.DataFileConf.DATA_VERSION to dataVersion
    )

    @Mock
    private lateinit var ctx: Context

    private lateinit var file: File

    @Before
    fun set() {
        MockitoAnnotations.initMocks(this)
        file = File(PublicConfig.CloudConfig.DATA_CONF_FILE_NAME)
    }

    @Test
    fun runAllTest() {
        val readConfigData: ReadConfigUpdateApi = ReadConfigData()
        readConfigData.readConfig(file, oldConfig, ctx)
        assertEquals(true, readConfigData.recommendUpdate())
        val calculateUpdate = readConfigData.calculateUpdate()!!

        val paramsDescription = PublicConfig.DataFileConf.PARAMS_DESCRIPTION
        val paramsFileToBeDownload = PublicConfig.DataFileConf.PARAMS_FILE_TO_DOWNLOAD
        val paramsPathFileConfig = PublicConfig.DataFileConf.PARAMS_PATH_FILE_CONFIG
        val paramsFileNameToDL: String = PublicConfig.DataFileConf.PARAMS_NAME_FILE_TO_DOWNLOAD

        assertEquals(calculateUpdate[paramsDescription], DATA_DESCRIPTION)
        assertEquals(calculateUpdate[paramsFileToBeDownload], FILE_PATH_TODL)
        assertEquals(calculateUpdate[paramsPathFileConfig], TYPE_FILE_HASHMAP)
        assertEquals(calculateUpdate[paramsFileNameToDL], LIST_ALL_FILENAMES)
        assertEquals(calculateUpdate[PublicConfig.DataFileConf.DATA_VERSION], NEWEST_VERSION)
    }
}