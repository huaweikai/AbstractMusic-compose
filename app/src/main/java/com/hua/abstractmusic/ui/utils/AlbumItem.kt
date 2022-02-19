package com.hua.abstractmusic.ui.utils

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.MediaItem
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.transform.RoundedCornersTransformation
import com.hua.abstractmusic.R


/**
 * @author : huaweikai
 * @Date   : 2022/01/19
 * @Desc   :
 */
@androidx.media3.common.util.UnstableApi
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
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
       AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .apply {
                    data(item.mediaMetadata.artworkUri)
                    error(R.drawable.music)
                    transformations(RoundedCornersTransformation(30f))
                }
                .build(),
            contentDescription = "",
            modifier = Modifier
                .size(80.dp)
        )
        Spacer(modifier = Modifier.padding(start = 8.dp))
        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Text(text = "${item.mediaMetadata.title}")
            Text(
                text = "${item.mediaMetadata.artist}",
                fontSize = 14.sp,
                fontWeight = FontWeight.W300
            )
            Text(
                text = "${item.mediaMetadata.trackNumber} 首",
                fontSize = 14.sp,
                fontWeight = FontWeight.W300
            )
        }
    }
}