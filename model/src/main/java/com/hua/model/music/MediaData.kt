package com.hua.model.music

import android.annotation.SuppressLint
import androidx.media3.common.MediaItem

@SuppressLint("UnsafeOptInUsageError")
data class MediaData (
    val mediaItem : MediaItem,
    val isPlaying :Boolean = false,
    val mediaId : String = mediaItem.mediaId
)