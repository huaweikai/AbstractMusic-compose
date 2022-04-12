package com.hua.service.usecase.currentlist


import com.hua.service.room.dao.MusicDao

/**
 * @author : huaweikai
 * @Date   : 2021/11/27
 * @Desc   : 清空存在数据库的播放列表
 */
class ClearCurrentListCase(
    private val dao: MusicDao
) {
    suspend operator fun invoke(){
        dao.clearCurrentList()
    }
}