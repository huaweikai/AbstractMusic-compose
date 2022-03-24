package com.hua.abstractmusic.ui.navigation

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.accompanist.navigation.material.BottomSheetNavigator
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.bottomSheet
import com.google.accompanist.pager.ExperimentalPagerApi
import com.hua.abstractmusic.bean.NavTypeMediaItem
import com.hua.abstractmusic.bean.defaultParcelizeMediaItem
import com.hua.abstractmusic.other.Constant
import com.hua.abstractmusic.other.Constant.NULL_MEDIA_ITEM
import com.hua.abstractmusic.preference.getValue
import com.hua.abstractmusic.ui.*
import com.hua.abstractmusic.ui.hello.HelloScreen
import com.hua.abstractmusic.ui.home.Controller
import com.hua.abstractmusic.ui.home.HomeScreen
import com.hua.abstractmusic.ui.home.detail.albumdetail.LocalAlbumDetail
import com.hua.abstractmusic.ui.home.detail.artistdetail.LocalArtistDetail
import com.hua.abstractmusic.ui.home.mine.register.LoginScreen
import com.hua.abstractmusic.ui.home.mine.register.RegisterScreen
import com.hua.abstractmusic.ui.home.mine.sheetdetail.SheetDetail
import com.hua.abstractmusic.ui.home.net.detail.NetSearchScreen
import com.hua.abstractmusic.ui.play.PlayListScreen
import com.hua.abstractmusic.ui.play.PlayScreen
import com.hua.abstractmusic.ui.route.Screen
import com.hua.abstractmusic.ui.splash.SplashScreen
import com.hua.abstractmusic.ui.utils.PopupWindow
import com.hua.abstractmusic.ui.viewmodels.PlayingViewModel
import com.hua.abstractmusic.ui.viewmodels.ThemeViewModel
import kotlinx.coroutines.launch

/**
 * @author : huaweikai
 * @Date   : 2022/01/07
 * @Desc   : 整个界面的navigation
 */

val controllerDone = listOf(
    Screen.PlayScreen.route,
    Screen.PlayListScreen.route
)

@OptIn(
    ExperimentalAnimationApi::class,
    com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi::class
)
@ExperimentalPagerApi
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@SuppressLint("UnsafeOptInUsageError")
@Composable
fun AppNavigation(
    themeViewModel: ThemeViewModel = LocalThemeViewModel.current
) {
    var bottomControllerHeight by remember {
        mutableStateOf(0.dp)
    }
    val bottomNavigationHeight = remember {
        mutableStateOf(0.dp)
    }
    val density = LocalDensity.current
    val playingViewModel = LocalPlayingViewModel.current
    val sheetState =
        rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val bottomSheetNavigator = remember {
        BottomSheetNavigator(sheetState)
    }
    val playScreenState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden,skipHalfExpanded = true)
    val controller = rememberNavController(bottomSheetNavigator)

    val popupWindow = remember {
        mutableStateOf(false)
    }
    val popItem = remember {
        mutableStateOf(Constant.NULL_MEDIA_ITEM)
    }
    CompositionLocalProvider(
        LocalAppNavController provides controller,
        LocalBottomControllerHeight provides bottomControllerHeight,
//        LocalNavigationHeight provides bottomNavigationHeight,
        LocalPopWindowItem provides popItem,
        LocalPopWindow provides popupWindow,
    ) {
        PlayScreen(state = playScreenState) {
            Box(modifier = Modifier.fillMaxSize()) {
                ModalBottomSheetLayout(bottomSheetNavigator = bottomSheetNavigator) {
                    NavHost(
                        navController = LocalAppNavController.current,
                        startDestination = Screen.Splash.route
                    ) {
                        router(sheetState, bottomNavigationHeight)
                    }
                }
                MusicController(
                    controller = controller,
                    playingViewModel = playingViewModel,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(
                            bottom = animateDpAsState(8.dp + bottomNavigationHeight.value).value,
                            start = 6.dp,
                            end = 6.dp
                        )
                        .onSizeChanged {
                            bottomControllerHeight = with(density) { it.height.toDp() + 16.dp }
                        },
                    playScreenState = playScreenState
                )
            }
            PopupWindow()
        }
    }
    LaunchedEffect(key1 = themeViewModel.isReady) {
        if (!themeViewModel.isReady.value) themeViewModel.init()
    }
}

