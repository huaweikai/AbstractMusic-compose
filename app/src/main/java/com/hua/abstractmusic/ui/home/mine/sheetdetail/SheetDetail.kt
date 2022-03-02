package com.hua.abstractmusic.ui.home.mine.sheetdetail

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.MediaItem
import coil.transform.RoundedCornersTransformation
import com.hua.abstractmusic.bean.CropParams
import com.hua.abstractmusic.bean.MediaData
import com.hua.abstractmusic.other.Constant.NULL_MEDIA_ITEM
import com.hua.abstractmusic.other.NetWork
import com.hua.abstractmusic.ui.LocalPlayingViewModel
import com.hua.abstractmusic.ui.LocalPopWindow
import com.hua.abstractmusic.ui.LocalPopWindowItem
import com.hua.abstractmusic.ui.utils.*
import com.hua.abstractmusic.ui.viewmodels.PlayingViewModel
import com.hua.abstractmusic.utils.CropPhotoContract
import com.hua.abstractmusic.utils.isLocal
import kotlinx.coroutines.launch


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
) {
    DisposableEffect(Unit) {
        sheetDetailViewModel.isLocal = mediaData.mediaId.isLocal()
        sheetDetailViewModel.sheetId = mediaData.mediaId
        sheetDetailViewModel.initializeController()
        this.onDispose {
            sheetDetailViewModel.releaseBrowser()
        }
    }
    val contentResolver = LocalContext.current.contentResolver
    val context = LocalContext.current
    val cropPicture = rememberLauncherForActivityResult(CropPhotoContract()) {
        try {
            if (it == null) {
                Toast.makeText(context, "链接为空了，报告开发者", Toast.LENGTH_SHORT).show()
            } else {
                sheetDetailViewModel.putSheetArt(it.toString(), contentResolver)
            }
        } catch (e: Exception) {
            Toast.makeText(context, "未知错误，报告开发者", Toast.LENGTH_SHORT).show()
        }

    }

    val popState = remember {
        mutableStateOf(false)
    }

    val item = remember {
        mutableStateOf(NULL_MEDIA_ITEM)
    }

    val selectPicture = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        it?.let {
            cropPicture.launch(CropParams(uri = it))
        }
    }
    CompositionLocalProvider(
        LocalPopWindowItem provides item,
        LocalPopWindow provides popState
    ) {
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!mediaData.mediaId.isLocal()) {
                when (sheetDetailViewModel.screenState.value) {
                    is LCE.Success -> {
                        Detail_Net_Success(
                            sheetDetailViewModel
                        ) {
                            selectPicture.launch("image/*")
                        }
                    }
                    is LCE.Error -> {
                        Column(
                            Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(text = "请刷新重试")
                        }
                    }
                    is LCE.Loading -> {
                        CircularProgressIndicator()
                    }
                }
            } else {
                Detail_Success(mediaData = mediaData, sheetDetailViewModel = sheetDetailViewModel)
            }
        }
        SheetPopWindow(
            sheetDetailViewModel = sheetDetailViewModel
        )
    }
}

@SuppressLint("UnsafeOptInUsageError")
@Composable
fun Detail_Success(
    mediaData: MediaData,
    sheetDetailViewModel: SheetDetailViewModel,
    popWindowState: MutableState<Boolean> = LocalPopWindow.current,
    popWindowItem: MutableState<MediaItem> = LocalPopWindowItem.current
) {
    val item = mediaData.mediaItem.mediaMetadata
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
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "${item.title}", fontSize = 22.sp)
        Text(text = "${item.subtitle ?: "暂无介绍"}")
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        itemsIndexed(sheetDetailViewModel.sheetDetailList.value) { index, item ->
            MusicItem(data = item, onMoreClick = {
                popWindowState.value = true
                popWindowItem.value = item.mediaItem
            }, onClick = {
                sheetDetailViewModel.setPlaylist(
                    index,
                    sheetDetailViewModel.sheetDetailList.value
                )
            })
        }
    }
}

@Composable
private fun Detail_Net_Success(
    sheetDetailViewModel: SheetDetailViewModel,
    popWindowState: MutableState<Boolean> = LocalPopWindow.current,
    popWindowItem: MutableState<MediaItem> = LocalPopWindowItem.current,
    onclick: () -> Unit
) {
    val item = sheetDetailViewModel.sheetDetail.collectAsState().value
    Column(
        Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ArtImage(
            modifier = Modifier
                .size(150.dp)
                .clickable {
                    onclick()

                },
            uri = item.artUri,
            desc = "",
            transformation = RoundedCornersTransformation(20f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = item.title, fontSize = 22.sp)
        Text(text = item.sheetDesc ?: "暂无介绍")
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        itemsIndexed(sheetDetailViewModel.sheetDetailList.value) { index, item ->
            MusicItem(data = item,
                onMoreClick = {
                    popWindowState.value = true
                    popWindowItem.value = item.mediaItem
                }, onClick = {
                    sheetDetailViewModel.setPlaylist(
                        index,
                        sheetDetailViewModel.sheetDetailList.value
                    )
                }
            )
        }
    }
}

@SuppressLint("UnsafeOptInUsageError")
@Composable
fun SheetPopWindow(
    sheetDetailViewModel: SheetDetailViewModel,
    state: MutableState<Boolean> = LocalPopWindow.current,
    item: MediaItem = LocalPopWindowItem.current.value,
    viewModel: PlayingViewModel = LocalPlayingViewModel.current,
    config: Configuration = LocalConfiguration.current,
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    if (state.value) {
        Dialog(onDismissRequest = { state.value = false }) {
            Column(
                modifier = Modifier
                    .width((config.screenWidthDp * 0.75).dp)
                    .heightIn(max = (config.screenHeightDp * 0.6).dp)
                    .padding(horizontal = 8.dp)
                    .background(MaterialTheme.colorScheme.background, RoundedCornerShape(12.dp))
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(90.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.width(8.dp))
                    ArtImage(
                        modifier = Modifier.size(80.dp),
                        uri = item.mediaMetadata.artworkUri,
                        desc = "",
                        transformation = RoundedCornersTransformation(16f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(Modifier.fillMaxHeight(), verticalArrangement = Arrangement.Center) {
                        TitleAndArtist(
                            title = "${item.mediaMetadata.title}",
                            subTitle = "${item.mediaMetadata.artist}",
                            height = 4.dp
                        )
                    }
                }
                Divider()
                Spacer(modifier = Modifier.height(8.dp))
                PopItem(desc = "添加到播放队列") {
                    viewModel.addQueue(item)
                }
                PopItem(desc = "添加到下一曲播放") {
                    viewModel.addQueue(item, true)
                }
                PopItem(desc = "移出当前歌单") {
                    scope.launch {
                        val result = sheetDetailViewModel.removeNetSheetItem(item.mediaId)
                        if (result.code != NetWork.SUCCESS) {
                            Toast.makeText(context, result.msg, Toast.LENGTH_SHORT).show()
                        }
                    }
                    state.value = false
                }
            }
        }
    }
}