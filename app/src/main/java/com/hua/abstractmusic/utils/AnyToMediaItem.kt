package com.hua.abstractmusic.utils

import android.content.Context
import android.net.Uri
import androidx.media2.common.MediaItem
import androidx.media2.common.MediaMetadata
import com.hua.abstractmusic.bean.net.NetAlbum
import com.hua.abstractmusic.bean.net.NetArtist
import com.hua.abstractmusic.bean.net.NetMusic
import com.hua.abstractmusic.bean.net.NetSheet
import com.hua.abstractmusic.other.Constant
import com.hua.abstractmusic.other.Constant.NETWORK_RECOMMEND_ID
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

fun NetAlbum.toMediaItem(parentId: String): MediaItem {
    return MediaItem.Builder()
        .setMetadata(
            MediaMetadata.Builder().apply {
                id = "${parentId}/${this@toMediaItem.id}"
                title = this@toMediaItem.name
                year = this@toMediaItem.time.toTime()
                artist = this@toMediaItem.artistName
                displayTitle = this@toMediaItem.name
                displaySubtitle = this@toMediaItem.artistName
                albumArtUri = this@toMediaItem.imgUrl
                displayIconUri = this@toMediaItem.imgUrl
                isPlayable = false
                browserType = MediaMetadata.BROWSABLE_TYPE_ALBUMS
            }.build()
        ).build()
}

fun NetArtist.toMediaItem(): MediaItem {
    return MediaItem.Builder()
        .setMetadata(
            MediaMetadata.Builder().apply {
                id = "${Constant.NETWORK_ARTIST_ID}/${this@toMediaItem.id}"
                title = this@toMediaItem.name
                artist = this@toMediaItem.name
                displayTitle = this@toMediaItem.name
                albumArtUri = this@toMediaItem.imgUrl
                displayIconUri = this@toMediaItem.imgUrl
                displaySubtitle = this@toMediaItem.artistDesc
                isPlayable = false
                browserType = MediaMetadata.BROWSABLE_TYPE_ARTISTS
            }.build()
        ).build()
}

fun NetMusic.toMediaItem(parentId: Uri): MediaItem {
    return MediaItem.Builder()
        .setMetadata(
            MediaMetadata.Builder().apply {
                id = parentId.buildUpon().appendPath(this@toMediaItem.id.toString()).toString()
                mediaUri = this@toMediaItem.musicUrl
                displayIconUri = this@toMediaItem.imgUrl
                artUri = this@toMediaItem.imgUrl
                albumArtUri = this@toMediaItem.imgUrl
                artist = this@toMediaItem.artist
                album = this@toMediaItem.albumName
                title = this@toMediaItem.name
                displayTitle = this@toMediaItem.name
                displaySubtitle = this@toMediaItem.artist
                isPlayable = true
                browserType = MediaMetadata.BROWSABLE_TYPE_NONE
            }.build()
        )
        .build()
}

fun NetSheet.toMediaItem() =
    MediaItem.Builder()
        .setMetadata(
            MediaMetadata.Builder().apply {
                id = "$NETWORK_RECOMMEND_ID/${this@toMediaItem.id}"
                albumArtUri = this@toMediaItem.sheetImg
                title = this@toMediaItem.sheetName
            }.build()
        )
        .build()

/**
 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
 */
fun Number.px2dip(context: Context, pxValue: Float): Int {
    val scale: Float = context.resources.displayMetrics.density
    return (pxValue / scale + 0.5f).toInt()
}

