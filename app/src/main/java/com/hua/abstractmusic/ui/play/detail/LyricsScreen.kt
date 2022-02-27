package com.hua.abstractmusic.ui.play.detail

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.transform.RoundedCornersTransformation
import com.hua.abstractmusic.bean.LyricsEntry
import com.hua.abstractmusic.ui.LocalPlayingViewModel
import com.hua.abstractmusic.ui.utils.ArtImage
import com.hua.abstractmusic.ui.utils.LCE
import com.hua.abstractmusic.ui.utils.TitleAndArtist
import com.hua.abstractmusic.ui.utils.translucent
import com.hua.abstractmusic.ui.viewmodels.PlayingViewModel
import com.hua.abstractmusic.utils.textDp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
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
        val lyricsLoad = viewModel.lyricsLoadState.collectAsState()
        when (lyricsLoad.value) {
            is LCE.Loading -> {
                LyricsLoading()
            }
            is LCE.Error -> {
                LyricsError()
            }
            is LCE.Success -> {
                LyricsSuccess()
            }
        }
    }
}


@Composable
private fun LyricsSuccess(
    viewModel: PlayingViewModel = LocalPlayingViewModel.current,
    configuration: Configuration = LocalConfiguration.current,
) {
    val current = remember {
        mutableStateOf(26)
    }
    val height = (configuration.screenHeightDp - current.value) / 2
    val isTouch = remember {
        mutableStateOf(false)
    }
    val playerState = viewModel.playerState.collectAsState().value
    LaunchedEffect(
        viewModel.lyricList.value,
        playerState,
        isTouch.value,
        viewModel.lyricsCanScroll.value
    ) {
        if (viewModel.lyricList.value.isNotEmpty() && playerState && viewModel.lyricsCanScroll.value) {
            val start = viewModel.getMusicDuration()
            val nextIndex = viewModel.getNextIndex(start)
            delay(viewModel.getStartToNext(nextIndex, start))
            if(!isTouch.value){
                viewModel.lyricsState.animateScrollToItem((nextIndex).coerceAtLeast(0), -height.toInt())
            }
            viewModel.setLyricsItem(nextIndex)
        }
    }

    LazyColumn(
        Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .moreClick(isTouch)
            .translucent(),
        state = viewModel.lyricsState
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
                if(viewModel.lyricsCanScroll.value){
                    viewModel.seekTo(lyrics.time ?: 0L)
                }
            }
        }
        blackItem()
    }
}

@Composable
private fun LyricsLoading() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun LyricsError(
    viewModel: PlayingViewModel = LocalPlayingViewModel.current
) {
    val scope = rememberCoroutineScope()
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "没有歌词")
        Button(onClick = {
            scope.launch {
                viewModel.getLyrics(viewModel.currentPlayItem.value)
            }
        }) {
            Text(text = "点击重试")
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
        shape = RoundedCornerShape(10.dp),
        backgroundColor = if (current) Color.Transparent.copy(alpha = 0.1f) else Color.Transparent,
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
                .height((LocalConfiguration.current.screenHeightDp * 0.2).dp)
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

