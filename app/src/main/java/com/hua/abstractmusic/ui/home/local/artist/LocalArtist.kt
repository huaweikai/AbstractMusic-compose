package com.hua.abstractmusic.ui.home.local.artist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.transform.CircleCropTransformation
import com.hua.abstractmusic.bean.MediaData
import com.hua.abstractmusic.ui.LocalHomeViewModel
import com.hua.abstractmusic.ui.utils.ArtImage
import com.hua.abstractmusic.ui.utils.TitleAndArtist
import com.hua.abstractmusic.ui.viewmodels.HomeViewModel
import com.hua.abstractmusic.utils.albumArtUri
import com.hua.abstractmusic.utils.displayDescription
import com.hua.abstractmusic.utils.title


/**
 * @author : huaweikai
 * @Date   : 2022/01/18
 * @Desc   :
 */
@Composable
fun LocalArtist(
    homeViewModel: HomeViewModel = LocalHomeViewModel.current,
    onClick: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        itemsIndexed(
            homeViewModel.localArtistList.value,
            key = { _, item -> item.mediaId }
        ) { index: Int, item: MediaData ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, bottom = 20.dp)
                    .clickable {
                        onClick(index)
                    }
            ) {
                ArtImage(
                    modifier = Modifier
                        .size(45.dp),
                    uri = item.mediaItem.metadata?.albumArtUri!!,
                    transformation = CircleCropTransformation(),
                    desc = "歌手图像"
                )
                Spacer(modifier = Modifier.padding(horizontal = 5.dp))
                Column(
                    modifier = Modifier.align(Alignment.CenterVertically)
                ) {
                    TitleAndArtist(
                        title = "${item.mediaItem.metadata?.title}",
                        subTitle = "${item.mediaItem.metadata?.displayDescription} 首"
                    )
                }
            }
        }
    }
}