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
import androidx.compose.material3.*
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
import androidx.navigation.NavHostController
import coil.transform.RoundedCornersTransformation
import com.hua.abstractmusic.bean.ParcelizeMediaItem
import com.hua.abstractmusic.bean.toNavType
import com.hua.abstractmusic.other.Constant.NULL_MEDIA_ITEM
import com.hua.abstractmusic.other.NetWork
import com.hua.abstractmusic.ui.LocalAppNavController
import com.hua.abstractmusic.ui.LocalBottomControllerHeight
import com.hua.abstractmusic.ui.LocalPopWindow
import com.hua.abstractmusic.ui.LocalPopWindowItem
import com.hua.abstractmusic.ui.route.Screen
import com.hua.abstractmusic.ui.utils.*
import com.hua.abstractmusic.utils.getCacheDir
import com.hua.abstractmusic.utils.isLocal
import kotlinx.coroutines.launch


/**
 * @author : huaweikai
 * @Date   : 2022/02/28
 * @Desc   :
 */
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnsafeOptInUsageError")
@Composable
fun SheetDetail(
    mediaItem: ParcelizeMediaItem,
    navController: NavHostController = LocalAppNavController.current,
    sheetDetailViewModel: SheetDetailViewModel = hiltViewModel(),
) {
    DisposableEffect(Unit) {
        sheetDetailViewModel.parcelItem = mediaItem
        sheetDetailViewModel.loadData()
        this.onDispose {
            sheetDetailViewModel.removeListener()
        }
    }
    val contentResolver = LocalContext.current.contentResolver
    val context = LocalContext.current
//    val cropPicture = rememberLauncherForActivityResult(CropPhotoContract()) {
//        try {
//            if (it == null) {
//                Toast.makeText(context, "链接为空了，报告开发者", Toast.LENGTH_SHORT).show()
//            } else {
//                sheetDetailViewModel.putSheetArt(it.toString(), contentResolver)
//            }
//        } catch (e: Exception) {
//            Toast.makeText(context, "未知错误，报告开发者", Toast.LENGTH_SHORT).show()
//        }
//    }


    val cropPicture = rememberLauncherForActivityResult(UCropActivityResultContract()) {
        if (it != null) {
            sheetDetailViewModel.putSheetArt(it.toString(), contentResolver)
        } else {
            Toast.makeText(context, "连接为空", Toast.LENGTH_SHORT).show()
        }
    }

    val selectPicture = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        it?.let {
            val outputUri = getCacheDir(context, it)
            cropPicture.launch(Pair(it, outputUri!!))
        }
    }

    val popState = remember {
        mutableStateOf(false)
    }

    val item = remember {
        mutableStateOf(NULL_MEDIA_ITEM)
    }

//    val selectPicture = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
//        it?.let {
//            cropPicture.launch(CropParams(uri = it))
//        }
//    }
    val state = sheetDetailViewModel.screenState.collectAsState()
    CompositionLocalProvider(
        LocalPopWindowItem provides item,
        LocalPopWindow provides popState
    ) {
        val snackbarHostState = remember{ SnackbarHostState()}
        Scaffold(
            topBar = {
                SmallTopAppBar(
                    title = { Text(text = "歌单") },
                    navigationIcon = {
                        NavigationBack {
                            navController.navigateUp()
                        }
                    },
                    modifier = Modifier.statusBarsPadding()
                )
            },
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState){
                    Snackbar {
                        Text(text = it.visuals.message)
                    }
                }
            }
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(it),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (!mediaItem.mediaId.isLocal()) {
                    when (state.value) {
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
                    Detail_Success(
                        item = mediaItem,
                        sheetDetailViewModel = sheetDetailViewModel
                    )
                }
            }
            SheetPopWindow(
                sheetDetailViewModel = sheetDetailViewModel,
                snackbarHostState = snackbarHostState
            )
        }
    }
}

