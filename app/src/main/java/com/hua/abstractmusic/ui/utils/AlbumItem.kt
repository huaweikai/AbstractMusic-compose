package com.hua.abstractmusic.ui.utils

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.media2.common.MediaItem
import coil.compose.rememberImagePainter
import coil.transform.RoundedCornersTransformation
import com.hua.abstractmusic.R
import com.hua.abstractmusic.utils.*


/**
 * @author : huaweikai
 * @Date   : 2022/01/19
 * @Desc   :
 */
@Composable
fun AlbumItem(
    item: MediaItem,
    modifier: Modifier = Modifier,
    onClick:()->Unit
) {
    Row(
        modifier = modifier
            .clickable {
                onClick()
            }
    ) {
        Image(
            painter = rememberImagePainter(data = item.metadata?.albumArtUri) {
                this.error(R.drawable.music)
                this.transformations(RoundedCornersTransformation(30f))
            },
            contentDescription = "",
            modifier = Modifier
                .size(80.dp)
        )
        Spacer(modifier = Modifier.padding(start = 5.dp))
        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Text(text = "${item.metadata?.title}")
            Text(text = "${item.metadata?.artist}")
            Text(text = "${item.metadata?.trackCount}")
        }
    }
}