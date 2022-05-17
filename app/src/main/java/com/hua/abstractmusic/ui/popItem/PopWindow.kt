package com.hua.abstractmusic.ui.utils

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.MediaItem
import androidx.navigation.NavHostController
import coil.transform.RoundedCornersTransformation
import com.hua.abstractmusic.ui.LocalAppNavController
import com.hua.abstractmusic.ui.LocalPopWindow
import com.hua.abstractmusic.ui.LocalPopWindowItem
import com.hua.abstractmusic.ui.popItem.PopItemViewModel
import com.hua.abstractmusic.ui.popItem.SnackData
import com.hua.abstractmusic.ui.route.Screen
import com.hua.abstractmusic.utils.isLocal
import com.hua.model.parcel.toNavType
import kotlinx.coroutines.launch


/**
 * @author : huaweikai
 * @Date   : 2022/03/26
 * @Desc   :
 */
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnsafeOptInUsageError")
@Composable
fun PopupWindow(
    state: MutableState<Boolean> = LocalPopWindow.current,
    item: MediaItem = LocalPopWindowItem.current.value,
    viewModel: PopItemViewModel = hiltViewModel(),
    homeNavController: NavHostController = LocalAppNavController.current,
    snackBarHostState: SnackbarHostState
) {
    val sheetPop = remember {
        mutableStateOf(false)
    }
    val artistPop = remember {
        mutableStateOf(false)
    }
    val scope = rememberCoroutineScope()
    viewModel.selectAlbumByMusicId(item)

    val snackData = viewModel.snackEvent.collectAsState(initial = SnackData(message = ""))

    LaunchedEffect(snackData.value) {
        if (snackData.value.message.isNotBlank()) {
            snackBarHostState.showSnackbar(snackData.value.message)
        }
    }


    PopItemLayout(state = state, onDismiss = { viewModel.clearAlbum() }, title = {
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
    }, popItems = listOf(
        PopItems("添加到播放队列") {
            viewModel.addQueue(item)
            state.value = false
        },
        PopItems("添加到下一曲播放") {
            viewModel.addQueue(item, true)
            state.value = false
        },
        PopItems("添加到歌单") {
            scope.launch {
                viewModel.refresh(item.mediaId.isLocal())
                state.value = false
                sheetPop.value = true
            }
        }, PopItems("歌手:${item.mediaMetadata.artist}") {
            viewModel.selectArtistByMusicId(item)
            state.value = false
            artistPop.value = true
        }, PopItems("专辑:${item.mediaMetadata.albumTitle}") {
            state.value = false
            homeNavController.navigate("${Screen.AlbumDetailScreen.route}?mediaItem=${viewModel.moreAlbum.value.toNavType()}")
            viewModel.clearAlbum()
        }
    ))
    PopItemLayout(state = sheetPop, onDismiss = {}, title = {
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "歌单", fontSize = 22.sp, modifier = Modifier.padding(start = 16.dp))
        Spacer(modifier = Modifier.height(16.dp))
    }, popItems = viewModel.sheetList.value.map {
        PopItems("${it.mediaMetadata.title}") {
            sheetPop.value = false
            viewModel.insertMusicToSheet(
                mediaItem = item,
                sheetItem = it
            )
        }
    })

    if (viewModel.moreArtistList.value.isNotEmpty()) {
        PopItemLayout(state = artistPop, onDismiss = { viewModel.clearArtist() }, title = {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "歌手", fontSize = 22.sp, modifier = Modifier.padding(start = 16.dp))
            Spacer(modifier = Modifier.height(16.dp))
        }, popItems = viewModel.moreArtistList.value.map {
            PopItems(title = "${it.mediaMetadata.title}") {
                artistPop.value = false
                homeNavController.navigate("${Screen.ArtistDetailScreen.route}?mediaItem=${it.toNavType()}")
                viewModel.clearArtist()
            }
        })
    }
}


@Composable
fun PopItemLayout(
    state: MutableState<Boolean>,
    onDismiss: () -> Unit,
    title: @Composable ColumnScope.() -> Unit,
    popItems: List<PopItems>
) {
    val config: Configuration = LocalConfiguration.current
    if (state.value) {
        Dialog(
            onDismissRequest = {
                state.value = false
                onDismiss()
            }
        ) {
            Column(
                modifier = Modifier
                    .width((config.screenWidthDp * 0.75).dp)
                    .heightIn(max = (config.screenHeightDp * 0.6).dp)
                    .padding(horizontal = 8.dp)
                    .background(MaterialTheme.colorScheme.background, RoundedCornerShape(12.dp))
            ) {
                title()
                Divider(
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                    thickness = 0.5.dp
                )
                LazyColumn(Modifier.fillMaxWidth()) {
                    items(popItems) {
                        PopItem(desc = it.title) {
                            it.onClick()
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PopItem(
    desc: String,
    onClick: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .height(50.dp)
            .background(
                MaterialTheme.colorScheme.background,
                RoundedCornerShape(8.dp)
            )
            .clickable {
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = desc,
            fontSize = 16.sp,
            maxLines = 1,
            textAlign = TextAlign.Start,
        )
    }
}

data class PopItems(
    val title: String,
    val onClick: () -> Unit
)