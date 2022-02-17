package com.hua.abstractmusic.use_case.net

import android.net.Uri
import androidx.media2.common.MediaItem
import com.hua.abstractmusic.repository.NetRepository

/**
 * @author : huaweikai
 * @Date   : 2022/01/06
 * @Desc   : case
 */
class SelectNetAlbumCase(
    private val repository: NetRepository
) {
//    suspend operator fun invoke():List<MediaItem>{
//        return repository.selectAlbumList()
//    }
//    suspend operator fun invoke(parentId:Uri):List<MediaItem>{
//        return repository.selectMusicByAlbum(parentId)
//    }
}