package com.hua.model.user

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @author : huaweikai
 * @Date   : 2022/04/10
 * @Desc   :
 */
@Entity
data class HistoryPO(
    @PrimaryKey(autoGenerate = true) var id:Int = 0,
    val history:String
)