/**
 * Copyright @2019 Alexzander Purwoko Widiantoro <purwoko908@gmail.com> HOPEProjects
 *
 * This file is a part of PANRI Android Application
 * Except @author, you must not be able to modify, copying, change this file, because its closed source programs
 * This file is patented by any copyright law
 */

package id.kenshiro.app.panri.params

import android.os.Process

object PublicConfig {

    object DataFileConf {
        const val ROOT_CONFIG = "RootConfig"
        const val MIN_APK_VERSION = "minApkVersion"
        const val DATA_VERSION = "dataVersion"
        const val APK_VERSION = "apkVersion"
        const val PARAMS_DESCRIPTION = "updateDescription"
        const val PARAMS_FILE_TO_DOWNLOAD = "fileToBeDownload"
        const val PARAMS_PATH_FILE_CONFIG = "pathFileConfig"
        const val DATA_CLOUD_PATH = "pathFolder"
        const val SUPPORTED_APK_VERSION = "supportedApkVersion"
        const val PARAMS_NAME_FILE_TO_DOWNLOAD = "nameFileToDownload"
    }

    object CloudConfig {
        const val DATA_CONF_FILE_NAME = "data_schedule_update.conf"
        const val AVAILADS_CONF_FILENAME = "available_ads.conf"
        const val AVAILADS_CONF_FILENAME_ALIAS = "temp_${CloudConfig.AVAILADS_CONF_FILENAME}"

    }
    object PathConfig {
        const val IMAGE_CACHE_PATHNAME = "images"
        const val OTA_PATCH_UPDATES_PATH = "ota_updates"
        const val ADS_FOLDER_NAME = "ads"
    }

    object Config {
        const val QUALITY_FACTOR_VIEWPAGER: Int = 40
        const val QUALITY_FACTOR_LIMGDISEASE: Int = QUALITY_FACTOR_VIEWPAGER
    }

    object Assets {
        const val DB_ASSET_NAME = "database_penyakitpadi.db"
        const val DB_ADS_JOURNAL_NAME = "ads_journal.db"
        const val GECKO_ASSETS_FONT = "Gecko_PersonalUseOnly.ttf"
        const val COMIC_SANS_MS3_FONT = "Comic_Sans_MS3.ttf"
        const val RIFFIC_BOLD_ASSETS_FONT = "RifficFree-Bold.ttf"
        const val DB_VERSION_ASSET_NAME = "db_version"
        const val ADS_VERSION_ASSET_NAME = "iklan_version"
        const val ASSET_RES_DATA_PATH = "data"
        const val ASSET_DATA_IMAGES = "$ASSET_RES_DATA_PATH/images"
        const val LIST_DISEASE_IMGPATH_CARD = "$ASSET_DATA_IMAGES/list"
    }

    object AppFlags {
        const val APP_IS_OLDER_VERSION = 0xfab
        const val APP_IS_SAME_VERSION = 0xfaf
        const val APP_IS_NEWER_VERSION = 0xf44
        const val APP_IS_FIRST_USAGE = 0xfaa
        const val DB_IS_FIRST_USAGE = 0xaac
        const val DB_REQUEST_UPDATE = 0xaffc
        const val DB_IS_NEWER_VERSION = 0xaab
        const val DB_IS_OLDER_IN_APP_VERSION = 0xaaf
        const val DB_IS_SAME_VERSION = 0xaca
        const val DB_REQUIRE_NEWER_APP_VERSION = 0xbbca
    }

    object KeyExtras {
        const val APP_CONDITION_KEY = "APP_CONDITION_KEY_EXTRAS"
        const val DB_CONDITION_KEY = "DB_CONDITION_KEY_EXTRAS"
        const val KEY_DATA_LIBRARY_VERSION = "data_library_version"
        const val KEY_ADS_VERSION = "data_iklan_version"
        const val KEY_AUTOCHECKUPDATE_APPDATA = "enable_autocheck_update"
        const val KEY_APP_VERSION = "app_version"
        const val UNDEFINED_KEY = "undefined"
    }

    object SharedPrefConf {
        const val NAME = "panri_data"
        const val KEY_APP_VERSION = KeyExtras.KEY_APP_VERSION
        const val KEY_DATA_LIBRARY_VERSION = KeyExtras.KEY_DATA_LIBRARY_VERSION
        const val KEY_ADS_VERSION = KeyExtras.KEY_ADS_VERSION
        const val KEY_AUTOCHECKUPDATE_APPDATA = KeyExtras.KEY_AUTOCHECKUPDATE_APPDATA
        const val KEY_SHARED_DATA_CURRENT_IMG_NAVHEADER = "key_nav"
        const val KEY_VERSION_BOOL_NEW = "is_db_available_new_version"
        const val KEY_DATA_VERSION_ON_CLOUD = "db_version_on_cloud"
        const val KEY_DB_REQUP_DESCRIPTION = DataFileConf.PARAMS_DESCRIPTION
        const val KEY_DB_REQUP_LIST_FILETODL = DataFileConf.PARAMS_FILE_TO_DOWNLOAD
        const val KEY_DB_REQUP_LIST_FILETYPE = DataFileConf.PARAMS_PATH_FILE_CONFIG
        const val KEY_DB_REQUP_LIST_FILENAME = DataFileConf.PARAMS_NAME_FILE_TO_DOWNLOAD


    }

    object ServiceThrPriority {
        const val ON_CHECKUPDATE_SERVICE = Process.THREAD_PRIORITY_BACKGROUND + Process.THREAD_PRIORITY_LESS_FAVORABLE
        const val ON_ADS_SERVICES = Process.THREAD_PRIORITY_BACKGROUND + Process.THREAD_PRIORITY_MORE_FAVORABLE
    }

    object AdsConfig {
        const val PLACE_ADS_ON_MAINACTIVITY = 0x5fea
        const val PLACE_ADS_ON_HOWTORESOLVE = 0xafa2
        const val ROOT_CONFIG = DataFileConf.ROOT_CONFIG
        const val PARAMS_PLACE_IN = "placedIn"
        const val PARAMS_START_ADS_DATE = "startAdsDate"
        const val PARAMS_END_ADS_DATE = "endAdsDate"
        const val PARAMS_ADS_NAME = "adsName"
        const val PARAMS_URL = "url"
        const val PARAMS_URL_TYPE = "urlType"
        const val PARAMS_IMAGES_PATH = "imagesPath"
        const val PARAMS_DESCRIPTION = "description"
        const val PARAMS_PUBLISHER = "publisher"
        const val PARAMS_DURATION = "duration"
        const val PARAMS_ICON_PUBLISHER = "iconPublisher"


        const val VALUE_ICON_PUBLISHER_NONE = "undefined"
        const val PARAMS_VERSION = "version"
    }
}