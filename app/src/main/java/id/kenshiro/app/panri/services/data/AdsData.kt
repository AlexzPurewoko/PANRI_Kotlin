package id.kenshiro.app.panri.services.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AdsData(
    val gifBytes: ByteArray?,
    val iconPublisherPath: String?,
    val publisher: String?,
    val startAdsDate: String?,
    val adsName: String?,
    val urlLink: String?,
    val description: String?,
    val duration: String?,
    val typeUrl: Int,
    val placedIn: Int
) : Parcelable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AdsData

        if (gifBytes != null) {
            if (other.gifBytes == null) return false
            if (!gifBytes.contentEquals(other.gifBytes)) return false
        } else if (other.gifBytes != null) return false
        if (iconPublisherPath != other.iconPublisherPath) return false
        if (publisher != other.publisher) return false
        if (startAdsDate != other.startAdsDate) return false
        if (adsName != other.adsName) return false
        if (urlLink != other.urlLink) return false
        if (description != other.description) return false
        if (duration != other.duration) return false
        if (typeUrl != other.typeUrl) return false
        if (placedIn != other.placedIn) return false

        return true
    }

    override fun hashCode(): Int {
        var result = gifBytes?.contentHashCode() ?: 0
        result = 31 * result + (iconPublisherPath?.hashCode() ?: 0)
        result = 31 * result + (publisher?.hashCode() ?: 0)
        result = 31 * result + (startAdsDate?.hashCode() ?: 0)
        result = 31 * result + (adsName?.hashCode() ?: 0)
        result = 31 * result + (urlLink?.hashCode() ?: 0)
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + (duration?.hashCode() ?: 0)
        result = 31 * result + typeUrl
        result = 31 * result + placedIn
        return result
    }
}