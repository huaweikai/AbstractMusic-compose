package com.hua.abstractmusic.use_case.currentlist

import com.hua.abstractmusic.repository.Repository

/**
 * @author : huaweikai
 * @Date   : 2021/11/27
 * @Desc   : 清空存在数据库的播放列表
 */
class ClearCurrentListCase(
    private val repository: Repository
) {
    suspend operator fun invoke(){
        repository.clearCurrentPlayItems()
    }
}