@SuppressLint("UnsafeOptInUsageError")
@Composable
fun Detail_Success(
    item: ParcelizeMediaItem,
    sheetDetailViewModel: SheetDetailViewModel,
    popWindowState: MutableState<Boolean> = LocalPopWindow.current,
    popWindowItem: MutableState<MediaItem> = LocalPopWindowItem.current
) {
    Column(
        Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ArtImage(
            modifier = Modifier
                .size(150.dp),
            uri = item.artUri,
            desc = "",
            transformation = RoundedCornersTransformation(20f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = item.title, fontSize = 22.sp)
        Text(text = if(item.desc.isNullOrBlank())"暂无介绍" else item.desc)
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
                sheetDetailViewModel.setPlayList(
                    index,
                    sheetDetailViewModel.sheetDetailList.value.map { it.mediaItem }
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
            .padding(PaddingValues(bottom = LocalBottomControllerHeight.current.coerceAtLeast(16.dp)))
    ) {
        itemsIndexed(sheetDetailViewModel.sheetDetailList.value) { index, item ->
            MusicItem(data = item,
                onMoreClick = {
                    popWindowState.value = true
                    popWindowItem.value = item.mediaItem
                }, onClick = {
                    sheetDetailViewModel.setPlayList(
                        index,
                        sheetDetailViewModel.sheetDetailList.value.map { it.mediaItem }
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
    snackbarHostState:SnackbarHostState,
    state: MutableState<Boolean> = LocalPopWindow.current,
    item: MediaItem = LocalPopWindowItem.current.value,
    config: Configuration = LocalConfiguration.current,
    navController: NavHostController = LocalAppNavController.current
) {
    val scope = rememberCoroutineScope()
    val artistPop = remember{ mutableStateOf(false)}
    val sheetPop = remember { mutableStateOf(false)}
    val context = LocalContext.current
    if (state.value) {
        sheetDetailViewModel.selectAlbumByMusicId(item)
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
                    sheetDetailViewModel.addQueue(item)
                }
                PopItem(desc = "添加到下一曲播放") {
                    sheetDetailViewModel.addQueue(item, true)
                }
                PopItem(desc = "添加到歌单") {
                    sheetDetailViewModel.refresh(item.mediaId.isLocal())
                    state.value = false
                    sheetPop.value = true
                }
                PopItem(desc = "歌手:${item.mediaMetadata.artist}") {
                    sheetDetailViewModel.selectArtistByMusicId(item)
                    state.value = false
                    artistPop.value = true
                }
                PopItem(desc = "专辑:${item.mediaMetadata.albumTitle}") {
                    state.value = false
                    navController.navigate("${Screen.AlbumDetailScreen.route}?mediaItem=${sheetDetailViewModel.moreAlbum.value.toNavType()}")
                    sheetDetailViewModel.clearAlbum()
                }
                if(sheetDetailViewModel.hasPermission()){
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

    PopItemLayout(state = sheetPop, onDismiss = {}, title = {
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "歌单", fontSize = 22.sp, modifier = Modifier.padding(start = 16.dp))
        Spacer(modifier = Modifier.height(16.dp))
    }, popItems = sheetDetailViewModel.sheetList.value.map {
        PopItems("${it.mediaMetadata.title}") {
            scope.launch {
                sheetDetailViewModel.insertMusicToSheet(
                    mediaItem = item,
                    sheetItem = it
                ).let {
                    snackbarHostState.showSnackbar(it.second)
                }
                sheetPop.value = false
            }
        }
    })

    if (sheetDetailViewModel.moreArtistList.value.isNotEmpty()) {
        PopItemLayout(state = artistPop, onDismiss = { sheetDetailViewModel.clearArtist() }, title = {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "歌手", fontSize = 22.sp, modifier = Modifier.padding(start = 16.dp))
            Spacer(modifier = Modifier.height(16.dp))
        }, popItems = sheetDetailViewModel.moreArtistList.value.map {
            PopItems(title = "${it.mediaMetadata.title}") {
                artistPop.value = false
                navController.navigate("${Screen.ArtistDetailScreen.route}?mediaItem=${it.toNavType()}")
                sheetDetailViewModel.clearArtist()
            }
        })
    }
}