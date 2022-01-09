package com.hua.abstractmusic.use_case.sheet

import android.net.Uri
import androidx.media2.common.MediaItem
import androidx.media2.common.MediaMetadata
import com.hua.abstractmusic.repository.Repository
import com.hua.abstractmusic.utils.*

/**
 * @author : huaweikai
 * @Date   : 2021/11/27
 * @Desc   : 获取自定义歌单名字
 */
class GetSheetNameCase(
    private val repository: Repository
){
    operator fun invoke(parentId:Uri) :List<MediaItem>{
        val sheets = repository.getSheetName()?.toSet()
        val sheetList = mutableListOf<MediaItem>()
        sheets?.forEach {
            val metadataBuilder = MediaMetadata.Builder().apply {
                this.id = parentId.buildUpon().appendPath(it).toString()
                this.title = it
                this.isPlayable = false
                this.browserType = MediaMetadata.BROWSABLE_TYPE_MIXED
            }.build()
            sheetList.add(
                MediaItem.Builder()
                    .setMetadata(metadataBuilder)
                    .build()
            )
        }
        return sheetList
    }
}