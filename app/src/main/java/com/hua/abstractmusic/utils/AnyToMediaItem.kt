package com.hua.abstractmusic.utils

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.hua.abstractmusic.bean.net.NetAlbum
import com.hua.abstractmusic.bean.net.NetArtist
import com.hua.abstractmusic.bean.net.NetMusic
import com.hua.abstractmusic.bean.net.NetSheet
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author : huaweikai
 * @Date   : 2021/11/28
 * @Desc   : more
 */
fun Long.toTime(): String {
    val simpleDateFormat = SimpleDateFormat("mm:ss", Locale.CHINESE)
    val date = Date(this)
    return simpleDateFormat.format(date)
}

fun Long.toDate(): String {
    val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.CHINESE)
    val date = Date(this)
    return simpleDateFormat.format(date)
}

fun String.toTime(): Long {
    val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.CHINESE)
    return try {
        simpleDateFormat.parse(this)!!.time
    } catch (e: Throwable) {
        0
    }
}

fun Long.toYear(): Int? {
    val simpleDateFormat = SimpleDateFormat("yyyy", Locale.CHINESE)
    return try {
        simpleDateFormat.format(Date(this)).toIntOrNull()
    } catch (e: Exception) {
        null
    }
}

@SuppressLint("UnsafeOptInUsageError")
fun NetAlbum.toMediaItem(parentId: Uri): MediaItem {
    return MediaItem.Builder()
        .setMediaMetadata(
            MediaMetadata.Builder()
                .setTitle(this@toMediaItem.name)
                .setReleaseYear(this.time.toTime().toYear())
                .setArtist(this@toMediaItem.artistName)
                .setDisplayTitle(this@toMediaItem.name)
                .setArtworkUri(Uri.parse(this@toMediaItem.imgUrl))
                .setSubtitle(this@toMediaItem.albumDesc)
                .setTrackNumber(this.num)
                .setFolderType(MediaMetadata.FOLDER_TYPE_ALBUMS)
                .setIsPlayable(false)
                .build()
        )
        .setMediaId(parentId.buildUpon().appendPath(this@toMediaItem.id.toString()).toString())
        .build()
}

@SuppressLint("UnsafeOptInUsageError")
fun NetArtist.toMediaItem(parentId: Uri): MediaItem {
    return MediaItem.Builder()
        .setMediaMetadata(
            MediaMetadata.Builder()
                .setTitle(this@toMediaItem.name)
                .setArtist(this@toMediaItem.name)
                .setDisplayTitle(this@toMediaItem.name)
                .setArtworkUri(Uri.parse(this@toMediaItem.imgUrl))
                .setTrackNumber(num)
                .setSubtitle(this@toMediaItem.artistDesc)
                .setIsPlayable(false)
                .setFolderType(MediaMetadata.FOLDER_TYPE_ARTISTS)
                .build()
        )
        .setMediaId(parentId.buildUpon().appendPath(this@toMediaItem.id.toString()).toString())
        .build()
}

@SuppressLint("UnsafeOptInUsageError")
fun NetMusic.toMediaItem(parentId: Uri): MediaItem {
    return MediaItem.Builder()
        .setMediaMetadata(
            MediaMetadata.Builder()
                .setTitle(this@toMediaItem.name)
                .setDisplayTitle(this@toMediaItem.name)
                .setSubtitle(this@toMediaItem.artist)
                .setArtist(this@toMediaItem.artist)
                .setArtworkUri(Uri.parse(this@toMediaItem.imgUrl))
                .setMediaUri(this.musicUrl.toUri())
                .setExtras(
                    Bundle().apply {
                        putLong("albumId", albumId.toLong())
//                        putLong("artistId", artistId)
                    }
                )
                .setIsPlayable(true)
                .setAlbumTitle(this@toMediaItem.albumName)
                .setAlbumArtist(this@toMediaItem.artist)
                .setFolderType(MediaMetadata.FOLDER_TYPE_NONE)
                .build()
        )
        .setMediaId(parentId.buildUpon().appendPath(this@toMediaItem.id.toString()).toString())
        .setUri(this.musicUrl)
        .build()
}

@SuppressLint("UnsafeOptInUsageError")
fun NetSheet.toMediaItem(parentId: Uri) =
    MediaItem.Builder()
        .setMediaMetadata(
            MediaMetadata.Builder()
                .setTitle(this@toMediaItem.title)
                .setIsPlayable(false)
                .setFolderType(MediaMetadata.FOLDER_TYPE_PLAYLISTS)
                .setArtist(author)
                .setTrackNumber(num)
                .setArtworkUri(
                    if (this.artUri == null) null else Uri.parse(this.artUri)
                )
                .build()
        )
        .setMediaId(parentId.buildUpon().appendPath(this@toMediaItem.id.toString()).toString())
        .build()

/**
 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
 */
fun Number.px2dip(context: Context, pxValue: Float): Int {
    val scale: Float = context.resources.displayMetrics.density
    return (pxValue / scale + 0.5f).toInt()
}

