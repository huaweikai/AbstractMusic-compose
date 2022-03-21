package com.hua.abstractmusic.ui.home.mine

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.MediaItem
import androidx.navigation.NavHostController
import coil.transform.RoundedCornersTransformation
import com.hua.abstractmusic.R
import com.hua.abstractmusic.bean.MediaData
import com.hua.abstractmusic.other.Constant.NULL_MEDIA_ITEM
import com.hua.abstractmusic.ui.LocalComposeUtils
import com.hua.abstractmusic.ui.LocalHomeNavController
import com.hua.abstractmusic.ui.LocalUserViewModel
import com.hua.abstractmusic.ui.home.net.ItemPlayButton
import com.hua.abstractmusic.ui.route.Screen
import com.hua.abstractmusic.ui.utils.CoilImage
import com.hua.abstractmusic.ui.viewmodels.UserViewModel
import com.hua.abstractmusic.utils.PaletteUtils


/**
 * @author : huaweikai
 * @Date   : 2022/03/02
 * @Desc   : \
 */

@SuppressLint("UnsafeOptInUsageError")
@Composable
fun Sheet(
    isLocal: Boolean = true,
    onClick: (String) -> Unit,
    newSheet: (String) -> Unit,
    sheetList: MutableState<List<MediaData>>
) {
    val diaLogState = remember {
        mutableStateOf(false)
    }
    val deleteSheetState = remember {
        mutableStateOf(false)
    }
    val item = remember {
        mutableStateOf(NULL_MEDIA_ITEM)
    }
    Surface(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column {
            Row(
                modifier = Modifier
                    .padding(
                        vertical = 8.dp, horizontal = 12.dp
                    )
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = if (isLocal) "本地歌单" else "在线歌单", fontSize = 16.sp)
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "",
                    modifier = Modifier.clickable {
                        diaLogState.value = true
                    }
                )
            }
            LazyRow(
                modifier = Modifier.padding(vertical = 8.dp),
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                items(
                    sheetList.value
                ) { sheet ->
                    RecommendItem(item = sheet, onclick = {
                        onClick(it.mediaId)
                    }, onPlay = {
                        //todo
                    }) {
                        deleteSheetState.value = true
                        item.value = sheet.mediaItem
                    }
                }
            }
        }
    }
    CreateNewSheet(diaLogState = diaLogState) {
        newSheet(it)
    }
    DeleteSheet(deleteSheetState = deleteSheetState, mediaItem = item.value)
}

@SuppressLint("UnsafeOptInUsageError")
@Composable
fun LocalSheet(
    navHostController: NavHostController = LocalHomeNavController.current,
    userViewModel: UserViewModel = LocalUserViewModel.current
) {
    Sheet(
        onClick = { sheetId ->
            navHostController.navigate("${Screen.LocalSheetDetailScreen.route}?sheetId=$sheetId")
        },
        newSheet = {
            userViewModel.createSheet(it, true)
        },
        sheetList = userViewModel.sheetList
    )
}

@SuppressLint("UnsafeOptInUsageError")
@Composable
fun DeleteSheet(
    deleteSheetState: MutableState<Boolean>,
    mediaItem: MediaItem,
    userViewModel: UserViewModel = LocalUserViewModel.current
) {
    if (deleteSheetState.value) {
        AlertDialog(
            onDismissRequest = { deleteSheetState.value = false },
            title = {
                Text(text = "移出歌单", fontSize = 20.sp)
            },
            text = {
                Text(text = "确定移出${mediaItem.mediaMetadata.title}?")
            },
            confirmButton = {
                Text(text = "同意", Modifier.clickable {
                    userViewModel.deleteSheet(mediaItem.mediaId)
                    deleteSheetState.value = false
                })
            },
            dismissButton = {
                Text(text = "取消", modifier = Modifier.clickable {
                    deleteSheetState.value = false
                })
            }
        )
    }
}

@Composable
fun CreateNewSheet(
    diaLogState: MutableState<Boolean>,
    confirm: (String) -> Unit
) {
    val (sheetName, setSheetName) = remember {
        mutableStateOf("")
    }
    if (diaLogState.value) {
        AlertDialog(
            onDismissRequest = {
                diaLogState.value = false
                setSheetName("")
            },
            confirmButton = {
                Text(
                    text = "添加",
                    modifier = Modifier.clickable {
                        confirm(sheetName)
                        diaLogState.value = false
                        setSheetName("")
                    }
                )
            },
            dismissButton = {
                Text(
                    text = "取消",
                    modifier = Modifier.clickable {
                        diaLogState.value = false
                        setSheetName("")
                    }
                )
            },
            title = {
                Text(text = "添加歌单", fontSize = 20.sp)
            },
            text = {
                TextField(
                    value = sheetName,
                    onValueChange = setSheetName,
                    maxLines = 1
                )
            }
        )
    }
}

@SuppressLint("UnsafeOptInUsageError")
@Composable
private fun RecommendItem(
    item: MediaData,
    onclick: (MediaData) -> Unit,
    onPlay: (MediaData) -> Unit,
    onLongClick: () -> Unit,
) {
    val composeUtils = LocalComposeUtils.current
    val context = LocalContext.current
    val background = remember(item.mediaItem) {
        mutableStateOf(Color.Gray)
    }
    LaunchedEffect(Unit) {
        val bitmap =
            composeUtils.coilToBitmap(item.mediaItem.mediaMetadata.artworkUri)
        val pair =
            PaletteUtils.resolveBitmap(
                false,
                bitmap,
                context.getColor(R.color.black)
            )
        background.value = Color(pair.first)
    }
    Column(
        Modifier
            .width(106.dp)
            .padding(horizontal = 8.dp)
            .height(150.dp)
            .clickable {
                onclick(item)
            },
        verticalArrangement = Arrangement.Top
    ) {
        Box(
            modifier = Modifier
                .height(100.dp),
        ) {
            CoilImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .pointerInput(Unit) {
                        this.detectTapGestures(
                            onLongPress = {
                                onLongClick()
                            },
                            onTap = {
                                onclick(item)
//                                onClick(sheet.mediaId)
                            }
                        )
                    },
                url = item.mediaItem.mediaMetadata.artworkUri,
                contentDescription = "",
                builder = { transformations(RoundedCornersTransformation(10f)) }
            )
            ItemPlayButton(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .align(
                        Alignment.BottomStart
                    )
                    .background(
                        background.value, RoundedCornerShape(50f)
                    )
            ) {
                onPlay(item)
            }
        }
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = "${item.mediaItem.mediaMetadata.title}",
            maxLines = 2,
            fontSize = 14.sp,
            overflow = TextOverflow.Ellipsis
        )
    }
}