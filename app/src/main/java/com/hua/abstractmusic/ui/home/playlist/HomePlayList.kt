package com.hua.abstractmusic.ui.home.playlist

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.hua.abstractmusic.R
import com.hua.abstractmusic.bean.MediaData
import com.hua.abstractmusic.ui.LocalHomeViewModel
import com.hua.abstractmusic.ui.home.viewmodels.HomeViewModel
import com.hua.abstractmusic.utils.artist
import com.hua.abstractmusic.utils.title
import kotlinx.coroutines.launch


/**
 * @author : huaweikai
 * @Date   : 2022/01/10
 * @Desc   : item
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomePlayList(
    playListState: ModalBottomSheetState,
    viewModel: HomeViewModel = LocalHomeViewModel.current,
) {
//    val state = viewModel.playListState.value

    val scope = rememberCoroutineScope()

    ModalBottomSheetLayout(
        sheetState = playListState,
        sheetContent = {
            Spacer(modifier = Modifier.height(10.dp))
            Row(Modifier.fillMaxWidth()) {
                Text(
                    text = "播放列表",
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
                Image(
                    painter = painterResource(id = R.drawable.ic_delete_all),
                    contentDescription = "清空",
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clickable {
                            viewModel.clearPlayList()
                        }
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            LazyColumn(
                modifier = Modifier
                    .heightIn(max = 500.dp)
                    .padding(10.dp)
                    .fillMaxWidth()
            ) {
                itemsIndexed(viewModel.currentPlayList.value) { index: Int, item: MediaData ->
                    ConstraintLayout(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable {
                                viewModel.skipTo(index)
                            }
                    ) {
                        val (text, delete) = createRefs()
                        val percent = createGuidelineFromStart(0.85f)
                        val itemTitle = buildAnnotatedString {
                            withStyle(
                                if (item.isPlaying) SpanStyle(Color(0xff77D3D0)) else SpanStyle(
                                    Color.Gray
                                )
                            ) {
                                withStyle(style = SpanStyle(fontSize = 20.sp)) {
                                    append("${item.mediaItem.metadata?.title} - ")
                                }
                                withStyle(style = SpanStyle(fontSize = 15.sp)) {
                                    append("${item.mediaItem.metadata?.artist}")
                                }
                            }
                        }
                        Text(
                            text = itemTitle, maxLines = 1,
                            modifier = Modifier.constrainAs(text) {
                                top.linkTo(parent.top, 8.dp)
                                start.linkTo(parent.start, 8.dp)
                                bottom.linkTo(parent.bottom, 8.dp)
                                end.linkTo(percent, 8.dp)
                                width = Dimension.fillToConstraints
                            },
                            textAlign = TextAlign.Start
                        )
                        Image(
                            painter = painterResource(id = R.drawable.ic_delete_item),
                            contentDescription = "删除",
                            Modifier
                                .constrainAs(delete) {
                                    start.linkTo(percent)
                                    end.linkTo(parent.end, 8.dp)
                                    top.linkTo(text.top, 0.dp)
                                    bottom.linkTo(text.bottom, 0.dp)
                                }
                                .clickable {
                                    viewModel.removePlayItem(index)
                                }
                        )
                    }
                }
            }
        }
    ) {
//        ScaffoldContent()
    }
}