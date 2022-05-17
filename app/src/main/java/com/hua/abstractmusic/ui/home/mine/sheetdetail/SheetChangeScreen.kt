package com.hua.abstractmusic.ui.home.mine.sheetdetail

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.transform.RoundedCornersTransformation
import com.hua.abstractmusic.ui.LocalBottomControllerHeight
import com.hua.abstractmusic.ui.utils.*
import com.hua.abstractmusic.utils.getCacheDir

/**
 * @author : huaweikai
 * @Date   : 2022/05/18
 * @Desc   :
 */
@SuppressLint("UnsafeOptInUsageError")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SheetChangeScreen(
    sheetNavHostController: NavHostController,
    sheetDetailViewModel: SheetDetailViewModel
) {
    LifecycleFocusClearUtils()
    val context = LocalContext.current
    val cropPicture = rememberLauncherForActivityResult(UCropActivityResultContract()) {
        if (it != null) {
            sheetDetailViewModel.uploadSheetDesc(
                sheetDetailViewModel.sheetDetail.value.copy(
                    artUri = it.toString()
                )
            )
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
    val item = sheetDetailViewModel.sheetDetail.collectAsState().value
    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text(text = "修改歌单", modifier = Modifier.padding(start = 16.dp)) },
                navigationIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "",
                        modifier = Modifier
                            .clickable {
                                sheetNavHostController.navigateUp()
                            }
                            .padding(start = 8.dp)
                            .size(24.dp)
                    )
                },
                actions = {
                    val state = remember { mutableStateOf(false) }
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .padding(end = 8.dp)
                    ) {
                        if (state.value) {
                            CircularProgressIndicator()
                        } else {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "",
                                modifier = Modifier
                                    .clickable {
                                        state.value = true
                                        sheetDetailViewModel.uploadSheetDesc {
                                            state.value = false
                                            sheetNavHostController.navigateUp()
                                        }
                                    })
                        }
                    }
                },
                modifier = Modifier.statusBarsPadding()
            )
        }
    ) {
        val moreHeight = remember {
            mutableStateOf(0.dp)
        }
        Column(
            Modifier
                .fillMaxSize()
                .padding(it)
                .padding(horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ArtImage(
                    modifier = Modifier
                        .size(150.dp)
                        .clickable {
                            selectPicture.launch("image/*")
                        },
                    uri = item.artUri,
                    desc = "",
                    transformation = RoundedCornersTransformation(20f)
                )
                Spacer(modifier = Modifier.height(16.dp))
                BasicTextField(
                    value = item.title,
                    onValueChange = { sheetDetailViewModel.uploadSheetDesc(item.copy(title = it)) },
                    maxLines = 1,
                    singleLine = true,
                    textStyle = TextStyle(
                        fontSize = 22.sp, lineHeight = 26.sp, textAlign = TextAlign.Center
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                BasicTextField(
                    value = item.sheetDesc ?: "",
                    onValueChange = {
                        sheetDetailViewModel.uploadSheetDesc(item.copy(sheetDesc = it))
                    },
                    maxLines = 2,
                    textStyle = TextStyle(
                        fontSize = 18.sp, lineHeight = 22.sp, textAlign = TextAlign.Center
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            Box(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentPadding = PaddingValues(bottom = moreHeight.value + LocalBottomControllerHeight.current)
                ) {
                    itemsIndexed(sheetDetailViewModel.sheetChangeList.value, key = {_,item-> item.mediaId}) { index, item ->
                        val data = item.mediaItem.mediaMetadata
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .height(70.dp)
                                .background(
                                    if (item.isPlaying) MaterialTheme.colorScheme.surfaceVariant
                                    else MaterialTheme.colorScheme.surface
                                )
                                .clickable {
                                    sheetDetailViewModel.updateSelect(index)
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CoilImage(
                                url = data.artworkUri,
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxHeight()
                            ) {
                                TitleAndArtist(
                                    title = "${data.title}",
                                    subTitle = "${data.artist}"
                                )
                            }
                        }
                    }
                }
                val density = LocalDensity.current
                androidx.compose.animation.AnimatedVisibility(
                    visible = sheetDetailViewModel.sheetChangeList.value.find { it.isPlaying } != null,
                    modifier = Modifier
                        .onSizeChanged {
                            moreHeight.value = with(density) {
                                it.height.toDp()
                            }
                        }
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(
                            bottom = LocalBottomControllerHeight.current - 8.dp
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                sheetDetailViewModel.removeNetSheetList()
                            }
                            .height(38.dp)
                            .background(
                                MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                            ),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = "移除歌单")
                    }
                }
            }
        }
    }
}