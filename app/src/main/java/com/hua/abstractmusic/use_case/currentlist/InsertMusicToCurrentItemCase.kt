package com.hua.abstractmusic.use_case.currentlist

import androidx.media2.common.MediaItem
import com.hua.abstractmusic.bean.CurrentPlayItem
import com.hua.abstractmusic.bean.MediaData
import com.hua.abstractmusic.repository.Repository
import com.hua.abstractmusic.utils.*

/**
 * @author : huaweikai
 * @Date   : 2021/11/27
 * @Desc   : 保存当前播放歌单
 */
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
            list.forEach {
                val item = it.metadata?:return@let
                val currentPlayItem = CurrentPlayItem(
                    item.mediaId!!,
                    item.title.toString(),
                    item.displayTitle.toString(),
                    item.displaySubtitle.toString(),
                    item.album.toString(),
                    item.artist.toString(),
                    item.duration,
                    item.trackNumber,
                    item.mediaUri.toString(),
                    item.albumArtUri.toString()
                )
                repository.insertIntoCurrentPlayList(currentPlayItem)
            }
        }
    }
}