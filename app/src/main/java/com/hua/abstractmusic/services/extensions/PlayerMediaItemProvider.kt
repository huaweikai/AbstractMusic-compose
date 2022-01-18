package com.hua.abstractmusic.services.extensions

import androidx.media2.common.MediaItem
import androidx.media2.session.MediaSession
import com.google.android.exoplayer2.ext.media2.SessionCallbackBuilder
import com.hua.abstractmusic.services.MediaItemTree

/**
 * @author Xiaoc
 * @since 2021-11-14
 *
 * MediaItem的提供器实现，通过mediaId来查询对应mediaId的[MediaItem]对象内容
 * 用于给播放器提供具体播放信息
 * 这里使用[MediaItemTree]来查询对应媒体数据并返回结果
 */
class PlayerMediaItemProvider(
    private val mediaItemTree: MediaItemTree
): SessionCallbackBuilder.MediaItemProvider {

    override fun onCreateMediaItem(
        session: MediaSession,
        controllerInfo: MediaSession.ControllerInfo,
        mediaId: String
    ): MediaItem? {
        return mediaItemTree.getItem(mediaId)
    }
}