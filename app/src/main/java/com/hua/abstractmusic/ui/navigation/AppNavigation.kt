package com.hua.abstractmusic.ui.navigation

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.res.Configuration
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.*
import androidx.navigation.compose.*
import com.google.accompanist.navigation.material.BottomSheetNavigator
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.bottomSheet
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.google.gson.Gson
import com.hua.abstractmusic.R
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
import com.hua.abstractmusic.ui.setting.SettingScreen
import com.hua.abstractmusic.ui.splash.SplashScreen
import com.hua.abstractmusic.ui.utils.CoilImage
import com.hua.abstractmusic.ui.utils.PopupWindow
import com.hua.abstractmusic.ui.viewmodels.PlayingViewModel
import com.hua.abstractmusic.ui.viewmodels.ThemeViewModel
import com.hua.abstractmusic.utils.toMediaItem
import com.hua.model.parcel.*
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * @author : huaweikai
 * @Date   : 2022/01/07
 * @Desc   : 整个界面的navigation
 */

val controllerDone = listOf(
    Screen.PlayScreen.route,
    Screen.PlayListScreen.route,
    Screen.Splash.route,
    Screen.HelloScreen.route,
    Screen.SettingScreen.route
)

@OptIn(
    ExperimentalAnimationApi::class,
    com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi::class,
    androidx.compose.material3.ExperimentalMaterial3Api::class
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
        rememberModalBottomSheetState(
            ModalBottomSheetValue.Hidden,
            skipHalfExpanded = true
        )
    val bottomSheetNavigator = remember {
        BottomSheetNavigator(sheetState)
    }
    val controller = rememberNavController(bottomSheetNavigator)

    val popupWindow = remember {
        mutableStateOf(false)
    }
    val snackBarHostState = remember { SnackbarHostState() }
    val popItem = remember {
        mutableStateOf(Constant.NULL_MEDIA_ITEM)
    }
    val viewPageState = rememberPagerState(1)
    CompositionLocalProvider(
        LocalAppNavController provides controller,
        LocalBottomControllerHeight provides bottomControllerHeight,
        LocalPopWindowItem provides popItem,
        LocalPopWindow provides popupWindow,
    ) {
        Scaffold(
            snackbarHost = {
                SnackbarHost(hostState = snackBarHostState) { data ->
                    Snackbar(
                        modifier = Modifier.padding(
                            PaddingValues(
                                start = 16.dp,
                                end = 16.dp,
                                bottom = bottomNavigationHeight.value.coerceAtLeast(80.dp)
                            )
                        )
                    ) {
                        Text(text = data.visuals.message)
                    }
                }
            },
            modifier = Modifier.navigationBarsPadding()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                ModalBottomSheetLayout(
                    bottomSheetNavigator = bottomSheetNavigator,
                    sheetShape = RoundedCornerShape(topEnd = 16.dp, topStart = 16.dp),
                ) {
                    NavHost(
                        navController = LocalAppNavController.current,
                        startDestination = Screen.Splash.route
                    ) {
                        router(sheetState, bottomNavigationHeight, viewPageState)
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
                )
            }
            PopupWindow(snackBarHostState = snackBarHostState)
        }
    }
//    LaunchedEffect(key1 = themeViewModel.isReady) {
//        if (!themeViewModel.isReady.value) themeViewModel.init()
//    }
//    val isConnect = playingViewModel.isConnect.collectAsState()
//    LaunchedEffect(isConnect.value) {
//        if (isConnect.value) {
//            playingViewModel.setController()
//        }
//    }

    val clipboardManager = LocalClipboardManager.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()
    DisposableEffect(Unit) {
        val lifecycleObserver = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                scope.launch {
                    delay(800L)
                    clipboardManager.getText()?.let {
                        if(it.text.isNotBlank()){
                            val result = kotlin.runCatching {
                                val share = String(Base64.decode(it.text,Base64.DEFAULT))
                                Gson().fromJson(share, ParcelizeMediaItem::class.java)
                                share
                            }
                            if (result.isSuccess && controller.currentDestination?.route !in controllerDone) {
                                controller.navigate(
                                    "${Screen.ShareSheetDialog.route}?item=${result.getOrNull()}"
                                )
                                clipboardManager.setText(AnnotatedString(""))
                            }
                        }
                    }
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(lifecycleObserver)
        this.onDispose {
            lifecycleOwner.lifecycle.removeObserver(lifecycleObserver)
        }
    }
}

@OptIn(
    ExperimentalFoundationApi::class,
    ExperimentalPagerApi::class,
    ExperimentalMaterialApi::class,
    ExperimentalMaterialNavigationApi::class, ExperimentalMaterial3Api::class
)
fun NavGraphBuilder.router(
    sheetPlayState: ModalBottomSheetState,
    bottomNavigationHeight: MutableState<Dp>,
    viewPageState: PagerState
) {
    composable(route = Screen.Splash.route) {
        SplashScreen()
    }
    composable(route = Screen.HelloScreen.route) {
        HelloScreen()
    }
    composable(route = Screen.HomeScreen.route) {
        HomeScreen(
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
            }
        ),
    ) {
//        val item = it.getValue("mediaItem", defaultParcelizeMediaItem)
        LocalAlbumDetail()
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
//        val item = it.getValue("mediaItem", defaultParcelizeMediaItem)
        LocalArtistDetail()
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
        route = "${Screen.SheetDetailScreen.route}?mediaItem={mediaItem}",
        arguments = arrayListOf(
            navArgument(
                name = "mediaItem"
            ) {
                type = NavTypeMediaItem()
                defaultValue = defaultParcelizeMediaItem
            }
        )
    ) {
        SheetDetail()
    }

    composable(Screen.NetSearchScreen.route) {
        NetSearchScreen()
    }

    composable(Screen.SettingScreen.route) {
        SettingScreen()
    }

    bottomSheet(Screen.PlayListScreen.route) {
        val scope = rememberCoroutineScope()
        PlayListScreen()
        BackHandler(true) {
            scope.launch {
                sheetPlayState.hide()
            }
        }
    }
    bottomSheet(Screen.PlayScreen.route) {
        val scope = rememberCoroutineScope()
        Column(modifier = Modifier.fillMaxSize()) {
            PlayScreen(viewPageState = viewPageState)
            BackHandler(true) {
                scope.launch {
                    sheetPlayState.hide()
                }
            }
        }
    }
    bottomSheet(route = "${Screen.OtherDetail.route}?item={item}", arguments = listOf(
        navArgument("item") {
            type = NavTypeMediaItem()
        }
    )) {
        val item = it.getValue("item", defaultParcelizeMediaItem)
        val scope = rememberCoroutineScope()
        Column(
            modifier = Modifier.height(IntrinsicSize.Min),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_level_button),
                modifier = Modifier.padding(16.dp),
                contentDescription = ""
            )
            Text(text = item.title, fontSize = 24.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Column(Modifier.fillMaxWidth()) {
                Text(
                    text = item.desc ?: "暂无介绍",
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 32.dp),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                )
            }
        }
        BackHandler(true) {
            scope.launch {
                sheetPlayState.hide()
            }
        }
    }
    dialog(route = "${Screen.ShareSheetDialog.route}?item={item}", arguments = listOf(
        navArgument("item") {
            type = NavTypeMediaItem()
        }
    )) {
        val configuration = LocalConfiguration.current
        val item = it.getValue("item", defaultParcelizeMediaItem)
        val clipboardManager = LocalClipboardManager.current
        val appNavController = LocalAppNavController.current
        Card(
            modifier = Modifier
                .width(configuration.screenWidthDp.dp * 0.8f)
                .height(
                    configuration.screenHeightDp.dp * 0.4f
                ),
            shape = RoundedCornerShape(32.dp)
        ) {
            Text(text = "发现了分享的歌单~", fontSize = 22.sp,modifier = Modifier.padding(top = 16.dp, start = 16.dp))
            Column(
                Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CoilImage(
                    url = item.artUri,
                    modifier = Modifier
                        .size(configuration.screenHeightDp.dp * 0.2f)
                        .clip(
                            RoundedCornerShape(16.dp)
                        )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = item.title, fontSize = 22.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = {
                    appNavController.navigate(
                        "${Screen.SheetDetailScreen.route}?mediaItem=${
                            Gson().toJson(item)
                        }"
                    )
                    clipboardManager.setText(AnnotatedString(""))
                }) {
                    Text(text = "立即查看")
                }
            }
        }
    }
}

@SuppressLint("UnsafeOptInUsageError")
@OptIn(ExperimentalPagerApi::class, ExperimentalMaterialApi::class)
@Composable
fun MusicController(
    controller: NavHostController,
//    playScreenState: ModalBottomSheetState,
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
            isVis.value =
                backState?.destination?.route !in controllerDone && backState?.destination?.route?.contains(
                    Screen.OtherDetail.route
                ) == false
        } else {
            isVis.value = false
        }
    }
//    AnimatedVisibility(
//        visible = isVis.value,
//        modifier = modifier,
//        enter = slideInHorizontally { fullWidth -> -fullWidth },
//        exit = slideOutHorizontally { fullWidth -> fullWidth }
//    ) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        tonalElevation = 3.dp,
    ) {
        if (isVis.value) {
            Controller(
                playListClick = {
                    controller.navigate(Screen.PlayListScreen.route)
                },
                playScreenClick = {
                    controller.navigate(Screen.PlayScreen.route)
                })
        }
    }
//    }
}