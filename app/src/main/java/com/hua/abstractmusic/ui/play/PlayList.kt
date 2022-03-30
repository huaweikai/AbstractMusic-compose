package com.hua.abstractmusic.ui.play

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material3.Text
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.airbnb.lottie.compose.*
import com.hua.abstractmusic.R
import com.hua.abstractmusic.bean.MediaData
import com.hua.abstractmusic.ui.LocalPlayingViewModel
import com.hua.abstractmusic.ui.viewmodels.PlayingViewModel


/**
 * @author : huaweikai
 * @Date   : 2022/01/10
 * @Desc   : item
 */

@ExperimentalMaterialApi
@SuppressLint("UnsafeOptInUsageError")
@Composable
fun HomePlayList(
    playListState: ModalBottomSheetState,
    content: @Composable () -> Unit
) {

    ModalBottomSheetLayout(
        sheetState = playListState,
        sheetContent = {
            PlayListScreen()
        },
    ) {
        content()
    }
}

@SuppressLint("UnsafeOptInUsageError")
@Composable
fun PlayListScreen(
    viewModel: PlayingViewModel = LocalPlayingViewModel.current,
    config: Configuration = LocalConfiguration.current
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(config.screenHeightDp.dp * 0.4f, config.screenHeightDp.dp * 0.7f),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        Icon(
            painter = painterResource(id = R.drawable.ic_level_button),
            modifier = Modifier.padding(8.dp),
            contentDescription = ""
        )
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp), horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "播放列表",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
//                modifier = Modifier
//                    .fillMaxWidth()
            )
            Image(
                painter = painterResource(id = R.drawable.ic_delete_all),
                contentDescription = "清空",
                modifier = Modifier
                    .clickable {
                        viewModel.clearPlayList()
                    }
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        if (viewModel.currentPlayList.value.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .fillMaxWidth(),
                contentPadding = PaddingValues()
            ) {
                itemsIndexed(viewModel.currentPlayList.value) { index: Int, item: MediaData ->
                    ListItem(item = item, onClick = {
                        viewModel.skipTo(index, true)
                    }, onRemove = {
                        viewModel.removePlayItem(index)
                    })
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "当前并无播放音乐")
            }
        }

    }
}

@SuppressLint("UnsafeOptInUsageError")
@Composable
private fun ListItem(
    item: MediaData,
    onClick: () -> Unit,
    onRemove: () -> Unit
) {
    val playing by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.playing2)
    )
    val process by animateLottieCompositionAsState(
        composition = playing,
        iterations = LottieConstants.IterateForever
    )
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (item.isPlaying) MaterialTheme.colorScheme.onBackground.copy(alpha = 0.10f) else Color.Transparent,
                RoundedCornerShape(8.dp)
            )
            .clickable {
                onClick()
            }
    ) {
        val (text, delete) = createRefs()
        val itemTitle = buildAnnotatedString {
            withStyle(
                if (item.isPlaying) SpanStyle(MaterialTheme.colorScheme.primary) else SpanStyle(
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            ) {
                withStyle(style = SpanStyle(fontSize = 18.sp)) {
                    append("${item.mediaItem.mediaMetadata.title} - ")
                }
                withStyle(style = SpanStyle(fontSize = 14.sp)) {
                    append("${item.mediaItem.mediaMetadata.artist}")
                }
            }
        }
        Row(
            modifier = Modifier.constrainAs(text) {
                top.linkTo(parent.top, 8.dp)
                start.linkTo(parent.start, 8.dp)
                bottom.linkTo(parent.bottom, 8.dp)
                end.linkTo(delete.start, 8.dp)
                height = Dimension.preferredValue(32.dp)
                width = Dimension.fillToConstraints
            },
        ) {
            if (item.isPlaying) {
                LottieAnimation(
                    composition = playing,
                    progress = process,
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(end = 8.dp)
                        .aspectRatio(1f),
                )
            }
            Text(
                text = itemTitle, maxLines = 1,
                textAlign = TextAlign.Start
            )
        }

        Image(
            painter = painterResource(id = R.drawable.ic_delete_item),
            contentDescription = "删除",
            Modifier
                .constrainAs(delete) {
                    end.linkTo(parent.end,8.dp)
                    top.linkTo(text.top)
                    bottom.linkTo(text.bottom)
                }
                .clickable {
                    onRemove()
//                    viewModel.removePlayItem(index)
                }
        )
    }
}