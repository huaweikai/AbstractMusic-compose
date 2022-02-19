package com.hua.abstractmusic.bean

import androidx.media3.common.MediaItem

@androidx.media3.common.util.UnstableApi
data class MediaData (
    val mediaItem : MediaItem,
    val isPlaying :Boolean = false,
    val mediaId : String = mediaItem.mediaId
)