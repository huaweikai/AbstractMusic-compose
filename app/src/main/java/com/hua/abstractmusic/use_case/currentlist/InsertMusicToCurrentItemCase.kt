package com.hua.abstractmusic.use_case.currentlist

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
    suspend operator fun invoke(mediaItems:List<MediaData>?){
        mediaItems?.let { items->
            items.forEach { mediaData ->
                val item = mediaData.mediaItem.metadata ?: return@let
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