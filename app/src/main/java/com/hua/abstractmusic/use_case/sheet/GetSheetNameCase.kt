package com.hua.abstractmusic.use_case.sheet

import android.annotation.SuppressLint
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.hua.abstractmusic.repository.Repository

/**
 * @author : huaweikai
 * @Date   : 2021/11/27
 * @Desc   : 获取自定义歌单名字
 */
@SuppressLint("UnsafeOptInUsageError")
class GetSheetNameCase(
    private val repository: Repository
){
    operator fun invoke(parentId:Uri) :List<MediaItem>{
        val sheets = repository.getSheetName()?.toSet()
        val sheetList = mutableListOf<MediaItem>()
        sheets?.forEach {
            val metadataBuilder = MediaMetadata.Builder()
                .setTitle(it)
                .setIsPlayable(false)
                .setFolderType(MediaMetadata.FOLDER_TYPE_MIXED)
                .build()
            sheetList.add(
                MediaItem.Builder()
                    .setMediaId(parentId.buildUpon().appendPath(it).toString())
                    .setMediaMetadata(metadataBuilder)
                    .build()
            )
        }
        return sheetList
    }
}