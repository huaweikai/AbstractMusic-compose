package com.hua.abstractmusic.use_case.currentlist

import androidx.media2.common.MediaItem
import androidx.media2.common.MediaMetadata
import com.hua.abstractmusic.repository.Repository
import com.hua.abstractmusic.utils.*

/**
 * @author : huaweikai
 * @Date   : 2021/11/27
 * @Desc   : 获取上次保存的播放列表
 */
class GetCurrentListCase(
    private val repository: Repository
) {
    suspend operator fun invoke():List<MediaItem>{
        val playList = arrayListOf<MediaItem>()
        val currentPlaylist = repository.getLastPlayList()
        currentPlaylist?.let {
            it.forEach { item->
                val metadata = MediaMetadata.Builder().apply {
                    this.id = item.musicId
                    this.title = item.title
                    this.displayTitle = item.displayTitle
                    this.displaySubtitle = item.displaySubtitle
                    this.album = item.album
                    this.artist = item.artist
                    this.trackCount = item.trackerNumber
                    this.mediaUri = item.mediaUri
                    this.albumArtUri = item.albumUri
                    this.displayIconUri = item.albumUri
                    this.isPlayable = true
                    this.browserType = MediaMetadata.BROWSABLE_TYPE_NONE
                }.build()
                playList.add(
                    MediaItem.Builder()
                        .setMetadata(metadata)
                        .build()
                )
            }
        }
        return playList
    }
}