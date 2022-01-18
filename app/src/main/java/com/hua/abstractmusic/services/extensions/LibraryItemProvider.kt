package com.hua.abstractmusic.services.extensions

import androidx.annotation.IntRange
import androidx.media2.common.BaseResult
import androidx.media2.session.LibraryResult
import androidx.media2.session.MediaLibraryService.LibraryParams
import androidx.media2.session.MediaLibraryService.MediaLibrarySession
import androidx.media2.session.MediaSession

/**
 * @author Xiaoc
 * @since 2021-11-14
 *
 * LibraryService的Item项提供者
 */
interface LibraryItemProvider{

    /**
     * 当 MediaBrowser 调用 libraryRoot 命令时回调此方法
     * 用于访问Library资料库的根内容
     * 如果你允许继续访问下面的内容，你可以返回[LibraryResult.RESULT_SUCCESS]内容，并提供一个有效的 root media item（含 media id）
     * 这个 media id 将是访问它的子内容的关键字段
     * @param session 会话对象
     * @param controller 相关信息
     * @param params 携带的额外参数
     */
    fun onGetLibraryRoot(
        session: MediaLibrarySession,
        controller: MediaSession.ControllerInfo,
        params: LibraryParams?
    ): LibraryResult

    /**
     * 当 MediaBrowser 调用 getItem 命令时回调此方法
     * 用于访问对应mediaId的详情信息（例如一个歌手id查询该歌手的详情信息）
     * 如果你允许继续访问下面的内容，你可以返回[LibraryResult.RESULT_SUCCESS]内容
     * @param session 会话对象
     * @param controller 相关信息
     * @param mediaId 要查询的详情内容的mediaId
     */
    fun onGetItem(
        session: MediaLibrarySession,
        controller: MediaSession.ControllerInfo,
        mediaId: String
    ): LibraryResult

    /**
     * 当 MediaBrowser 调用 getChildren 命令时回调此方法
     * 用于访问对应mediaId的字内容列表信息（例如一个歌手id查询该歌手的所有子内容）
     * 如果你允许继续访问下面的内容，你可以返回[LibraryResult.RESULT_SUCCESS]内容
     * @param session 会话对象
     * @param controller 相关信息
     * @param parentId 要查询的详情列表的mediaId
     * @param page 页码
     * @param pageSize 页大小
     * @param params 额外参数
     */
    fun onGetChildren(
        session: MediaLibrarySession,
        controller: MediaSession.ControllerInfo,
        parentId: String,
        @IntRange(from = 0) page: Int,
        @IntRange(from = 1) pageSize: Int,
        params: LibraryParams?
    ): LibraryResult

    /**
     * 当 MediaBrowser 调用 subscribe 命令时回调此方法
     * 用于订阅对应mediaId的内容信息，当内容信息出现更改时，会回调具体方法进行更新
     * 如果你允许继续访问下面的内容，你可以返回[LibraryResult.RESULT_SUCCESS]内容
     * @param session 会话对象
     * @param controller 相关信息
     * @param parentId 要订阅的内容的mediaId
     * @param params 额外参数
     */
    @LibraryResult.ResultCode
    fun onSubscribe(
        session: MediaLibrarySession,
        controller: MediaSession.ControllerInfo,
        parentId: String,
        params: LibraryParams?
    ): Int

    /**
     * 当 MediaBrowser 调用 unsubscribe 命令时回调此方法
     * 用于取消订阅对应mediaId的内容信息
     * 如果你允许继续访问下面的内容，你可以返回[LibraryResult.RESULT_SUCCESS]内容
     * @param session 会话对象
     * @param controller 相关信息
     * @param parentId 要订阅的内容的mediaId
     */
    @LibraryResult.ResultCode
    fun onUnsubscribe(
        session: MediaLibrarySession,
        controller: MediaSession.ControllerInfo,
        parentId: String
    ): Int

    @LibraryResult.ResultCode
    fun onSearch(
        session: MediaLibrarySession,
        controller: MediaSession.ControllerInfo,
        query: String,
        params: LibraryParams?
    ): Int

    fun onGetSearchResult(
        session: MediaLibrarySession,
        controller: MediaSession.ControllerInfo,
        query: String,
        @IntRange(from = 0) page: Int,
        @IntRange(from = 1) pageSize: Int,
        params: LibraryParams?
    ): LibraryResult

}