package com.hua.abstractmusic.ui.home.mine.sheetdetail

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.util.Base64
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.Popup
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.MediaItem
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.transform.RoundedCornersTransformation
import com.google.gson.Gson
import com.hua.abstractmusic.other.Constant.NULL_MEDIA_ITEM
import com.hua.abstractmusic.ui.LocalAppNavController
import com.hua.abstractmusic.ui.LocalBottomControllerHeight
import com.hua.abstractmusic.ui.LocalPopWindow
import com.hua.abstractmusic.ui.LocalPopWindowItem
import com.hua.abstractmusic.ui.route.Screen
import com.hua.abstractmusic.ui.utils.*
import com.hua.abstractmusic.utils.getCacheDir
import com.hua.abstractmusic.utils.isLocal
import com.hua.model.parcel.toGson
import com.hua.model.parcel.toNavType
import com.hua.network.onFailure
import com.hua.network.utils.toJsonString
import kotlinx.coroutines.launch
import java.util.*


/**
 * @author : huaweikai
 * @Date   : 2022/02/28
 * @Desc   :
 */
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnsafeOptInUsageError")
@Composable
fun SheetDetail(
    sheetDetailViewModel: SheetDetailViewModel = hiltViewModel(),
) {
    val popState = remember {
        mutableStateOf(false)
    }

    val item = remember {
        mutableStateOf(NULL_MEDIA_ITEM)
    }

    CompositionLocalProvider(
        LocalPopWindowItem provides item,
        LocalPopWindow provides popState
    ) {
        val sheetNavHostController = rememberNavController()
        NavHost(navController = sheetNavHostController, startDestination = "detail") {
            composable("detail") {
                NavSheetDetail(
                    sheetDetailViewModel = sheetDetailViewModel,
                    sheetNavHostController
                )
            }
            composable("change") {
                SheetChangeScreen(
                    sheetNavHostController = sheetNavHostController,
                    sheetDetailViewModel = sheetDetailViewModel
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavSheetDetail(
    sheetDetailViewModel: SheetDetailViewModel,
    sheetNavHostController: NavHostController,
    appNavHostController: NavHostController = LocalAppNavController.current
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val popState = remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        sheetDetailViewModel.refreshSheetDesc()
    }
    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text(text = "歌单") },
                navigationIcon = {
                    NavigationBack {
                        appNavHostController.navigateUp()
                    }
                },
                actions = {
//                    if (sheetDetailViewModel.hasPermission()) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "",
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .clickable {
                                    popState.value = true
                                }
                        )
//                    }
                },
                modifier = Modifier.statusBarsPadding()
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.padding(
                    PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = LocalBottomControllerHeight.current.coerceAtLeast(80.dp)
                    )
                )
            ) {
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
            if (!sheetDetailViewModel.isLocal) {
                NetSheetDetail(
                    sheetDetailViewModel = sheetDetailViewModel,
                    navController = appNavHostController
                )
            } else {
                LocalSheetDetail(
                    sheetDetailViewModel = sheetDetailViewModel,
                    navController = appNavHostController
                )
            }
            if (popState.value) {
                Popup(
                    onDismissRequest = {
                        popState.value = false
                    },
                    alignment = Alignment.TopEnd
                ) {
                    Column(
                        modifier = Modifier
                            .statusBarsPadding()
                            .background(
                                MaterialTheme.colorScheme.secondaryContainer,
                                shape = RoundedCornerShape(8.dp)
                            )
                    ) {
                        if(sheetDetailViewModel.hasPermission()){
                            MorePopItem(icon = Icons.Default.Edit, title = "修改歌单") {
                                sheetNavHostController.navigate("change")
                            }
                            MorePopItem(icon = Icons.Default.Delete, title = "删除歌单") {
                                sheetDetailViewModel.deleteSheet()
                                appNavHostController.navigateUp()
                            }
                        }
                        val clipboardManager = LocalClipboardManager.current
//                        if(!sheetDetailViewModel.isLocal){
                            MorePopItem(icon = Icons.Rounded.Share, title = "分享歌单") {
                                sheetDetailViewModel.parcelItem?.let {
                                    val share = it.toGson()
                                    clipboardManager.setText(AnnotatedString(
//                                        it.toGson()
                                    Base64.encodeToString(share.toByteArray(),Base64.DEFAULT)
                                    ))

                                    popState.value = false
                                    sheetDetailViewModel.showSnackBar("已复制到粘贴板")
                                }
                            }
//                        }
                    }
                }
            }
        }
        MediaPopWindow(
            sheetDetailViewModel = sheetDetailViewModel
        )
    }
    val snack = sheetDetailViewModel.snackBarTitle.collectAsState(initial = "")
    LaunchedEffect(snack.value) {
        if (snack.value.isNotBlank()) {
            snackbarHostState.showSnackbar(snack.value)
        }
    }

}




