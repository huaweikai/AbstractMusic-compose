package com.hua.abstractmusic.use_case.sheet

import android.annotation.SuppressLint
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import com.hua.abstractmusic.bean.SongSheet
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
    suspend operator fun invoke(mediaItem: MediaItem, sheetName:String, isNewSheet:Boolean = false)= withContext(
        Dispatchers.IO){
        val list = repository.getSongIdBySheetName(sheetName)
        val sheetNameList = repository.getSheetName()
        val mediaId = mediaItem.mediaId.toUri().lastPathSegment.toString()
        sheetNameList?.let {
            //判断是否是addnew按钮要创建新歌单，并判断新歌单是否冲突
            if(sheetName in it && isNewSheet) throw MusicInsertError("歌单已存在，无法新建")
        }
        if(mediaId in list) throw MusicInsertError("音乐已经存在！")
        with(mediaItem.mediaMetadata){
            val songSheet = SongSheet(
                sheetName,
                mediaId,
                title.toString(),
                displayTitle.toString(),
                displayTitle.toString(),
                albumTitle.toString(),
                artist.toString(),
                trackNumber?.toLong()?:0L,
                mediaUri.toString(),
                artworkUri.toString()
            )
            repository.insertIntoSheet(songSheet)
        }
    }
}