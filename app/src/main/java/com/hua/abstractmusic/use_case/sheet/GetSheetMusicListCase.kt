package com.hua.abstractmusic.use_case.sheet

import android.net.Uri
import androidx.media2.common.MediaItem
import androidx.media2.common.MediaMetadata
import com.hua.abstractmusic.repository.Repository
import com.hua.abstractmusic.utils.*

/**
 * @author : huaweikai
 * @Date   : 2021/11/27
 * @Desc   : 获取歌单具体的列表
 */
class GetSheetMusicListCase(
    private val repository: Repository
) {
    operator fun invoke(sheetName:String,parentId: Uri):List<MediaItem>{
        val sheets = repository.getSheet(sheetName)
        val mediaItems = mutableListOf<MediaItem>()
        sheets?.forEach {
            val metadataBuilder = MediaMetadata.Builder().apply {
                //由于存的时候用的所有音乐的mediaid，为了播放不冲突，取出最后一个，然后添加歌单的前缀
                this.id = parentId.buildUpon().appendPath(it.musicId).toString()
                this.title = it.title
                this.displayTitle = it.displayTitle
                this.displaySubtitle = it.displaySubtitle
                this.album = it.album
                this.artist = it.artist
                this.trackCount = it.trackerNumber
                this.mediaUri = it.mediaUri
                this.albumArtUri = it.albumUri
                this.displayIconUri =it.albumUri
                this.isPlayable = true
                this.browserType = MediaMetadata.BROWSABLE_TYPE_NONE
            }.build()
            mediaItems.add(
                MediaItem.Builder()
                    .setMetadata(metadataBuilder)
                    .build()
            )
        }
        return mediaItems
    }
}