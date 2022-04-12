package com.hua.abstractmusic.ui.play.detail

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.media3.common.Player
import coil.transform.RoundedCornersTransformation
import com.hua.abstractmusic.R
import com.hua.abstractmusic.bean.ui.home.IconBean
import com.hua.abstractmusic.ui.LocalMusicScreenSecondColor
import com.hua.abstractmusic.ui.LocalPlayingViewModel
import com.hua.abstractmusic.ui.LocalScreenSize
import com.hua.abstractmusic.ui.utils.ArtImage
import com.hua.abstractmusic.ui.utils.TitleAndArtist
import com.hua.abstractmusic.ui.utils.WindowSize
import com.hua.abstractmusic.ui.viewmodels.PlayingViewModel
import com.hua.abstractmusic.utils.toTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors

/**
 * @author : huaweikai
 * @Date   : 2022/01/20
 * @Desc   : view
 */
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnsafeOptInUsageError")
@Composable
fun MusicScreen(
    snackBarState: SnackbarHostState,
    viewModel: PlayingViewModel = LocalPlayingViewModel.current,
) {
    val itemColor = viewModel.itemColor.collectAsState().value
    val windowSize = LocalScreenSize.current

    CompositionLocalProvider(
        LocalContentColor provides animateColorAsState(
            targetValue = itemColor.first,
            tween(600)
        ).value,
        androidx.compose.material.LocalContentColor provides animateColorAsState(
            targetValue = itemColor.first,
            tween(600)
        ).value,
        LocalMusicScreenSecondColor provides animateColorAsState(
            targetValue = itemColor.second,
            tween(600)
        ).value
    ) {
        if (windowSize == WindowSize.Expanded) {
            HorizontalScreen()
        } else if (windowSize == WindowSize.Compact) {
            VerticalScreen(snackBarState)
        }
    }
}

