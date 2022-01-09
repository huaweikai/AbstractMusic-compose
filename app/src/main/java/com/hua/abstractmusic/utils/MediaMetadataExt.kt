package com.hua.abstractmusic.utils

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import androidx.core.net.toUri
import androidx.media2.common.MediaMetadata
import androidx.media2.common.Rating

/**
 *
 * @author Xiaoc
 * @since 2020/12/26
 *
 * 关于 [androidx.media2.common.MediaMetadata] 的扩展，方便调用使用
 */

inline val MediaMetadata.id: String
    get() = getString(MediaMetadata.METADATA_KEY_MEDIA_ID) ?: ""

inline val MediaMetadata.browserType: Long
    get() = getLong(MediaMetadata.METADATA_KEY_BROWSABLE)

inline val MediaMetadata.title: String?
    get() = getString(MediaMetadata.METADATA_KEY_TITLE)

inline val MediaMetadata.artist: String?
    get() = getString(MediaMetadata.METADATA_KEY_ARTIST)

inline val MediaMetadata.duration
    get() = getLong(MediaMetadata.METADATA_KEY_DURATION)

inline val MediaMetadata.author: String?
    get() = getString(MediaMetadata.METADATA_KEY_AUTHOR)

inline val MediaMetadata.album: String?
    get() = getString(MediaMetadata.METADATA_KEY_ALBUM)

inline val MediaMetadata.writer: String?
    get() = getString(MediaMetadata.METADATA_KEY_WRITER)

inline val MediaMetadata.composer: String?
    get() = getString(MediaMetadata.METADATA_KEY_COMPOSER)

inline val MediaMetadata.compilation: String?
    get() = getString(MediaMetadata.METADATA_KEY_COMPILATION)

inline val MediaMetadata.date: String?
    get() = getString(MediaMetadata.METADATA_KEY_DATE)

inline val MediaMetadata.year
    get() = getLong(MediaMetadata.METADATA_KEY_YEAR)

inline val MediaMetadata.genre: String?
    get() = getString(MediaMetadata.METADATA_KEY_GENRE)

inline val MediaMetadata.trackNumber
    get() = getLong(MediaMetadata.METADATA_KEY_TRACK_NUMBER)

inline val MediaMetadata.trackCount
    get() = getLong(MediaMetadata.METADATA_KEY_NUM_TRACKS)

inline val MediaMetadata.discNumber
    get() = getLong(MediaMetadata.METADATA_KEY_DISC_NUMBER)

inline val MediaMetadata.albumArtist: String?
    get() = getString(MediaMetadata.METADATA_KEY_ALBUM_ARTIST)

inline val MediaMetadata.art: Bitmap?
    get() = getBitmap(MediaMetadata.METADATA_KEY_ART)

inline val MediaMetadata.artUri: Uri?
    get() = getString(MediaMetadata.METADATA_KEY_ART_URI)?.toUri()

inline val MediaMetadata.albumArt: Bitmap?
    get() = getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART)

inline val MediaMetadata.albumArtUri: Uri?
    get() = getString(MediaMetadata.METADATA_KEY_ALBUM_ART_URI)?.toUri()

inline val MediaMetadata.rating: Rating?
    get() = getRating(MediaMetadata.METADATA_KEY_RATING)

inline val MediaMetadata.displayTitle: String?
    get() = getString(MediaMetadata.METADATA_KEY_DISPLAY_TITLE)

inline val MediaMetadata.displaySubtitle: String?
    get() = getString(MediaMetadata.METADATA_KEY_DISPLAY_SUBTITLE)

inline val MediaMetadata.displayDescription: String?
    get() = getString(MediaMetadata.METADATA_KEY_DISPLAY_DESCRIPTION)

inline val MediaMetadata.displayIcon: Bitmap?
    get() = getBitmap(MediaMetadata.METADATA_KEY_DISPLAY_ICON)

inline val MediaMetadata.displayIconUri: Uri?
    get() = getString(MediaMetadata.METADATA_KEY_DISPLAY_ICON_URI)?.toUri()

inline val MediaMetadata.mediaUri: Uri?
    get() = getString(MediaMetadata.METADATA_KEY_MEDIA_URI)?.toUri()

inline val MediaMetadata.downloadStatus
    get() = getLong(MediaMetadata.METADATA_KEY_DOWNLOAD_STATUS)

inline val MediaMetadata.advertisement
    get() = getLong(MediaMetadata.METADATA_KEY_ADVERTISEMENT)

inline val MediaMetadata.userRating: Rating?
    get() = getRating(MediaMetadata.METADATA_KEY_USER_RATING)

inline val MediaMetadata.isPlayable: Boolean
    get() = getLong(MediaMetadata.METADATA_KEY_PLAYABLE) != 0L

const val NO_GET = "属性不可以存在get方法"
const val GET_ERROR = "不可以从Builder中获取值"

inline var MediaMetadata.Builder.id: String?
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException(GET_ERROR)
    set(value) {
        putString(MediaMetadata.METADATA_KEY_MEDIA_ID,value)
    }

inline var MediaMetadata.Builder.browserType: Long
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException(GET_ERROR)
    set(value) {
        putLong(MediaMetadata.METADATA_KEY_BROWSABLE,value)
    }

inline var MediaMetadata.Builder.title: String?
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException(GET_ERROR)
    set(value) {
        putString(MediaMetadata.METADATA_KEY_TITLE,value)
    }

inline var MediaMetadata.Builder.artist: String?
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException(GET_ERROR)
    set(value) {
        putString(MediaMetadata.METADATA_KEY_ARTIST,value)
    }

