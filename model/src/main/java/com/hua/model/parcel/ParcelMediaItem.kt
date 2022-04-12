package com.hua.model.parcel

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Parcelable
import androidx.media3.common.MediaItem
import androidx.navigation.NavType
import com.google.gson.Gson
import kotlinx.parcelize.Parcelize

/**
 * @author : huaweikai
 * @Date   : 2022/04/10
 * @Desc   :
 */
@Parcelize
data class ParcelizeMediaItem(
    val mediaId: String,
    val title: String,
    val artist: String,
    val artUri: String,
    val albumId: Long? = null,
    val desc: String? = null,
    val year: Int? = null,
    val trackNumber: Int? = null,
    val userId: Int? = null,
    val artistId: Long? = null
) : Parcelable

val defaultParcelizeMediaItem = ParcelizeMediaItem(
    "0", "", "", "",
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
fun MediaItem.toNavType(): String = Gson().toJson(
    ParcelizeMediaItem(
        mediaId = mediaId,
        title = "${mediaMetadata.title}",
        artist = "${mediaMetadata.artist}",
        artUri = "${mediaMetadata.artworkUri}",
        desc = "${mediaMetadata.subtitle ?: ""}",
        year = mediaMetadata.releaseYear,
        trackNumber = mediaMetadata.trackNumber,
        userId = mediaMetadata.extras?.getInt("userId"),
        albumId = mediaMetadata.extras?.getLong("albumId") ?: 0L,
        artistId = mediaMetadata.extras?.getLong("artistId")
    )
)

fun ParcelizeMediaItem.toGson(): String = Gson().toJson(this)

@SuppressLint("UnsafeOptInUsageError")
fun MediaItem.toParcel() =
    ParcelizeMediaItem(
        mediaId = mediaId,
        title = "${mediaMetadata.title}",
        artist = "${mediaMetadata.artist}",
        artUri = "${mediaMetadata.artworkUri}",
        desc = "${mediaMetadata.subtitle ?: ""}",
        year = mediaMetadata.releaseYear,
        trackNumber = mediaMetadata.trackNumber,
        userId = mediaMetadata.extras?.getInt("userId"),
        albumId = mediaMetadata.extras?.getLong("albumId") ?: 0L,
        artistId = mediaMetadata.extras?.getLong("artistId")
    )