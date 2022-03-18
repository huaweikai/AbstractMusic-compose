package com.hua.abstractmusic.bean.user

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @author : huaweikai
 * @Date   : 2022/03/18
 * @Desc   :
 */
@Entity
data class SearchHistory(
    @PrimaryKey(autoGenerate = true) var id:Int = 0,
    val history:String
)