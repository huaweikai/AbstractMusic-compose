package com.hua.abstractmusic.use_case.sheet

import android.annotation.SuppressLint
import android.net.Uri
import androidx.media3.common.MediaItem
import com.hua.abstractmusic.bean.Sheet
import com.hua.abstractmusic.bean.SheetMusic
import com.hua.abstractmusic.bean.SheetToMusic
import com.hua.abstractmusic.repository.Repository
import com.hua.abstractmusic.use_case.events.MusicInsertError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @author : huaweikai
 * @Date   : 2021/11/27
 * @Desc   : 插入歌单
 */
@SuppressLint("UnsafeOptInUsageError")
class InsertSheetCase(
    private val repository: Repository
) {

    suspend operator fun invoke(sheetTitle: String) = withContext(Dispatchers.IO) {
        val list = repository.selectSheetName()
        list?.let {
            if (sheetTitle in list) throw MusicInsertError("歌单已存在，无法新建")
        }
        repository.insertSheet(Sheet(sheetId = 0, title = sheetTitle))
    }

    suspend operator fun invoke(mediaItem: MediaItem, sheetId: Int) = withContext(
        Dispatchers.IO
    ) {
        val song = repository.selectMusicIdBySheetId(sheetId)
        val sheet = repository.selectSheetBySheetId(sheetId)
        val id = "${Uri.parse(mediaItem.mediaId).lastPathSegment}"
        song?.let {
            if (id in it) throw MusicInsertError("音乐已经存在！")
        }
        with(mediaItem.mediaMetadata) {
            SheetMusic(
                musicId = "${Uri.parse(mediaItem.mediaId).lastPathSegment}",
                title = "$title",
                displayTitle = "$displayTitle",
                displaySubtitle = "$displayTitle",
                album = "$albumTitle",
                artist = "$artist",
                trackerNumber = trackNumber,
                mediaUri = "$mediaUri",
                albumUri = "$artworkUri",
                artistId = extras?.getLong("artistId") ?:0L,
                albumId = extras?.getLong("albumId") ?: 0L
            ).also {
                if(sheet.artUri == null){
                    repository.insertSheet(sheet.copy(
                        artUri = it.albumUri
                    ))
                }
                repository.insertIntoSheet(it)
                repository.insertMusicToSheet(SheetToMusic(sheetId,id))
            }
        }
    }
}