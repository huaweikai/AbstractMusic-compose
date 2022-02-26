package com.hua.abstractmusic.ui.play.detail

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.transform.RoundedCornersTransformation
import com.hua.abstractmusic.bean.LyricsEntry
import com.hua.abstractmusic.ui.LocalPlayingViewModel
import com.hua.abstractmusic.ui.utils.ArtImage
import com.hua.abstractmusic.ui.utils.TitleAndArtist
import com.hua.abstractmusic.ui.viewmodels.PlayingViewModel
import com.hua.abstractmusic.utils.textDp
import kotlinx.coroutines.delay


/**
 * @author : huaweikai
 * @Date   : 2022/02/24
 * @Desc   :
 */


@SuppressLint("UnsafeOptInUsageError")
@Composable
fun LyricsScreen(
    viewModel: PlayingViewModel = LocalPlayingViewModel.current,
    configuration: Configuration = LocalConfiguration.current,
) {
    val topGlide = configuration.screenHeightDp * 0.10
    val state = rememberLazyListState()
    val isTouch = remember {
        mutableStateOf(false)
    }
    val playerState = viewModel.playerState.collectAsState().value
    val current = remember {
        mutableStateOf(26)
    }
    val height = (configuration.screenHeightDp - current.value) / 2
    LaunchedEffect(viewModel.lyricList.value, playerState, isTouch.value) {
        if (viewModel.lyricList.value.isNotEmpty() && playerState &&!isTouch.value ) {
            while (true) {
                val start = viewModel.getMusicDuration()
                val nextIndex = viewModel.getNextIndex(start)
                delay(viewModel.getStartToNext(nextIndex, start))
                state.scrollToItem((nextIndex).coerceAtLeast(0), -height.toInt())
                viewModel.setLyricsItem(nextIndex)
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
                .moreClick(isTouch)
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
                    textSize = 22
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
    onClick: () -> Unit
) {
    val textAlpha = animateFloatAsState(if (current) 1F else 0.32F).value
    // 歌词文本对齐方式，可选左 / 中
    val align = if (centerAlign) TextAlign.Center else TextAlign.Left
    Card(
        modifier = Modifier
            .animateItemPlacement()
            .fillMaxWidth()
            .onSizeChanged {
                if (current) {
                    currentTextElementHeightPxState.value = it.height
                }
            }
            .padding(0.dp, (textSize * 0.1F).dp),
        shape = RoundedCornerShape(8.dp),
        backgroundColor = Color.Transparent,
        elevation = 0.dp
    ) {
        val paddingY = (textSize * 0.3F).dp
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onClick()
                }
                .padding(8.dp, paddingY),
            verticalArrangement = Arrangement.Center
        ) {
            val mainTextSize = textSize.textDp
            Text(
                text = lyricsEntry.main,
                modifier = Modifier
                    .alpha(textAlpha)
                    .fillMaxWidth(),
                fontSize = mainTextSize,
                textAlign = align
            )
        }
    }
}

// 前后空白
private val blackItem: (LazyListScope.() -> Unit) = {
    item {
        Box(
            modifier = Modifier
                .height(LocalConfiguration.current.screenHeightDp.dp / 2)
        ) {
        }
    }
}

private fun Modifier.moreClick(
    isTouch: MutableState<Boolean>
) = this.pointerInput(Unit) {
    awaitPointerEventScope {
        while (true) {
            val event = awaitPointerEvent(PointerEventPass.Initial)
            val dragEvent = event.changes.firstOrNull()
            when {
                dragEvent!!.positionChangeConsumed() -> {
                    return@awaitPointerEventScope
                }
                dragEvent.changedToDownIgnoreConsumed() -> {
                    isTouch.value = true
                }
                dragEvent.changedToUpIgnoreConsumed() -> {
                    isTouch.value = false
                }
            }
        }
    }
}

