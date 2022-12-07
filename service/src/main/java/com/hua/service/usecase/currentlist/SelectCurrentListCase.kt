package com.hua.service.usecase.currentlist

import android.annotation.SuppressLint
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.hua.service.room.dao.MusicDao

/**
 * @author : huaweikai
 * @Date   : 2021/11/27
 * @Desc   : 获取上次保存的播放列表
 */
@SuppressLint("UnsafeOptInUsageError")
class SelectCurrentListCase(
    private val dao: MusicDao
) {
     suspend operator fun invoke(): List<MediaItem> {
        val playList = arrayListOf<MediaItem>()
        val currentPlaylist = dao.getLastPlayList()
        currentPlaylist?.let {
            it.forEach { item ->
                val metadata = MediaMetadata.Builder()
                    .setTitle(item.title)
                    .setArtist(item.artist)
                    .setDisplayTitle(item.displayTitle)
                    .setSubtitle(item.displaySubtitle)
                    .setAlbumTitle(item.album)
                    .setTrackNumber(item.trackerNumber.toInt())
                    .setArtworkUri(Uri.parse(item.albumUri))
//                    .setMediaUri(Uri.parse(item.mediaUri))
                    .setIsPlayable(true)
                    .setFolderType(MediaMetadata.FOLDER_TYPE_NONE)
                    .build()
                playList.add(
                    MediaItem.Builder()
                        .setUri(item.mediaUri)
                        .setMediaId(item.musicId)
                        .setMediaMetadata(metadata)
                        .build()
                )
            }
        }
        return playList
    }
}