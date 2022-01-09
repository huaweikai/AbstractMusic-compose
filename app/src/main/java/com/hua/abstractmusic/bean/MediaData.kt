package com.hua.abstractmusic.bean


import androidx.media2.common.MediaItem


data class MediaData (
    val mediaItem :MediaItem,
    val isPlaying :Boolean = false,
    val mediaId : String = mediaItem.metadata?.mediaId ?:""
)