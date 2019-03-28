/**
 * Copyright @2019 Alexzander Purwoko Widiantoro <purwoko908@gmail.com> HOPEProjects
 *
 * This file is a part of PANRI Android Application
 * Except @author, you must not be able to modify, copying, change this file, because its closed source programs
 * This file is patented by any copyright law
 */

package id.kenshiro.app.panri.params

object PublicConfig {

    object PathConfig {
        const val IMAGE_CACHE_PATHNAME = "images"
    }

    object Config {
        const val QUALITY_FACTOR_VIEWPAGER: Int = 40
        const val QUALITY_FACTOR_LIMGDISEASE: Int = QUALITY_FACTOR_VIEWPAGER
    }

    object Assets {
        const val DB_ASSET_NAME = "database_penyakitpadi.db"
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
        const val KEY_VERSION_ON_CLOUD = "db_version_on_cloud"
    }
}