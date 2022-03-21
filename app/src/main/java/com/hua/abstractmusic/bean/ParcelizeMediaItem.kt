package com.hua.abstractmusic.bean

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Parcelable
import androidx.media3.common.MediaItem
import androidx.navigation.NavType
import com.google.gson.Gson
import kotlinx.parcelize.Parcelize

/**
 * @author : huaweikai
 * @Date   : 2022/03/22
 * @Desc   :
 */
@Parcelize
data class ParcelizeMediaItem(
    val mediaId: String,
    val title: String,
    val artist: String,
    val artUri: String,
    val desc: String?,
    val year: Int?,
    val trackNumber: Int?
) : Parcelable

val defaultParcelizeMediaItem = ParcelizeMediaItem(
    "0", "", "", "", null, null, null
)

class NavTypeMediaItem : NavType<ParcelizeMediaItem>(true) {
    override fun get(bundle: Bundle, key: String): ParcelizeMediaItem? {
        return bundle.getParcelable(key)
    }

    override fun parseValue(value: String): ParcelizeMediaItem {
        return Gson().fromJson(value, ParcelizeMediaItem::class.java)
    }

    override fun put(bundle: Bundle, key: String, value: ParcelizeMediaItem) {
        bundle.putParcelable(key, value)

    }
}

@SuppressLint("UnsafeOptInUsageError")
fun MediaItem.toNavType() = Gson().toJson(
    ParcelizeMediaItem(
        mediaId = mediaId,
        title = "${mediaMetadata.title}",
        artist = "${mediaMetadata.artist}",
        artUri = "${mediaMetadata.artworkUri}",
        desc = "${mediaMetadata.subtitle ?: ""}",
        year = mediaMetadata.releaseYear,
        trackNumber = mediaMetadata.trackNumber
    )
)

