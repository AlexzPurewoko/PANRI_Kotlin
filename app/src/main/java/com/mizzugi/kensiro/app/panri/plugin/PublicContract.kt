package com.mizzugi.kensiro.app.panri.plugin

object PublicContract {
    const val APP_IS_OLDER_VERSION = 0xfab
    const val APP_IS_SAME_VERSION = 0xfaf
    const val APP_IS_NEWER_VERSION = 0xf44
    const val APP_IS_FIRST_USAGE = 0xfaa
    const val DB_IS_FIRST_USAGE = 0xaac
    const val DB_REQUEST_UPDATE = 0xaffc
    const val DB_IS_NEWER_VERSION = 0xaab
    const val DB_IS_OLDER_IN_APP_VERSION = 0xaaf
    const val KEY_SHARED_DATA_CURRENT_IMG_NAVHEADER = "key_nav"
    const val SHARED_PREF_NAME = "panri_data"
    const val DB_IS_SAME_VERSION = 0xaca
    const val APP_CONDITION_KEY = "APP_CONDITION_KEY_EXTRAS"
    const val DB_CONDITION_KEY = "DB_CONDITION_KEY_EXTRAS"
    const val LIST_PENYAKIT_CIRI_KEY_CACHE = "key_ciri_data_penyakit"
    const val KEY_AUTOCHECKUPDATE_APPDATA = "enable_autocheck_update"

    // for identification of available updates DB
    const val UPDATE_DB_NOT_AVAILABLE_INTERNET_MISSING = 0x66
    const val UPDATE_DB_IS_AVAILABLE = 0x6A
    const val UPDATE_DB_NOT_AVAILABLE = 0x6b

    // FOR DATA VERSION
    const val KEY_DATA_LIBRARY_VERSION = "data_library_version"
    const val KEY_IKLAN_VERSION = "data_iklan_version"
    const val KEY_APP_VERSION = "app_version"
    const val KEY_LIST_VERSION_DB = "DB_LIST_VERSION_EXTRAS"
    const val KEY_VERSION_BOOL_NEW = "is_db_available_new_version"
    const val KEY_VERSION_ON_CLOUD = "db_version_on_cloud"

    // for Iklan
    const val FOLDER_IKLAN_CLOUD = "iklan"
    const val NAME_IKLAN_DATABASES = "database_iklan.db"
    const val NAME_IKLAN_CACHE_PATH = "iklan_cache"
    const val NUM_REQUEST_IKLAN_MODES = "NUM_REQUESTED_IKLAN"
    const val GET_ADS_MODE_START_SERVICE = "START_ADS_SERVICES"
    ////// MODE IKLAN
    const val IKLAN_MODE_START_SERVICE = 0xffa6
    const val IKLAN_MODE_GET_IKLAN = 0xffaa
    ////// PLACED ON
    const val ADS_PLACED_ON_MAIN = 0x0
    const val ADS_PLACED_ON_HOWTO = 0x1
    ////// iklan extra messages
    const val EXTRA_LIST_INFO_IKLAN = "DATA_IKLAN_EXTRAS"

    const val EXTRA_LIST_IKLAN_FILE_BYTES = "DATA_FILE_IKLAN_BYTE_EXTRAS"
    const val INTENT_BROADCAST_SEND_IKLAN = "id.kenshiro.app.panri.SEND_IKLAN_RESULTED"
    ////// method post iklan
    const val CALL_BY_WHATSAPP = 0x0
    const val CALL_BY_WEB = 0x1
    const val CALL_BY_TELEPHONE = 0x2
    const val CALL_BY_EMAIL = 0x3
    ////// name iklan on assets
    const val NAME_IKLAN_ON_ASSETS_PACK = "data_iklan.zip"

    object DatabaseContract {
        const val DATABASE_FAVORITE_NAME: String = "database_penyakitpadi.db"
        const val TABLE_CIRI_CIRI = "ciriciri"
        const val TABLE_IMAGE_DISEASE = "gambar_penyakit"
        const val TABLE_LIST_ID_IMAGE = "list_gambarid"
        const val TABLE_LIST_DISEASE = "penyakit"
    }
}