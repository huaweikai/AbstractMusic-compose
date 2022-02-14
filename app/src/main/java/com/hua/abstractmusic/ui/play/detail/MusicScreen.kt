package com.hua.abstractmusic.ui.play.detail

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight.Companion.W300
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.core.graphics.createBitmap
import androidx.core.graphics.drawable.toBitmap
import androidx.media2.common.SessionPlayer
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.request.SuccessResult
import coil.transform.RoundedCornersTransformation
import com.google.android.exoplayer2.Player
import com.hua.abstractmusic.R
import com.hua.abstractmusic.ui.viewmodels.HomeViewModel
import com.hua.abstractmusic.utils.*
import kotlinx.coroutines.launch

/**
 * @author : huaweikai
 * @Date   : 2022/01/20
 * @Desc   : view
 */
@Composable
fun MusicScreen(
    viewModel: HomeViewModel,
    isDark: Boolean = isSystemInDarkTheme()
) {
    val imageLoader = ImageLoader(LocalContext.current)
    val context = LocalContext.current
    val defaultColor = Pair(context.getColor(R.color.black), context.getColor(R.color.black))
    val bitmap = remember {
        mutableStateOf(defaultColor)
    }
    val scope = rememberCoroutineScope()
    LaunchedEffect(viewModel.currentItem.value) {
        val request = ImageRequest.Builder(context)
            .data(viewModel.currentItem.value.metadata?.albumArtUri)
            .allowHardware(false)
            .target(
                onSuccess = {
                    scope.launch {
                        bitmap.value = PaletteUtils.resolveBitmap(
                            isDark,
                            it.toBitmap(),
                            context.getColor(R.color.black)
                        )
                    }
                },
                onError = {
                    scope.launch {
                        bitmap.value = PaletteUtils.resolveBitmap(
                            isDark,
                            BitmapFactory.decodeResource(
                                context.resources,
                                R.drawable.music
                            ),
                            context.getColor(R.color.black)
                        )
                    }
                }
            )
            .build()
        imageLoader.execute(request)
    }
    val color by animateColorAsState(
        Color(bitmap.value.first)
    )
    CompositionLocalProvider(LocalContentColor provides color) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
        ) {
            val (img, title, mode, seekBar, second, controller) = createRefs()
            val imgStart = createGuidelineFromStart(0.1f)
            val imgEnd = createGuidelineFromStart(0.9f)
            val imgTop = createGuidelineFromTop(0.15f)
            val imgBottom = createGuidelineFromTop(0.5f)
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .apply {
                        data(viewModel.currentItem.value.metadata?.albumArtUri)
                        error(R.drawable.music)
                        transformations(RoundedCornersTransformation(30f))
                    }
                    .build(),
                contentDescription = "",
                modifier = Modifier
                    .constrainAs(img) {
                        top.linkTo(imgTop)
                        start.linkTo(imgStart)
                        end.linkTo(imgEnd)
                        bottom.linkTo(imgBottom)
                        width = Dimension.fillToConstraints
                        height = Dimension.fillToConstraints
                    }
            )
            Column(modifier = Modifier
                .constrainAs(title) {
                    top.linkTo(img.bottom, 32.dp)
                    start.linkTo(img.start)
                    end.linkTo(img.end)
                    height = Dimension.preferredValue(40.dp)
                    width = Dimension.fillToConstraints
                }
            ) {
                Title(
                    title = "${viewModel.currentItem.value.metadata?.title}",
                    desc = "${viewModel.currentItem.value.metadata?.artist} - ${viewModel.currentItem.value.metadata?.album}"
                )
            }
            Row(modifier = Modifier
                .constrainAs(mode) {
                    top.linkTo(title.bottom)
                    end.linkTo(parent.end, 16.dp)
                    height = Dimension.preferredValue(32.dp)
                }
            ) {
                Mode()
            }
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
                    inactiveTrackColor = Color(bitmap.value.second),
                    activeTrackColor = Color(bitmap.value.first)
                ),
                modifier = Modifier
                    .constrainAs(seekBar) {
                        start.linkTo(parent.start, 32.dp)
                        end.linkTo(parent.end, 32.dp)
                        top.linkTo(mode.bottom, 8.dp)
                        width = Dimension.fillToConstraints
                        height = Dimension.preferredValue(16.dp)
                    }
            )

            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(second) {
                        top.linkTo(seekBar.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end, 16.dp)
                    }
            ) {
                val (current, max) = createRefs()
                Text(
                    text = viewModel.currentPosition.value.toTime(),
                    modifier = Modifier
                        .constrainAs(current) {
                            start.linkTo(parent.start, 16.dp)
                            top.linkTo(parent.top)
                        }
                )
                Text(
                    text = viewModel.maxValue.value.toLong().toTime(),
                    modifier = Modifier
                        .constrainAs(max) {
                            end.linkTo(parent.end, 16.dp)
                            top.linkTo(parent.top)
                        }
                )
            }
            Column(
                modifier = Modifier
                    .constrainAs(controller) {
                        top.linkTo(second.bottom, 10.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                        width = Dimension.fillToConstraints
                        height = Dimension.fillToConstraints
                    }
            ) {
                PlayController(state = viewModel.playerState.value, {
                    viewModel.playOrPause()
                }, {
                    viewModel.prevItem()
                }, {
                    viewModel.nextItem()
                }
                )
            }

        }
    }
}

@Composable
private fun Title(
    title: String,
    desc: String
) {
    Text(
        text = title,
        textAlign = TextAlign.Start,
        fontSize = 22.sp
    )
    Spacer(modifier = Modifier.padding(top = 12.dp))
    Text(
        text = desc,
        textAlign = TextAlign.Start,
        fontSize = 16.sp,
        fontWeight = W300
    )
}

@Composable
private fun Mode() {
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

@Composable
private fun PlayController(
    @Player.State state: Int,
    onPlayClick: () -> Unit,
    onPrevClick: () -> Unit,
    onNextClick: () -> Unit,
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val (prev, play, next) = createRefs()
        Icon(
            painter = painterResource(
                if (state == SessionPlayer.PLAYER_STATE_PLAYING) R.drawable.ic_play_pause
                else R.drawable.ic_play_play
            ),
            contentDescription = "",
            modifier = Modifier
                .constrainAs(play) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
                .size(70.dp)
                .clickable {
                    onPlayClick()
                }
        )
        Icon(
            painter = painterResource(
                R.drawable.ic_play_prev
            ),
            contentDescription = "",
            modifier = Modifier
                .constrainAs(prev) {
                    end.linkTo(play.start, 50.dp)
                    top.linkTo(play.top)
                    bottom.linkTo(play.bottom)
                }
                .size(33.dp)
                .clickable {
                    onPrevClick()
                }
        )
        Icon(
            painter = painterResource(
                R.drawable.ic_play_next
            ),
            contentDescription = "",
            modifier = Modifier
                .constrainAs(next) {
                    start.linkTo(play.end, 50.dp)
                    top.linkTo(play.top)
                    bottom.linkTo(play.bottom)
                }
                .size(33.dp)
                .clickable {
                    onNextClick()
                }
        )
    }
}