@OptIn(
    ExperimentalFoundationApi::class,
    ExperimentalPagerApi::class,
    ExperimentalMaterialApi::class,
    com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi::class
)
fun NavGraphBuilder.router(
    sheetPlayState: ModalBottomSheetState,
    bottomNavigationHeight: MutableState<Dp>
) {
    composable(route = Screen.Splash.route) {
        SplashScreen()
    }
    composable(route = Screen.HelloScreen.route) {
        HelloScreen()
    }
    composable(route = Screen.HomeScreen.route) {

        HomeScreen(
//            onBack = {
//                scope.launch {
//                    sheetPlayState.hide()
//                }
//            },
            onSizeChange = {
                bottomNavigationHeight.value = it
            }
        )
    }
    composable(
        route = "${Screen.AlbumDetailScreen.route}?mediaItem={mediaItem}",
        arguments = listOf(
            navArgument(
                name = "mediaItem"
            ) {
                type = NavTypeMediaItem()
                defaultValue = defaultParcelizeMediaItem
            },
            navArgument(
                name = "isSearch"
            ) {
                type = NavType.BoolType
                defaultValue = false
            }
        ),
    ) {
        val item = it.getValue("mediaItem", defaultParcelizeMediaItem)
        LocalAlbumDetail(
            item
        )
    }
    composable(
        route = "${Screen.ArtistDetailScreen.route}?mediaItem={mediaItem}",
        arguments = listOf(
            navArgument(
                name = "mediaItem"
            ) {
                type = NavTypeMediaItem()
                defaultValue = defaultParcelizeMediaItem
            }
        ),
    ) {
        val item = it.getValue("mediaItem", defaultParcelizeMediaItem)
        LocalArtistDetail(
            item = item
        )
    }

    composable(
        route = Screen.RegisterScreen.route
    ) {
        RegisterScreen()
    }

    composable(
        route = Screen.LoginScreen.route
    ) {
        LoginScreen()
    }
    composable(
        route = "${Screen.SheetDetailScreen.route}?mediaItem={mediaItem}&isUser={isUser}",
        arguments = arrayListOf(
            navArgument(
                name = "mediaItem"
            ) {
                type = NavTypeMediaItem()
                defaultValue = defaultParcelizeMediaItem
            },
            navArgument(
                name = "isUser"
            ) {
                type = NavType.BoolType
                defaultValue = true
            }
        )
    ) {
        val item = it.getValue("mediaItem", defaultParcelizeMediaItem)
        val isUser = item.userId
        SheetDetail(mediaItem = item)
    }

    composable(Screen.NetSearchScreen.route) {
        NetSearchScreen()
    }

    bottomSheet(Screen.PlayListScreen.route) {
        val scope = rememberCoroutineScope()
        Column(modifier = Modifier.fillMaxHeight(0.5f)) {
            PlayListScreen()
            BackHandler(true) {
                scope.launch {
                    sheetPlayState.hide()
                }
            }
        }
    }
    bottomSheet(Screen.PlayScreen.route) {
        Column(modifier = Modifier.fillMaxSize()) {
            PlayScreen()
        }
    }

    //        composable(
//            route = "${Screen.NetDetailScreen.route}?type={type}",
//            arguments = arrayListOf(
//                navArgument(
//                    name = "type"
//                ) {
//                    type = NavType.StringType
//                    defaultValue = ALL_MUSIC_TYPE
//                }
//            )
//        ) {
//            val type = it.getValue("type", ALL_MUSIC_TYPE)
//            NetDetail(type)
//        }
}

@SuppressLint("UnsafeOptInUsageError")
@OptIn(ExperimentalPagerApi::class, ExperimentalMaterialApi::class)
@Composable
fun MusicController(
    controller: NavHostController,
    playScreenState:ModalBottomSheetState,
    playingViewModel: PlayingViewModel,
    modifier: Modifier
) {
    val scope = rememberCoroutineScope()
    val isVis = remember {
        mutableStateOf(false)
    }
    val backState = controller.currentBackStackEntryAsState().value
    LaunchedEffect(playingViewModel.currentPlayItem.value, backState) {
        if (playingViewModel.currentPlayItem.value != NULL_MEDIA_ITEM) {
            isVis.value = backState?.destination?.route !in controllerDone
        } else {
            isVis.value = false
        }
    }
    AnimatedVisibility(
        visible = isVis.value,
        modifier = modifier,
        enter = slideInHorizontally { fullWidth -> -fullWidth },
        exit = slideOutHorizontally { fullWidth -> fullWidth }
    ) {
        Surface(
            modifier = Modifier,
            shape = RoundedCornerShape(8.dp),
            tonalElevation = 3.dp,
        ) {
            Controller(
                playListClick = {
                    controller.navigate(Screen.PlayListScreen.route)
                },
                playScreenClick = {
                    scope.launch {
                        playScreenState.show()
                    }
//                    controller.navigate(Screen.PlayScreen.route)
                })
        }

    }
}