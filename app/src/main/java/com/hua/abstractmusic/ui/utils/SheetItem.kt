package com.hua.abstractmusic.ui.utils

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.MediaItem
import coil.transform.RoundedCornersTransformation

/**
 * @author : huaweikai
 * @Date   : 2022/03/18
 * @Desc   :
 */
@SuppressLint("UnsafeOptInUsageError")
@Composable
fun SheetItem(
    item: MediaItem,
    onClick:(MediaItem)->Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .clickable {
                onClick(item)
            }
    ) {
        CoilImage(
            url = item.mediaMetadata.artworkUri,
            modifier = Modifier.size(56.dp),
            builder = {
                transformations(RoundedCornersTransformation(8f))
            },
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "${item.mediaMetadata.title}", fontSize = 16.sp)
            Text(
                text = "${item.mediaMetadata.trackNumber}首, by ${item.mediaMetadata.artist}",
                fontSize = 16.sp
            )
        }
    }
}