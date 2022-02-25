package com.hua.abstractmusic.ui.play.detail

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.transform.RoundedCornersTransformation
import com.hua.abstractmusic.ui.LocalPlayingViewModel
import com.hua.abstractmusic.ui.utils.ArtImage
import com.hua.abstractmusic.ui.utils.TitleAndArtist
import com.hua.abstractmusic.ui.viewmodels.PlayingViewModel
import kotlinx.coroutines.delay


/**
 * @author : huaweikai
 * @Date   : 2022/02/24
 * @Desc   :
 */


/**
 * 歌词 Item
 *
 * @param lyricsEntry 歌词 [LyricsEntry]
 * @param current 是否为当前播放
 * @param textSize 字体大小
 * @param textColor 字体颜色
 * @param centerAlign 是否居中对齐
 * @param showSubText 是否显示翻译
 */

@SuppressLint("UnsafeOptInUsageError")
@Composable
fun LyricsScreen(
    viewModel: PlayingViewModel = LocalPlayingViewModel.current,
    configuration: Configuration = LocalConfiguration.current,
) {
    val topGlide = configuration.screenHeightDp * 0.10
    val state = rememberLazyListState()
    val playerState = viewModel.playerState.collectAsState()
    LaunchedEffect(viewModel.lyricList.value, playerState.value) {
        if (playerState.value && viewModel.lyricList.value.isNotEmpty()) {
            while (true) {
                val start = viewModel.getMusicDuration()
                delay(viewModel.setLyricsList(start))
            }
        }
    }

    // 定位中间
    LaunchedEffect(key1 = viewModel.lyricList.value, block = {
        val height = (configuration.screenHeightDp - topGlide) / 2
        val index =
            viewModel.lyricList.value.indexOf(viewModel.lyricList.value.find { it.isPlaying })
        state.animateScrollToItem((index + 1).coerceAtLeast(0), -height.toInt())
    })

    val current = remember {
        mutableStateOf(18)
    }
    // 前后空白
    val blackItem: (LazyListScope.() -> Unit) = {
        item {
            Box(
                modifier = Modifier
                    .height(18.dp / 2)
            ) {
            }
        }
    }
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Spacer(modifier = Modifier.height(topGlide.dp))
        Row(
            Modifier
                .fillMaxWidth()
                .height(70.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ArtImage(
                modifier = Modifier.size(70.dp),
                uri = viewModel.currentPlayItem.value.mediaMetadata.artworkUri,
                desc = "",
                transformation = RoundedCornersTransformation(20f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(
                Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                TitleAndArtist(
                    title = "${viewModel.currentPlayItem.value.mediaMetadata.title}",
                    subTitle = "${viewModel.currentPlayItem.value.mediaMetadata.artist}"
                )
            }
        }
        LazyColumn(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .graphicsLayer { alpha = 0.99F }
                .drawWithContent {
                    val colors = listOf(
                        Color.Transparent, Color.Black, Color.Black, Color.Black, Color.Black,
                        Color.Black, Color.Black, Color.Black, Color.Transparent
                    )
                    drawContent()
                    drawRect(
                        brush = Brush.verticalGradient(colors),
                        blendMode = BlendMode.DstIn
                    )
                },
            state = state
        ) {
            blackItem()
            items(viewModel.lyricList.value) { lyrics ->
                LyricsItem(
                    current = lyrics.isPlaying,
                    lyricsEntry = lyrics,
                    currentTextElementHeightPxState = current,
                    textSize = 14
                )
                {
                    viewModel.seekTo(lyrics.time ?: 0L)
                }
            }
            blackItem()
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun LazyItemScope.LyricsItem(
    lyricsEntry: LyricsEntry,
    current: Boolean = false,
    currentTextElementHeightPxState: MutableState<Int>,
    textSize: Int,
    centerAlign: Boolean = false,
//    showSubText: Boolean = true,
    onClick: () -> Unit
) {
    // 当前歌词，若不显示翻译则只显示主句
//    val mainLyrics = if (showSubText) lyricsEntry.lyrics else lyricsEntry.main ?: ""
    // 当前正在播放的歌词高亮
    val textAlpha = animateFloatAsState(if (current) 1F else 0.32F).value
    // 歌词文本对齐方式，可选左 / 中
    val align = if (centerAlign) TextAlign.Center else TextAlign.Left
    Card(
        modifier = Modifier
            .animateItemPlacement()
            .fillMaxWidth()
            .onSizeChanged {
                if (current) {
                    // 告知当前高亮歌词 Item 高度
                    currentTextElementHeightPxState.value = it.height
                }
            }
            .padding(0.dp, (textSize * 0.1F).dp),
//        shape = SuperEllipseCornerShape(8.dp),
        backgroundColor = Color.Transparent,
        elevation = 0.dp
    ) {
        val paddingY = (textSize * 0.3F).dp
        // 这里使用 Column 是为了若以后拓展具体显示
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onClick()
                }
                .padding(8.dp, paddingY),
            verticalArrangement = Arrangement.Center
        ) {
            val mainTextSize = textSize.dp
            Text(
                text = lyricsEntry.main,
                modifier = Modifier
                    .alpha(textAlpha)
                    .fillMaxWidth(),
                fontSize = mainTextSize.value.sp,
//                color = if (current) LocalContentColor.current else LocalMusicScreenSecondColor.current,
                textAlign = align
            )
        }
    }
}

data class LyricsEntry(
    var isPlaying: Boolean = false,
    val time: Long?,
    val main: String,
//    val lyrics :String
)

