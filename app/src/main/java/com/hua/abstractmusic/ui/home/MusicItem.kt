package com.hua.abstractmusic.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.media2.common.MediaItem
import androidx.media2.common.MediaMetadata
import coil.compose.rememberImagePainter
import com.hua.abstractmusic.R
import com.hua.abstractmusic.bean.MediaData
import com.hua.abstractmusic.ui.home.viewmodels.HomeViewModel
import com.hua.abstractmusic.utils.albumArtUri
import com.hua.abstractmusic.utils.browserType
import com.hua.abstractmusic.utils.isPlayable
import com.hua.abstractmusic.utils.title


/**
 * @author : huaweikai
 * @Date   : 2022/01/11
 * @Desc   : item
 */
@Composable
fun MusicItem(
    data:MediaData,
    onClick:()->Unit
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(bottom = 10.dp)
            .clickable {
                onClick()
            }
    ) {
        Image(
            painter = rememberImagePainter(
                data = data.mediaItem.metadata?.albumArtUri
            ) {
                this.error(R.drawable.music)
            },
            contentDescription = "专辑图",
            modifier = Modifier
                .height(60.dp)
                .width(60.dp)
        )
        Text(
            text = "${data.mediaItem.metadata?.title}",
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(start = 8.dp),
            color = if(data.isPlaying) Color(0xff77D3D0) else Color.Black
        )
    }
}