package com.hua.abstractmusic.ui.play.detail

import android.content.res.Configuration
import androidx.annotation.DrawableRes
import androidx.compose.animation.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media2.common.SessionPlayer
import coil.ImageLoader
import coil.transform.RoundedCornersTransformation
import com.hua.abstractmusic.R
import com.hua.abstractmusic.bean.ui.home.IconBean
import com.hua.abstractmusic.ui.LocalComposeUtils
import com.hua.abstractmusic.ui.LocalHomeViewModel
import com.hua.abstractmusic.ui.LocalMusicScreenSecondColor
import com.hua.abstractmusic.ui.LocalScreenSize
import com.hua.abstractmusic.ui.utils.AlbumArtImage
import com.hua.abstractmusic.ui.utils.TitleAndArtist
import com.hua.abstractmusic.ui.utils.WindowSize
import com.hua.abstractmusic.ui.viewmodels.HomeViewModel
import com.hua.abstractmusic.utils.*

/**
 * @author : huaweikai
 * @Date   : 2022/01/20
 * @Desc   : view
 */
@Composable
fun MusicScreen(
    viewModel: HomeViewModel = LocalHomeViewModel.current,
    composeUtils: ComposeUtils = LocalComposeUtils.current,
    isDark: Boolean = isSystemInDarkTheme()
) {
    val imageLoader = ImageLoader(LocalContext.current)
    val context = LocalContext.current
    val firstColor = remember {
        Animatable(Color.Black)
    }
    val secondColor = remember {
        Animatable(Color.Black)
    }
    LaunchedEffect(viewModel.currentItem.value) {
        val pair = PaletteUtils.resolveBitmap(
            isDark,
            composeUtils.coilToBitmap(viewModel.currentItem.value.metadata?.albumArtUri),
            context.getColor(R.color.black)
        )
        firstColor.animateTo(
            Color(pair.first),
            animationSpec = TweenSpec(500, easing = LinearOutSlowInEasing)
        )
        secondColor.animateTo(Color(pair.second))
    }
    CompositionLocalProvider(
        LocalContentColor provides firstColor.value,
        LocalMusicScreenSecondColor provides secondColor.value
    ) {
        val windowSize = LocalScreenSize.current
        if (windowSize == WindowSize.Expanded) {
            HorizontalScreen()
        } else if (windowSize == WindowSize.Compact) {
            VerticalScreen()
        }
    }
}

@Composable
private fun VerticalScreen(
    configuration: Configuration = LocalConfiguration.current,
    viewModel: HomeViewModel = LocalHomeViewModel.current
) {
    val topGlide = configuration.screenHeightDp * 0.15
    val data = viewModel.currentItem.value.metadata
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Spacer(modifier = Modifier.height(topGlide.dp))
        AlbumArtImage(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .aspectRatio(1f)
                .align(CenterHorizontally),
            uri = data?.albumArtUri,
            desc = "",
            transformation = RoundedCornersTransformation(30f)
        )
        Spacer(modifier = Modifier.height(32.dp))
        TitleAndArtist(
            title = "${data?.title}",
            subTitle = "${data?.artist}",
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

@Composable
private fun HorizontalScreen(
    configuration: Configuration = LocalConfiguration.current,
    viewModel: HomeViewModel = LocalHomeViewModel.current
) {
    val screenHeight = configuration.screenHeightDp
    val startGlide = screenHeight * 0.1
    val data = viewModel.currentItem.value.metadata
    Row(
        Modifier
            .fillMaxSize()
            .padding(start = startGlide.dp)
    ) {
        AlbumArtImage(
            modifier = Modifier
                .fillMaxHeight(0.8f)
                .align(CenterVertically)
                .aspectRatio(1f),
            uri = data?.albumArtUri,
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
                title = "${data?.title}",
                subTitle = "${data?.artist}",
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

@Composable
private fun MusicSlider(
    modifier: Modifier,
    viewModel: HomeViewModel = LocalHomeViewModel.current
) {
    Slider(
        value = viewModel.currentPosition.value.toFloat(),
        valueRange = 0f..viewModel.maxValue.value,
        onValueChange = {
            //点击准备改变时，先设置我已经对seekbar操作，让更新seekbar暂停。
            viewModel.actionSeekBar.value = true
            viewModel.currentPosition.value = it.toLong()
        },
        onValueChangeFinished = {
            //结束后，先去seekto再去更新ui
            viewModel.seekTo(viewModel.currentPosition.value)
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

@Composable
private fun SecondText(
    modifier: Modifier,
    viewModel: HomeViewModel = LocalHomeViewModel.current
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = viewModel.currentPosition.value.toTime())
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

@Composable
private fun PlayController(
    modifier: Modifier,
    viewModel: HomeViewModel = LocalHomeViewModel.current
) {
    Row(
        modifier.width(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = CenterVertically
    ) {
        val stateIcon =
            if (viewModel.playerState.value == SessionPlayer.PLAYER_STATE_PLAYING) {
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
        onClick = { onClick() }
    ) {
        Icon(
            painter = painterResource(
                resId
            ),
            contentDescription = desc,
            modifier = Modifier
                .clip(CircleShape)
                .size(size)
        )
    }
    Spacer(modifier = Modifier.width(width))

}