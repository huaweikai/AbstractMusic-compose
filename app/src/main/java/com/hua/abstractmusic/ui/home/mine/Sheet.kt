package com.hua.abstractmusic.ui.home.mine

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.MediaItem
import androidx.navigation.NavHostController
import coil.transform.RoundedCornersTransformation
import com.hua.abstractmusic.bean.MediaData
import com.hua.abstractmusic.other.Constant.NULL_MEDIA_ITEM
import com.hua.abstractmusic.ui.LocalHomeNavController
import com.hua.abstractmusic.ui.LocalUserViewModel
import com.hua.abstractmusic.ui.route.Screen
import com.hua.abstractmusic.ui.utils.ArtImage
import com.hua.abstractmusic.ui.viewmodels.UserViewModel


/**
 * @author : huaweikai
 * @Date   : 2022/03/02
 * @Desc   : \
 */

@SuppressLint("UnsafeOptInUsageError")
@Composable
fun Sheet(
    isLocal: Boolean = true,
    onClick: (Int) -> Unit,
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
    Column(
        Modifier
            .height(140.dp)
            .fillMaxWidth(0.9f)
            .background(
                MaterialTheme.colorScheme.background.copy(0.2f),
                RoundedCornerShape(12.dp)
            ),
    ) {
        Row(
            modifier = Modifier
                .height(25.dp)
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
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            itemsIndexed(
                sheetList.value
            ) { index, sheet ->
                Column(
                    modifier = Modifier
                        .height(100.dp)
                        .padding(horizontal = 8.dp)
                        .width(70.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ArtImage(
                        modifier = Modifier
                            .size(70.dp)
                            .pointerInput(Unit) {
                                this.detectTapGestures(
                                    onLongPress = {
                                        deleteSheetState.value = true
                                        item.value = sheet.mediaItem
                                    },
                                    onTap = {
                                        onClick(index)
                                    }
                                )
                            },
                        uri = sheet.mediaItem.mediaMetadata.artworkUri,
                        desc = "",
                        transformation = RoundedCornersTransformation(15f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "${sheet.mediaItem.mediaMetadata.title}")
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
        onClick = { index ->
            navHostController.navigate("${Screen.LocalSheetDetailScreen.route}?sheetIndex=$index&isLocal=true")
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
