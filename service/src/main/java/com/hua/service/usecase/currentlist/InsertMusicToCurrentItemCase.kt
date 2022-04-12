package com.hua.service.usecase.currentlist


import android.annotation.SuppressLint
import androidx.media3.common.MediaItem
import com.hua.model.music.LastMusicPO
import com.hua.model.music.MediaData
import com.hua.service.room.dao.MusicDao

/**
 * @author : huaweikai
 * @Date   : 2021/11/27
 * @Desc   : 保存当前播放歌单
 */
@SuppressLint("UnsafeOptInUsageError")
class InsertMusicToCurrentItemCase(
    private val dao: MusicDao
) {
    @JvmName("mediaDataSave")
    suspend operator fun invoke(mediaItems:List<MediaData>?){
        val items = mediaItems?.map {
            it.mediaItem
        }
        invoke(items)
    }
    @JvmName("mediaItemSave")
    suspend operator fun invoke(mediaItems:List<MediaItem>?){
        mediaItems?.let { list ->
            list.forEach { it->
                val item = it.mediaMetadata
                val currentPlayItem = LastMusicPO(
                    it.mediaId,
                    item.title.toString(),
                    item.displayTitle.toString(),
                    item.subtitle.toString(),
                    item.albumTitle.toString(),
                    item.artist.toString(),
                    item.trackNumber?.toLong() ?: 0L,
                    item.mediaUri.toString(),
                    item.artworkUri.toString()
                )
                dao.insertCurrentPlayItem(currentPlayItem)
            }
        }
    }
}