@Composable
fun NetSheetDetail(
    sheetDetailViewModel: SheetDetailViewModel,
    navController: NavHostController,
    popWindowState: MutableState<Boolean> = LocalPopWindow.current,
    popWindowItem: MutableState<MediaItem> = LocalPopWindowItem.current
) {
    val state = sheetDetailViewModel.screenState.collectAsState()
    LazyColumn(
        modifier = Modifier.padding(
            PaddingValues(
                bottom = LocalBottomControllerHeight.current.coerceAtLeast(
                    16.dp
                )
            )
        )
    ) {
        item {
            Column(
                Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SheetImgAndDesc(sheetDetailViewModel, navController)
            }
        }
        item {
            Spacer(modifier = Modifier.height(8.dp))
            when (state.value) {
                is LCE.Error -> {
                    Error {
                        sheetDetailViewModel.loadData()
                    }
                }
                is LCE.Loading -> {
                    Loading()
                }
                is LCE.Success -> {}
            }
        }
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
fun LocalSheetDetail(
    sheetDetailViewModel: SheetDetailViewModel,
    navController: NavHostController,
    popWindowState: MutableState<Boolean> = LocalPopWindow.current,
    popWindowItem: MutableState<MediaItem> = LocalPopWindowItem.current
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        item {
            SheetImgAndDesc(sheetDetailViewModel, navController)
        }
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
fun SheetImgAndDesc(
    viewModel: SheetDetailViewModel,
    navController: NavHostController
) {
    val item = viewModel.sheetDetail.collectAsState().value
    val subTitle = if (item.sheetDesc.isNullOrBlank()) "暂无介绍" else item.sheetDesc
    Column(
        Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CoilImage(
            modifier = Modifier
                .size(150.dp)
                .clip(RoundedCornerShape(16.dp)),
            url = item.artUri,
            contentDescription = "",
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = item.title, fontSize = 22.sp, lineHeight = 26.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = subTitle!!,
            fontSize = 18.sp,
            lineHeight = 22.sp,
            modifier = Modifier.clickable {
                navController.navigate("${Screen.OtherDetail.route}?item=${viewModel.getSheetDesc()}")
            }
        )
    }
}

@SuppressLint("UnsafeOptInUsageError")
@Composable
fun MediaPopWindow(
    sheetDetailViewModel: SheetDetailViewModel,
    state: MutableState<Boolean> = LocalPopWindow.current,
    item: MediaItem = LocalPopWindowItem.current.value,
    config: Configuration = LocalConfiguration.current,
    navController: NavHostController = LocalAppNavController.current
) {
    val artistPop = remember { mutableStateOf(false) }
    val sheetPop = remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
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
                    scope.launch {
                        sheetDetailViewModel.refreshSheetList(item.mediaId.isLocal())
                        state.value = false
                        sheetPop.value = true
                    }
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
                if (sheetDetailViewModel.hasPermission()) {
                    PopItem(desc = "移出当前歌单") {
                        scope.launch {
                            val result = sheetDetailViewModel.removeNetSheetItem(item.mediaId)
                            result.onFailure {
                                Toast.makeText(context, it.errorMsg, Toast.LENGTH_SHORT).show()
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
            sheetPop.value = false
            sheetDetailViewModel.insertMusicToSheet(
                mediaItem = item,
                sheetItem = it
            )
        }
    })

    if (sheetDetailViewModel.moreArtistList.value.isNotEmpty()) {
        PopItemLayout(
            state = artistPop,
            onDismiss = { sheetDetailViewModel.clearArtist() },
            title = {
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "歌手", fontSize = 22.sp, modifier = Modifier.padding(start = 16.dp))
                Spacer(modifier = Modifier.height(16.dp))
            },
            popItems = sheetDetailViewModel.moreArtistList.value.map {
                PopItems(title = "${it.mediaMetadata.title}") {
                    artistPop.value = false
                    navController.navigate("${Screen.ArtistDetailScreen.route}?mediaItem=${it.toNavType()}")
                    sheetDetailViewModel.clearArtist()
                }
            })
    }
}

@Composable
fun MorePopItem(
    icon: ImageVector,
    title: String,
    onclick: () -> Unit
) {
    Row(
        modifier = Modifier
            .height(32.dp)
            .fillMaxWidth(0.5f)
            .padding(horizontal = 8.dp)
            .clickable {
                onclick()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = title)
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = title)
    }
}