package com.hua.service.usecase.sheet

import android.annotation.SuppressLint
import android.net.Uri
import androidx.media3.common.MediaItem
import com.hua.model.sheet.SheetMusicPO
import com.hua.model.sheet.SheetPO
import com.hua.model.sheet.SheetToMusicPO
import com.hua.service.room.dao.MusicDao
import com.hua.service.usecase.events.MusicInsertError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @author : huaweikai
 * @Date   : 2021/11/27
 * @Desc   : 插入歌单
 */
@SuppressLint("UnsafeOptInUsageError")
class InsertSheetCase(
    private val dao: MusicDao
) {

    suspend operator fun invoke(sheetTitle: String) = withContext(Dispatchers.IO) {
        val list = dao.selectLocalSheetTitle()
        list?.let {
            if (sheetTitle in list) throw MusicInsertError("歌单已存在，无法新建")
        }
        dao.insertSheet(SheetPO(sheetId = 0, title = sheetTitle))
    }

    suspend operator fun invoke(mediaItem: MediaItem, sheetId: Int) = withContext(
        Dispatchers.IO
    ) {
        val song = dao.selectMusicIdBySheetId(sheetId)
        val sheet = dao.selectSheetBySheetId(sheetId)
        val id = "${Uri.parse(mediaItem.mediaId).lastPathSegment}"
        song?.let {
            if (id in it) throw MusicInsertError("音乐已经存在！")
        }
        with(mediaItem.mediaMetadata) {
            SheetMusicPO(
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
                    dao.insertSheet(sheet.copy(
                        artUri = it.albumUri
                    ))
                }
                dao.insertIntoSheet(it)
                dao.insertMusicToSheet(SheetToMusicPO(sheetId,id))
            }
        }
    }
}