@SuppressLint("UnsafeOptInUsageError")
@Composable
private fun VerticalScreen(
    snackbarHostState: SnackbarHostState,
    configuration: Configuration = LocalConfiguration.current,
    viewModel: PlayingViewModel = LocalPlayingViewModel.current
) {
    val data = viewModel.currentPlayItem.value.mediaMetadata
    val playState = viewModel.playerState.collectAsState()
    val scale by animateFloatAsState(if (playState.value) 1f else 0.95f, animationSpec = tween(300))
    Column(
        modifier = Modifier
            .padding(
                top = 52.dp + WindowInsets.statusBars
                    .asPaddingValues()
                    .calculateTopPadding()
            )
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .align(Alignment.CenterHorizontally)
                .aspectRatio(1f),
            contentAlignment = Center
        ) {
            ArtImage(
                modifier = Modifier
                    .graphicsLayer(scaleX = scale, scaleY = scale)
                    .fillMaxWidth()
                    .aspectRatio(1f),
                uri = data.artworkUri,
                desc = "",
                transformation = RoundedCornersTransformation(30f)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
        TitleAndArtist(
            title = "${data.title}",
            subTitle = "${data.artist}",
            height = 12.dp,
            titleStyle = {
                this.copy(fontSize = 22.sp)
            },
            subTitleStyle = {
                this.copy(fontSize = 16.sp)
            },
            color = LocalContentColor.current,
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .align(CenterHorizontally)
        )
        Mode(
            modifier = Modifier
                .align(Alignment.End)
                .padding(end = 16.dp),
            snackbarHostState = snackbarHostState
        )
        Spacer(modifier = Modifier.height(8.dp))
        MusicSlider(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .align(CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(18.dp))
        PlayController(modifier = Modifier.align(CenterHorizontally))
    }
}

@androidx.media3.common.util.UnstableApi
@Composable
private fun HorizontalScreen(
    configuration: Configuration = LocalConfiguration.current,
    viewModel: PlayingViewModel = LocalPlayingViewModel.current
) {
    val screenHeight = configuration.screenHeightDp
    val startGlide = screenHeight * 0.1
    val data = viewModel.currentPlayItem.value.mediaMetadata
    Row(
        Modifier
            .fillMaxSize()
            .padding(start = startGlide.dp)
    ) {
        ArtImage(
            modifier = Modifier
                .fillMaxHeight(0.8f)
                .animateContentSize()
                .align(CenterVertically)
                .aspectRatio(1f),
            uri = data.artworkUri,
            desc = "",
            transformation = RoundedCornersTransformation(30f)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(
            Modifier
                .fillMaxHeight()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            TitleAndArtist(
                title = "${data.title}",
                subTitle = "${data.artist}",
                height = 5.dp,
                titleStyle = {
                    this.copy(fontSize = 22.sp)
                },
                subTitleStyle = {
                    this.copy(fontSize = 16.sp)
                },
                color = LocalContentColor.current,
            )
            MusicSlider(modifier = Modifier.fillMaxWidth())
            PlayController(modifier = Modifier.fillMaxWidth())
        }
    }
}

@androidx.media3.common.util.UnstableApi
@Composable
private fun MusicSlider(
    modifier: Modifier,
    viewModel: PlayingViewModel = LocalPlayingViewModel.current
) {
    val playState = viewModel.playerState.collectAsState()
    val sliderValue = remember{ mutableStateOf(viewModel.getMusicDuration().toFloat())}
    val isFirst = remember{
        mutableStateOf(true)
    }
    Column(modifier = Modifier.fillMaxWidth()) {
        Slider(
            value = viewModel.currentPosition.value,
            valueRange = 0f..viewModel.maxValue.value.coerceAtLeast(0F),
            onValueChange = {
                //点击准备改变时，先设置我已经对seekbar操作，让更新seekbar暂停。
                viewModel.cancelUpdatePosition()
                sliderValue.value = it
            },
            onValueChangeFinished = {
                //结束后，先去seekto再去更新ui
            },
            colors = SliderDefaults.colors(
                thumbColor = LocalContentColor.current,
                inactiveTrackColor = LocalMusicScreenSecondColor.current,
                activeTrackColor = LocalContentColor.current
            ),
            modifier = modifier
        )
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = viewModel.currentPosition.value.toLong().toTime())
            Text(text = viewModel.maxValue.value.toLong().toTime())
        }
    }

    LaunchedEffect(sliderValue.value){
        viewModel.currentPosition.value = sliderValue.value
        if(!isFirst.value){
            delay(500L)
            viewModel.seekTo(sliderValue.value.toLong())
            if (playState.value) {
                viewModel.startUpdatePosition()
            }
        }
        isFirst.value = false
    }

    LaunchedEffect(
        playState.value
    ) {
        if (playState.value) {
            viewModel.startUpdatePosition()
        } else {
            viewModel.cancelUpdatePosition()
        }
    }

    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(Unit) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                if (playState.value) {
                    viewModel.startUpdatePosition()
                }
            } else if (event == Lifecycle.Event.ON_PAUSE) {
                viewModel.cancelUpdatePosition()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        this.onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

@Composable
private fun Mode(
    modifier: Modifier,
    snackbarHostState: SnackbarHostState,
    viewModel: PlayingViewModel = LocalPlayingViewModel.current
) {
    val scope = rememberCoroutineScope()
    val repeatUi = remember {
        mutableStateOf(R.drawable.ic_mode_repeat_off)
    }
    val shuffleBack by animateColorAsState(
        if (viewModel.shuffleUI.value) LocalMusicScreenSecondColor.current else Color.Transparent
    )

    LaunchedEffect(viewModel.repeatModeUI.value) {
        repeatUi.value = when (viewModel.repeatModeUI.value) {
            Player.REPEAT_MODE_OFF -> R.drawable.ic_mode_repeat_off
            Player.REPEAT_MODE_ONE -> R.drawable.ic_mode_repeat_one
            Player.REPEAT_MODE_ALL -> R.drawable.ic_mode_repeat_all
            else -> R.drawable.ic_mode_repeat_off
        }
    }
    Row(
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(
                    shuffleBack, RoundedCornerShape(8.dp)
                ),
            contentAlignment = Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_mode_shuffle),
                contentDescription = "",
                modifier = Modifier
                    .size(32.dp)
                    .clickable {
                        val result = viewModel.setShuffle()
                        scope.launch {
                            snackbarHostState.showSnackbar(if (result) "已开启随机播放" else "已关闭随机播放")
                        }
                    }
            )
        }
        Spacer(modifier = Modifier.padding(start = 16.dp))
        Icon(
            painter = painterResource(id = repeatUi.value),
            contentDescription = "",
            modifier = Modifier
                .size(32.dp)
                .clickable {
                    val message = when (viewModel.setRepeatMode()) {
                        Player.REPEAT_MODE_ALL -> "已开启循环列表"
                        Player.REPEAT_MODE_OFF -> "已开启顺序播放"
                        Player.REPEAT_MODE_ONE -> "已开启单曲循环"
                        else -> ""
                    }
                    if (message.isNotBlank()) {
                        scope.launch {
                            snackbarHostState.showSnackbar(message)
                        }
                    }
                }
        )
    }

}

@androidx.media3.common.util.UnstableApi
@Composable
private fun PlayController(
    modifier: Modifier,
    viewModel: PlayingViewModel = LocalPlayingViewModel.current
) {
    Row(
        modifier.width(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = CenterVertically
    ) {
        val state = viewModel.playerState.collectAsState()
        val stateIcon = if (state.value) {
            R.drawable.ic_pause
        } else {
            R.drawable.ic_play
        }
        val controller = listOf(
            IconBean(
                R.drawable.ic_play_prev, "上一曲",
                onClick = {
                    viewModel.prevItem()
                }
            ),
            IconBean(
                stateIcon, "播放", size = 96.dp,
                width = 50.dp,
                onClick = {
                    viewModel.playOrPause()
                }
            ),
            IconBean(
                R.drawable.ic_play_next, "下一曲",
                onClick = {
                    viewModel.nextItem()
                }
            )
        )
        controller.forEach {
            ControllerItem(
                it.resId,
                it.desc,
                it.size,
                it.width,
                onClick = it.onClick
            )
        }
    }
}

@Composable
fun ControllerItem(
    @DrawableRes resId: Int,
    desc: String,
    size: Dp,
    width: Dp,
    onClick: () -> Unit
) {
    Spacer(modifier = Modifier.width(width))
    IconButton(
        onClick = { onClick() },
        modifier = Modifier
            .size(size)
            .clip(CircleShape),
    ) {
        Icon(
            painter = painterResource(
                resId
            ),
            modifier = Modifier.size(size),
            contentDescription = desc,
        )
    }
    Spacer(modifier = Modifier.width(width))
}