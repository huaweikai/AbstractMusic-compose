package com.hua.service.usecase.events

/**
 * @author : huaweikai
 * @Date   : 2021/11/24
 * @Desc   : 自定义抛出错误
 *
 */
class MusicInsertError(message:String):Throwable(message)