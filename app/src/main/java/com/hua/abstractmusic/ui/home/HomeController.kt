package com.hua.abstractmusic.ui.home

import android.view.LayoutInflater
import android.widget.TextView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.media2.common.MediaItem
import androidx.media2.common.SessionPlayer
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.rememberImagePainter
import com.hua.abstractmusic.R
import com.hua.abstractmusic.bean.ui.home.BottomBarBean
import com.hua.abstractmusic.bean.ui.home.ControllerBean
import com.hua.abstractmusic.ui.home.viewmodels.HomeViewModel
import com.hua.abstractmusic.ui.route.Screen
import com.hua.abstractmusic.ui.theme.LightColor
import com.hua.abstractmusic.ui.utils.TitleAndArtist
import com.hua.abstractmusic.utils.albumArtUri
import com.hua.abstractmusic.utils.artUri
import com.hua.abstractmusic.utils.artist
import com.hua.abstractmusic.utils.title


/**
 * @author : huaweikai
 * @Date   : 2022/01/08
 * @Desc   : 主页的控制界面
 */

@Composable
fun HomeController(
    navController: NavHostController,
    viewModel: HomeViewModel,
    modifier: Modifier,
    playListClick: () -> Unit
) {
    Column(
        modifier = modifier,
    ) {
        Controller(viewModel, playListClick)
        HomeNavigation(navController = navController,viewModel)
    }
}

@Composable
fun Controller(
    viewModel: HomeViewModel,
    playListClick: () -> Unit
) {
    val data = viewModel.currentItem.value.metadata
    ConstraintLayout(
        modifier = Modifier
            .height(60.dp)
    ) {
        val (album, title, controller) = createRefs()
        val percent = createGuidelineFromStart(0.7f)
        Image(
            painter = rememberImagePainter(data = data?.albumArtUri) {
                this.error(R.drawable.music)
            },
            contentDescription = "专辑图",
            modifier = Modifier
                .constrainAs(album) {
                    start.linkTo(parent.start, 10.dp)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
                .size(50.dp)
        )
        Column(
            modifier = Modifier
                .constrainAs(title) {
                    start.linkTo(album.end, 8.dp)
                    top.linkTo(album.top)
                    end.linkTo(percent, 10.dp)
                    bottom.linkTo(album.bottom)
                    width = Dimension.fillToConstraints
                }
        ) {
            TitleAndArtist(
                title = "${data?.title}",
                artist = "${data?.artist}"
            )
        }
        Row(
            modifier = Modifier.constrainAs(controller) {
                top.linkTo(parent.top, 2.dp)
                bottom.linkTo(parent.bottom, 2.dp)
                start.linkTo(percent, 5.dp)
                end.linkTo(parent.end, 8.dp)
            }
        ) {
            val stateIcon =
                if (viewModel.playerState.value == SessionPlayer.PLAYER_STATE_PLAYING) {
                    R.drawable.ic_pause
                } else {
                    R.drawable.ic_play
                }
            val list = listOf(
                ControllerBean(stateIcon, "播放"),
                ControllerBean(R.drawable.ic_next, "下一首"),
                ControllerBean(R.drawable.ic_playlist, "播放列表")
            )

            for (i in 0..2) {
                val button = list[i]
                ControllerButton(
                    resId = button.resId,
                    resDesc = button.resDesc,
                    modifier = Modifier
                ) {
                    when (i) {
                        0 -> {
                            viewModel.playOrPause()
                        }
                        1 -> {
                            viewModel.skipIem()
                        }
                        2 -> {
                            playListClick()
                        }
                    }
                }
            }
        }

    }
}

@Composable
fun ControllerButton(
    resId: Int,
    resDesc: String,
    modifier: Modifier = Modifier,
    onclick: () -> Unit
) {
    IconButton(
        modifier = modifier,
        onClick = {
            onclick()
        },
    ) {
        Icon(
            painter = painterResource(id = resId),
            contentDescription = resDesc,
            tint = Color(0xff77D3D0)
        )
    }
}


@Composable
fun HomeNavigation(navController: NavHostController,viewModel: HomeViewModel) {
//    val back = navController.currentBackStackEntryAsState()
    val bars = listOf(
        BottomBarBean("网络音乐", R.drawable.ic_line, Screen.NetScreen.route),
        BottomBarBean("本地音乐", R.drawable.ic_music_icon, Screen.LocalScreen.route),
        BottomBarBean("我的", R.drawable.ic_person_icon, Screen.MineScreen.route)
    )
    BottomNavigation(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        elevation = 0.dp
    ) {
        bars.forEachIndexed { index, item ->
            val selected = item.route == viewModel.homeNavigationState.value
            val color = if (selected) LightColor.playingTitleColor else Color.Black
            BottomNavigationItem(
                selected = selected,
                onClick = {
                    navController.navigate(item.route)
                    viewModel.homeNavigationState.value = item.route
                },
                icon = {
                    Icon(
                        painter = painterResource(id = item.resId),
                        contentDescription = item.name,
                        tint = color
                    )
                },
                label = {
                    Text(
                        text = item.name,
                        textAlign = TextAlign.Center,
                        fontSize = 13.sp,
                        color = color
                    )
                },
                alwaysShowLabel = false,
                selectedContentColor = LightColor.playingTitleColor,
                modifier = Modifier
                    .background(LightColor.backgroundColor)
            )
        }
    }

}
