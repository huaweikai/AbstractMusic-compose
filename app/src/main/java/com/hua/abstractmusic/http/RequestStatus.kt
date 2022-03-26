package com.hua.abstractmusic.http

import com.hua.abstractmusic.bean.net.NetData

/**
 * @author : huaweikai
 * @Date   : 2022/03/26
 * @Desc   :
 */
data class RequestStatus<T :NetData<*>>(
    var code :Int = 0,
    var error:Throwable ?= null,
    var msg:String  ?= null,
    var status:DataState = DataState.STATE_CREATE,
    var json:T? = null
){
    val data get() = json?.data
}

enum class DataState {
    STATE_CREATE,//创建
    STATE_LOADING,//加载中
    STATE_SUCCESS,//成功
    STATE_COMPLETED,//完成
    STATE_EMPTY,//数据为null
    STATE_FAILED,//接口请求成功但是服务器返回error
    STATE_ERROR,//请求失败
    STATE_UNKNOWN//未知
}

val DataState.isLoading: Boolean get() = this == DataState.STATE_LOADING