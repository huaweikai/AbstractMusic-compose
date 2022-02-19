package com.hua.abstractmusic.use_case.currentlist


import android.annotation.SuppressLint
import androidx.media3.common.MediaItem
import com.hua.abstractmusic.bean.CurrentPlayItem
import com.hua.abstractmusic.bean.MediaData
import com.hua.abstractmusic.repository.Repository

/**
 * @author : huaweikai
 * @Date   : 2021/11/27
 * @Desc   : 保存当前播放歌单
 */
@SuppressLint("UnsafeOptInUsageError")
class InsertMusicToCurrentItemCase(
    private val repository: Repository
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
                val currentPlayItem = CurrentPlayItem(
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
                repository.insertIntoCurrentPlayList(currentPlayItem)
            }
        }
    }
}