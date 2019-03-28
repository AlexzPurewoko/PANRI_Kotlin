package id.kenshiro.app.panri.services.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AdsData(
    val gifBytes: ByteArray?,
    val publisher: String?,
    val startAdsDate: String?,
    val adsName: String?,
    val urlLink: String?,
    val typeUrl: Int,
    val description: String?
) : Parcelable