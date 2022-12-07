package com.hua.abstractmusic.utils

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.hua.model.album.AlbumVO
import com.hua.model.artist.ArtistVO
import com.hua.model.music.MusicVO
import com.hua.model.parcel.ParcelizeMediaItem
import com.hua.model.sheet.SheetPO
import com.hua.model.sheet.SheetVO
import com.hua.model.other.Constants

/**
 * @author : huaweikai
 * @Date   : 2021/11/28
 * @Desc   : more
 */
@SuppressLint("UnsafeOptInUsageError")
fun AlbumVO.toMediaItem(parentId: Uri): MediaItem {
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
fun ArtistVO.toMediaItem(parentId: Uri): MediaItem {
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
fun MusicVO.toMediaItem(parentId: Uri): MediaItem {
    return MediaItem.Builder()
        .setMediaMetadata(
            MediaMetadata.Builder()
                .setTitle(this@toMediaItem.name)
                .setDisplayTitle(this@toMediaItem.name)
                .setSubtitle(this@toMediaItem.artist)
                .setArtist(this@toMediaItem.artist)
                .setArtworkUri(Uri.parse(this@toMediaItem.imgUrl))
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
fun SheetVO.toMediaItem(parentId: Uri) =
    MediaItem.Builder()
        .setMediaMetadata(
            MediaMetadata.Builder()
                .setTitle(this@toMediaItem.title)
                .setIsPlayable(false)
                .setFolderType(MediaMetadata.FOLDER_TYPE_PLAYLISTS)
                .setArtist(author)
                .setSubtitle(this.sheetDesc)
                .setTrackNumber(num)
                .setArtworkUri(
                    if (this.artUri == null) null else Uri.parse(this.artUri)
                )
                .setExtras(
                    Bundle().apply {
                        putInt("userId",userId)
                    }
                )
                .build()
        )
        .setMediaId(parentId.buildUpon().appendPath(this@toMediaItem.id.toString()).toString())
        .build()
@SuppressLint("UnsafeOptInUsageError")
fun ParcelizeMediaItem.toMediaItem() = MediaItem.Builder()
    .setMediaMetadata(
        MediaMetadata.Builder()
            .setTitle(title)
            .setArtist(artist)
            .setAlbumTitle(album)
            .setArtworkUri(
                if (artUri.isNotBlank()) Uri.parse(artUri) else null
            )
            .setExtras(Bundle().apply {
                putLong("albumId",albumId ?: 0L)
                putLong("artistId",artistId ?: 0L)
            })
            .setSubtitle(desc)
            .setReleaseYear(year)
            .setTrackNumber(trackNumber)
            .build()
    )
    .setMediaId(mediaId)
    .build()

@SuppressLint("UnsafeOptInUsageError")
fun SheetPO.toMediaItem() = MediaItem.Builder()
    .setMediaMetadata(
        MediaMetadata.Builder()
            .setTitle(title)
            .setSubtitle(desc)
            .setArtworkUri(
                if(artUri == null) null else Uri.parse(artUri)
            )
            .setArtist("本地")
            .setIsPlayable(false)
            .setFolderType(MediaMetadata.FOLDER_TYPE_MIXED)
            .build()
    )
    .setMediaId(Uri.parse("${Constants.LOCAL_SHEET_ID}/$sheetId").toString())
    .build()

