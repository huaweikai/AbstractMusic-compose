package com.hua.service.usecase.sheet

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.hua.model.other.Constants
import com.hua.service.room.dao.MusicDao

/**
 * @author : huaweikai
 * @Date   : 2021/11/27
 * @Desc   : 获取歌单具体的列表
 */
@SuppressLint("UnsafeOptInUsageError")
class SelectInfoBySheet(
    private val dao: MusicDao
) {
    suspend operator fun invoke(parentId: Uri): List<MediaItem> {
        val sheetId = parentId.lastPathSegment!!.toInt()
        val musicBySheet = dao.selectMusic(sheetId)
        val mediaItems = mutableListOf<MediaItem>()
        musicBySheet.musicPOS?.forEach {
            val mediaMetadata = MediaMetadata.Builder()
                .setTitle(it.title)
                .setArtist(it.artist)
                .setArtworkUri(Uri.parse(it.albumUri))
                .setIsPlayable(it.isPlayable)
                .setAlbumTitle(it.album)
                .setSubtitle(it.displaySubtitle)
                .setFolderType(it.browserType)
                .setExtras(
                    Bundle().apply {
                        putLong("artistId",it.artistId)
                        putLong("albumId",it.albumId)
                    }
                )
//                .setMediaUri(Uri.parse(it.mediaUri))
                .build()
            mediaItems.add(
                MediaItem.Builder()
                    .setMediaId(parentId.buildUpon().appendPath(it.musicId).toString())
                    .setMediaMetadata(mediaMetadata)
                    .setUri(it.mediaUri)
                    .build()
            )
        }
        return mediaItems
    }

    suspend operator fun invoke(): List<MediaItem> {
        val parentId: Uri = Uri.parse(Constants.LOCAL_SHEET_ID)
        val sheets = dao.selectLocalSheet()
        val sheetList = mutableListOf<MediaItem>()
        sheets?.forEach {
            val metadataBuilder = MediaMetadata.Builder()
                .setTitle(it.title)
                .setArtist("本地")
                .setArtworkUri(
                    if (it.artUri != null) Uri.parse(it.artUri) else null
                )
                .setSubtitle(it.desc)
                .setIsPlayable(false)
                .setFolderType(MediaMetadata.FOLDER_TYPE_MIXED)
                .build()
            sheetList.add(
                MediaItem.Builder()
                    .setMediaId(parentId.buildUpon().appendPath(it.sheetId.toString()).toString())
                    .setMediaMetadata(metadataBuilder)
                    .build()
            )
        }
        return sheetList
    }
}