inline var MediaMetadata.Builder.duration: Long
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException(GET_ERROR)
    set(value) {
        putLong(MediaMetadata.METADATA_KEY_DURATION,value)
    }

inline var MediaMetadata.Builder.author: String?
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException(GET_ERROR)
    set(value) {
        putString(MediaMetadata.METADATA_KEY_AUTHOR,value)
    }

inline var MediaMetadata.Builder.album: String?
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException(GET_ERROR)
    set(value) {
        putString(MediaMetadata.METADATA_KEY_ALBUM,value)
    }

inline var MediaMetadata.Builder.writer: String?
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException(GET_ERROR)
    set(value) {
        putString(MediaMetadata.METADATA_KEY_WRITER,value)
    }

inline var MediaMetadata.Builder.composer: String?
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException(GET_ERROR)
    set(value) {
        putString(MediaMetadata.METADATA_KEY_COMPOSER,value)
    }

inline var MediaMetadata.Builder.compilation: String?
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException(GET_ERROR)
    set(value) {
        putString(MediaMetadata.METADATA_KEY_COMPILATION,value)
    }

inline var MediaMetadata.Builder.date: String?
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException(GET_ERROR)
    set(value) {
        putString(MediaMetadata.METADATA_KEY_DATE,value)
    }

inline var MediaMetadata.Builder.year: Long
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException(GET_ERROR)
    set(value) {
        putLong(MediaMetadata.METADATA_KEY_YEAR,value)
    }

inline var MediaMetadata.Builder.genre: String?
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException(GET_ERROR)
    set(value) {
        putString(MediaMetadata.METADATA_KEY_GENRE,value)
    }

inline var MediaMetadata.Builder.trackNumber: Long
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException(GET_ERROR)
    set(value) {
        putLong(MediaMetadata.METADATA_KEY_TRACK_NUMBER,value)
    }

inline var MediaMetadata.Builder.trackCount: Long
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException(GET_ERROR)
    set(value) {
        putLong(MediaMetadata.METADATA_KEY_NUM_TRACKS,value)
    }

inline var MediaMetadata.Builder.discNumber: Long
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException(GET_ERROR)
    set(value) {
        putLong(MediaMetadata.METADATA_KEY_DISC_NUMBER,value)
    }

inline var MediaMetadata.Builder.albumArtist: String?
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException(GET_ERROR)
    set(value) {
        putString(MediaMetadata.METADATA_KEY_ALBUM_ARTIST,value)
    }

inline var MediaMetadata.Builder.art: Bitmap?
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException(GET_ERROR)
    set(value) {
        putBitmap(MediaMetadata.METADATA_KEY_ART,value)
    }

inline var MediaMetadata.Builder.artUri: String?
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException(GET_ERROR)
    set(value) {
        putString(MediaMetadata.METADATA_KEY_ART_URI,value)
    }

inline var MediaMetadata.Builder.albumArt: Bitmap?
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException(GET_ERROR)
    set(value) {
        putBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART,value)
    }

inline var MediaMetadata.Builder.albumArtUri: String?
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException(GET_ERROR)
    set(value) {
        putString(MediaMetadata.METADATA_KEY_ALBUM_ART_URI,value)
    }

inline var MediaMetadata.Builder.rating: Rating?
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException(GET_ERROR)
    set(value) {
        putRating(MediaMetadata.METADATA_KEY_RATING,value)
    }

inline var MediaMetadata.Builder.displayTitle: String?
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException(GET_ERROR)
    set(value) {
        putString(MediaMetadata.METADATA_KEY_DISPLAY_TITLE,value)
    }

inline var MediaMetadata.Builder.displaySubtitle: String?
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException(GET_ERROR)
    set(value) {
        putString(MediaMetadata.METADATA_KEY_DISPLAY_SUBTITLE,value)
    }

inline var MediaMetadata.Builder.displayDescription: String?
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException(GET_ERROR)
    set(value) {
        putString(MediaMetadata.METADATA_KEY_DISPLAY_DESCRIPTION,value)
    }

inline var MediaMetadata.Builder.displayIcon: Bitmap?
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException(GET_ERROR)
    set(value) {
        putBitmap(MediaMetadata.METADATA_KEY_DISPLAY_ICON,value)
    }

inline var MediaMetadata.Builder.displayIconUri: String?
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException(GET_ERROR)
    set(value) {
        putString(MediaMetadata.METADATA_KEY_DISPLAY_ICON_URI,value)
    }

inline var MediaMetadata.Builder.mediaUri: String?
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException(GET_ERROR)
    set(value) {
        putString(MediaMetadata.METADATA_KEY_MEDIA_URI,value)
    }

inline var MediaMetadata.Builder.downloadStatus: Long
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException(GET_ERROR)
    set(value) {
        putLong(MediaMetadata.METADATA_KEY_DOWNLOAD_STATUS,value)
    }

inline var MediaMetadata.Builder.advertisement: Long
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException(GET_ERROR)
    set(value) {
        putLong(MediaMetadata.METADATA_KEY_ADVERTISEMENT,value)
    }

inline var MediaMetadata.Builder.userRating: Rating?
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException(GET_ERROR)
    set(value) {
        putRating(MediaMetadata.METADATA_KEY_USER_RATING,value)
    }

inline var MediaMetadata.Builder.isPlayable: Boolean?
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException(GET_ERROR)
    set(value) {
        putLong(MediaMetadata.METADATA_KEY_PLAYABLE,if(value == true){1L} else {0L})
    }


