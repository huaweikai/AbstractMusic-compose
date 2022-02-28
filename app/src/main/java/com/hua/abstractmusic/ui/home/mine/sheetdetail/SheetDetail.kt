package com.hua.abstractmusic.ui.home.mine.sheetdetail

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.transform.RoundedCornersTransformation
import com.hua.abstractmusic.bean.MediaData
import com.hua.abstractmusic.ui.utils.ArtImage
import com.hua.abstractmusic.ui.utils.MusicItem


/**
 * @author : huaweikai
 * @Date   : 2022/02/28
 * @Desc   :
 */
@SuppressLint("UnsafeOptInUsageError")
@Composable
fun SheetDetail(
    mediaData: MediaData,
    sheetDetailViewModel: SheetDetailViewModel = hiltViewModel(),
    configuration: Configuration = LocalConfiguration.current
) {
    DisposableEffect(Unit) {
        sheetDetailViewModel.sheetId = mediaData.mediaId
        sheetDetailViewModel.initializeController()
        this.onDispose {
            sheetDetailViewModel.releaseBrowser()
        }
    }

    val item = mediaData.mediaItem.mediaMetadata
    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height((configuration.screenHeightDp * 0.1).dp))
        Column(
            Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ArtImage(
                modifier = Modifier
                    .size(150.dp),
                uri = item.artworkUri,
                desc = "",
                transformation = RoundedCornersTransformation(20f)
            )
            Text(text = "${item.title}", fontSize = 22.sp)
            Text(text = "${item.subtitle ?: "暂无介绍"}")
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            itemsIndexed(sheetDetailViewModel.sheetDetailList.value) { index, item ->
                MusicItem(data = item) {
                    sheetDetailViewModel.setPlaylist(
                        index,
                        sheetDetailViewModel.sheetDetailList.value
                    )
                }
            }
        }
    }
}