package com.hua.abstractmusic.ui.home

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.transform.RoundedCornersTransformation
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.hua.abstractmusic.R
import com.hua.abstractmusic.bean.MediaData
import com.hua.abstractmusic.bean.ui.home.BottomBarBean
import com.hua.abstractmusic.bean.ui.home.IconBean
import com.hua.abstractmusic.other.Constant.LASTMEDIA
import com.hua.abstractmusic.other.Constant.LASTMEDIAINDEX
import com.hua.abstractmusic.ui.LocalHomeNavController
import com.hua.abstractmusic.ui.LocalPlayingViewModel
import com.hua.abstractmusic.ui.play.detail.ControllerItem
import com.hua.abstractmusic.ui.route.Screen
import com.hua.abstractmusic.ui.utils.TitleAndArtist
import com.hua.abstractmusic.ui.viewmodels.PlayingViewModel
import com.tencent.mmkv.MMKV


/**
 * @author : huaweikai
 * @Date   : 2022/01/08
 * @Desc   : 主页的控制界面
 */

@ExperimentalPagerApi
@Composable
fun HomeController(
    modifier: Modifier,
    playListClick: () -> Unit,
    playScreenClick: () -> Unit,
) {
    Column(
        modifier = modifier
    ) {
        Controller(
            playListClick,
            playScreenClick
        )
        HomeNavigation(modifier)
    }
}

@ExperimentalPagerApi
@SuppressLint("UnsafeOptInUsageError")
@Composable
fun Controller(
    playListClick: () -> Unit,
    playScreenClick: () -> Unit,
    viewModel: PlayingViewModel = LocalPlayingViewModel.current
) {
    val context = LocalContext.current

    val isTouch = remember {
        mutableStateOf(false)
    }
    val mmkv = MMKV.mmkvWithID(LASTMEDIA)
    val pagerState = rememberPagerState(mmkv.decodeInt(LASTMEDIAINDEX, 0))

    LaunchedEffect(viewModel.currentPlayItem.value) {
        val index = viewModel.currentPlayList.value.indexOf(
            MediaData(viewModel.currentPlayItem.value, true)
        )
        pagerState.scrollToPage(if (index < 0) 0 else index)
    }
    LaunchedEffect(pagerState.currentPage) {
        viewModel.skipTo(pagerState.currentPage, true)
    }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clickable {
                playScreenClick()
            },
        constraintSet = controllerConstrains(8.dp)
    ) {
        HorizontalPager(
            state = pagerState,
            count = viewModel.currentPlayList.value.size,
            modifier = Modifier
                .layoutId("pager")
                .isTouch(isTouch),
            verticalAlignment = CenterVertically
        ) { page ->
            val item = viewModel.currentPlayList.value[page].mediaItem.mediaMetadata
            Row(
                Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .apply {
                            data(item.artworkUri)
                            error(R.drawable.music)
                            transformations(RoundedCornersTransformation(10f))
                        }
                        .build(),
                    contentDescription = "专辑图",
                    modifier = Modifier
                        .aspectRatio(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center
                ) {
                    TitleAndArtist(
                        title = "${item.title}",
                        subTitle = "${item.artist}"
                    )
                }
            }
        }
        Row(
            modifier = Modifier.layoutId("controller"),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val playState = viewModel.playerState.collectAsState()
            val stateIcon =
                if (playState.value) {
                    R.drawable.ic_pause
                } else {
                    R.drawable.ic_play
                }
            val list = listOf(
                IconBean(stateIcon, "播放", size = 48.dp, onClick = {
                    viewModel.playOrPause()
                }),
                IconBean(R.drawable.ic_playlist, "播放列表", size = 20.dp, onClick = {
                    playListClick()
                })
            )
            list.forEach {
                ControllerItem(
                    resId = it.resId,
                    desc = it.desc,
                    size = it.size,
                    width = it.width,
                    onClick = it.onClick
                )
            }
        }
    }
}

private fun controllerConstrains(margin: Dp): ConstraintSet {
    return ConstraintSet {
        val pager = createRefFor("pager")
        val controller = createRefFor("controller")
        constrain(pager) {
            start.linkTo(parent.start, 8.dp)
            end.linkTo(controller.start, 8.dp)
            top.linkTo(parent.top, 8.dp)
            bottom.linkTo(parent.bottom, 8.dp)
            height = Dimension.fillToConstraints
            width = Dimension.fillToConstraints
        }
        constrain(controller) {
            end.linkTo(parent.end, 8.dp)
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom)
            width = Dimension.wrapContent
            height = Dimension.fillToConstraints
        }
    }
}


@Composable
fun HomeNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = LocalHomeNavController.current
) {
    val back = navController.currentBackStackEntryAsState()
    val bars = listOf(
        BottomBarBean("网络音乐", R.drawable.ic_line, Screen.NetScreen.route),
        BottomBarBean("本地音乐", R.drawable.ic_music_icon, Screen.LocalScreen.route),
        BottomBarBean("我的", R.drawable.ic_person_icon, Screen.MineScreen.route)
    )
    BottomNavigation(
        modifier = modifier,
        elevation = 0.dp,
        backgroundColor = MaterialTheme.colorScheme.background
    ) {
        bars.forEach { item ->
            val selected = item.route == back.value?.destination?.route
            val color =
                if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
            BottomNavigationItem(
                selected = selected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
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
                selectedContentColor = MaterialTheme.colorScheme.primary
            )
        }
    }

}

@ExperimentalPagerApi
private fun Modifier.isTouch(
    isTouch: MutableState<Boolean> = mutableStateOf(false)
) =
    this.pointerInput(Unit) {
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

                    }
                }
            }
        }
    }