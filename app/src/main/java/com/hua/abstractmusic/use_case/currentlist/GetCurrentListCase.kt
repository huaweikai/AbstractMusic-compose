package com.hua.abstractmusic.use_case.currentlist

import android.annotation.SuppressLint
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.hua.abstractmusic.repository.Repository

/**
 * @author : huaweikai
 * @Date   : 2021/11/27
 * @Desc   : 获取上次保存的播放列表
 */
@SuppressLint("UnsafeOptInUsageError")
class GetCurrentListCase(
    private val repository: Repository
) {
     suspend operator fun invoke(): List<MediaItem> {
        val playList = arrayListOf<MediaItem>()
        val currentPlaylist = repository.getLastPlayList()
        currentPlaylist?.let {
            it.forEach { item ->
                val metadata = MediaMetadata.Builder()
                    .setTitle(item.title)
                    .setArtist(item.artist)
                    .setDisplayTitle(item.displayTitle)
                    .setSubtitle(item.displaySubtitle)
                    .setAlbumTitle(item.album)
                    .setTrackNumber(item.trackerNumber.toInt())
                    .setArtworkUri(item.albumUri.toUri())
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