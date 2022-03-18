package com.hua.abstractmusic.bean

import android.annotation.SuppressLint
import androidx.media3.common.MediaItem

@SuppressLint("UnsafeOptInUsageError")
data class MediaData (
    val mediaItem : MediaItem,
    val isPlaying :Boolean = false,
    val mediaId : String = mediaItem.mediaId
)