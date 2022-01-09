package com.hua.abstractmusic.use_case.sheet

import androidx.core.net.toUri
import androidx.media2.common.MediaItem
import com.example.mediasession2demo.ui.data.SongSheet
import com.hua.abstractmusic.repository.Repository
import com.hua.abstractmusic.use_case.events.MusicInsertError
import com.hua.abstractmusic.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @author : huaweikai
 * @Date   : 2021/11/27
 * @Desc   : 插入歌单
 */
class InsertSheetCase(
    private val repository: Repository
) {
    suspend operator fun invoke(mediaItem: MediaItem, sheetName:String, isNewSheet:Boolean = false)= withContext(
        Dispatchers.IO){
        val list = repository.getSongIdBySheetName(sheetName)
        val sheetNameList = repository.getSheetName()
        val mediaId = mediaItem.metadata?.mediaId!!.toUri().lastPathSegment.toString()
        sheetNameList?.let {
            //判断是否是addnew按钮要创建新歌单，并判断新歌单是否冲突
            if(sheetName in it && isNewSheet) throw MusicInsertError("歌单已存在，无法新建")
        }
        if(mediaId in list) throw MusicInsertError("音乐已经存在！")
        mediaItem.metadata?.apply {
            val songSheet = SongSheet(
                sheetName,
                mediaId,
                this.title!!,
                this.displayTitle!!,
                this.displayTitle!!,
                this.album!!,
                this.artist!!,
                this.duration,
                this.trackNumber,
                this.mediaUri.toString(),
                this.albumArtUri.toString()
            )
            repository.insertIntoSheet(songSheet)
        }

    }
}