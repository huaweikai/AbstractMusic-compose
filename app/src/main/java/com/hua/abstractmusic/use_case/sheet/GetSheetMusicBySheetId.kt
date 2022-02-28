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
class GetSheetMusicBySheetId(
    private val repository: Repository
) {
    suspend operator fun invoke(parentId: Uri): List<MediaItem> {
        val sheetId = parentId.lastPathSegment!!.toInt()
        val musicBySheet = repository.selectMusicBySheetId(sheetId)
        val mediaItems = mutableListOf<MediaItem>()
        musicBySheet.music?.forEach {
            val mediaMetadata = MediaMetadata.Builder()
                .setTitle(it.title)
                .setArtist(it.artist)
                .setArtworkUri(Uri.parse(it.albumUri))
                .setIsPlayable(it.isPlayable)
                .setAlbumTitle(it.album)
                .setSubtitle(it.displaySubtitle)
                .setFolderType(it.browserType)
                .setMediaUri(Uri.parse(it.mediaUri))
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
}