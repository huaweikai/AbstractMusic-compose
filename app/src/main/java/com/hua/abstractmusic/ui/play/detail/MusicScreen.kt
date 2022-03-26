package com.hua.abstractmusic.ui.play.detail

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.annotation.DrawableRes
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

/**
 * @author : huaweikai
 * @Date   : 2022/01/20
 * @Desc   : view
 */
@SuppressLint("UnsafeOptInUsageError")
@Composable
fun MusicScreen(
//    viewModel: PlayingViewModel = LocalPlayingViewModel.current,
) {
    val windowSize = LocalScreenSize.current
    if (windowSize == WindowSize.Expanded) {
        HorizontalScreen()
    } else if (windowSize == WindowSize.Compact) {
        VerticalScreen()
    }

}

@SuppressLint("UnsafeOptInUsageError")
@Composable
private fun VerticalScreen(
    configuration: Configuration = LocalConfiguration.current,
    viewModel: PlayingViewModel = LocalPlayingViewModel.current
) {
    val data = viewModel.currentPlayItem.value.mediaMetadata
    val playState = viewModel.playerState.collectAsState()
    Column(
        modifier = Modifier
            .padding(
                top = 52.dp + WindowInsets.statusBars
                    .asPaddingValues()
                    .calculateTopPadding()
            )
            .fillMaxSize()
    ) {
        ArtImage(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .aspectRatio(1f)
                .align(CenterHorizontally),
            uri = data.artworkUri,
            desc = "",
            transformation = RoundedCornersTransformation(30f)
        )
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
        )
        Spacer(modifier = Modifier.height(8.dp))
        MusicSlider(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .align(CenterHorizontally)
        )
        SecondText(
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
            SecondText(modifier = Modifier.fillMaxWidth())
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
    Slider(
        value = viewModel.currentPosition.value.coerceAtLeast(0F),
        valueRange = 0f..viewModel.maxValue.value.coerceAtLeast(0F),
        onValueChange = {
            //点击准备改变时，先设置我已经对seekbar操作，让更新seekbar暂停。
            viewModel.actionSeekBar.value = true
            viewModel.currentPosition.value = it
        },
        onValueChangeFinished = {
            //结束后，先去seekto再去更新ui
            viewModel.seekTo(viewModel.currentPosition.value.toLong())
            viewModel.actionSeekBar.value = false
        },
        colors = SliderDefaults.colors(
            thumbColor = LocalContentColor.current,
            inactiveTrackColor = LocalMusicScreenSecondColor.current,
            activeTrackColor = LocalContentColor.current
        ),
        modifier = modifier
    )
}

@androidx.media3.common.util.UnstableApi
@Composable
private fun SecondText(
    modifier: Modifier,
    viewModel: PlayingViewModel = LocalPlayingViewModel.current
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = viewModel.currentPosition.value.toLong().toTime())
        Text(text = viewModel.maxValue.value.toLong().toTime())
    }
}

@Composable
private fun Mode(
    modifier: Modifier
) {
    Row(
        modifier = modifier
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_mode_shuffle),
            contentDescription = ""
        )
        Spacer(modifier = Modifier.padding(start = 16.dp))
        Icon(
            painter = painterResource(id = R.drawable.ic_mode_order_play),
            contentDescription = ""
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