package com.hua.abstractmusic.ui.home

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media2.common.SessionPlayer
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.rememberImagePainter
import com.hua.abstractmusic.R
import com.hua.abstractmusic.bean.ui.home.BottomBarBean
import com.hua.abstractmusic.bean.ui.home.ControllerBean
import com.hua.abstractmusic.ui.home.viewmodels.HomeViewModel
import com.hua.abstractmusic.ui.route.Screen
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
    viewModel: HomeViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(androidx.compose.material3.MaterialTheme.colorScheme.background)
            .height(130.dp),
    ) {
        Controller(viewModel)
        HomeNavigation(navController = navController)
    }
}

@Composable
fun Controller(
    viewModel: HomeViewModel
) {
    val data = viewModel.currentItem.value.metadata
    Row(
        modifier = Modifier
            .height(60.dp)
    ) {
        Image(
            painter = rememberImagePainter(data = data?.albumArtUri ){
                   this.error(R.drawable.music)
            },
            contentDescription = "专辑图",
            modifier = Modifier
                .padding(
                    start = 10.dp
                )
                .size(50.dp)
                .align(Alignment.CenterVertically)
        )
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(start = 8.dp)
                .align(Alignment.CenterVertically)
        ) {
            Text(
                modifier = Modifier
                    .padding(top = 5.dp)
                    .fillMaxHeight()
                    .weight(1f),
                text = "${data?.title}",
                textAlign = TextAlign.Left,
                maxLines = 1
            )
            Spacer(modifier = Modifier.padding(top = 10.dp))
            Text(
                modifier = Modifier
                    .padding(bottom = 5.dp)
                    .fillMaxHeight()
                    .weight(1f),
                text = "${data?.artist}",
                textAlign = TextAlign.Left,
                maxLines = 1
            )
        }
        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterVertically)
        ) {
            val stateIcon =
                if(viewModel.playerState.value == SessionPlayer.PLAYER_STATE_PLAYING){
                    R.drawable.ic_pause
                }else{
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
                        .padding(end = 5.dp)
                ) {
                    when (i) {
                        0 -> {
                            viewModel.playOrPause()
                        }
                        1 -> {
                            viewModel.skipIem()
                        }
                        2 -> {}
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
        modifier =  modifier,
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
fun HomeNavigation(navController: NavHostController) {
    val back = navController.currentBackStackEntryAsState()
    val bars = listOf(
        BottomBarBean("网络音乐", R.drawable.ic_line, Screen.NetScreen.route),
        BottomBarBean("本地音乐", R.drawable.ic_music_icon, Screen.LocalScreen.route),
        BottomBarBean("我的", R.drawable.ic_person_icon, Screen.MineScreen.route)
    )
    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp),
        tonalElevation = 0.dp
    ) {
        bars.forEachIndexed { index, item ->
            val selected = item.route == back.value?.destination?.route
            NavigationBarItem(
                selected = selected,
                onClick = {
                    navController.navigate(item.route)
                },
                icon = {
                        Icon(
                            painter = painterResource(id = item.resId),
                            contentDescription = item.name
                        )
                },
                label = {
                    Text(
                        text = item.name,
                        textAlign = TextAlign.Center,
                        fontSize = 13.sp
                    )
                },
                alwaysShowLabel = false,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xff77D3D0),
                    selectedTextColor = Color(0xff77D3D0),
                    indicatorColor = Color.White
                )
            )
        }
    }
}
