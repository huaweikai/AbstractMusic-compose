package com.hua.abstractmusic.use_case.sheet

import android.annotation.SuppressLint
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.hua.abstractmusic.repository.Repository

/**
 * @author : huaweikai
 * @Date   : 2021/11/27
 * @Desc   : 获取歌单具体的列表
 */
@SuppressLint("UnsafeOptInUsageError")
class GetSheetMusicListCase(
    private val repository: Repository
) {
    operator fun invoke(sheetName:String,parentId: Uri):List<MediaItem>{
        val sheets = repository.getSheet(sheetName)
        val mediaItems = mutableListOf<MediaItem>()
        sheets?.forEach {
            val mediaMetadata = MediaMetadata.Builder()
                .setTitle(it.title)
                .setArtist(it.artist)
                .setArtworkUri(Uri.parse(it.albumUri))
                .setIsPlayable(true)
                .setAlbumTitle(it.album)
                .setDisplayTitle(it.displayTitle)
                .setFolderType(it.browserType)
                .setSubtitle(it.displaySubtitle)
                .build()
            mediaItems.add(
                MediaItem.Builder()
                    .setMediaId(parentId.buildUpon().appendPath(it.musicId).toString())
                    .setMediaMetadata(mediaMetadata)
                    .build()
            )
        }
        return mediaItems
    }
}