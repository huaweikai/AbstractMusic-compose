package com.hua.abstractmusic.ui.home.local.album.detail

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media2.common.MediaItem
import androidx.navigation.NavHostController
import com.hua.abstractmusic.R
import com.hua.abstractmusic.ui.home.viewmodels.AlbumDetailViewModel
import com.hua.abstractmusic.ui.home.viewmodels.HomeViewModel
import com.hua.abstractmusic.ui.theme.utils.scrim
import com.hua.abstractmusic.utils.albumArtUri
import com.hua.abstractmusic.utils.albumArtist
import com.hua.abstractmusic.utils.title

/**
 * @author : huaweikai
 * @Date   : 2022/01/13
 * @Desc   : detail
 */
@Composable
fun LocalAlbumDetail(
    homeNavHostController: NavHostController,
    item: MediaItem,
    homeViewModel: HomeViewModel,
    detailViewModel: AlbumDetailViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current
    Text(text = "${item.metadata?.title}")
//    ConstraintLayout(
//        modifier = Modifier
//            .padding(top = 40.dp)
//            .fillMaxSize()
//    ) {
//        val (topBar) = createRefs()
////        TopAppBar(
////            modifier = Modifier.constrainAs(topBar) {
////                top.linkTo(parent.top)
////                start.linkTo(parent.start)
////                end.linkTo(parent.end)
////                height = Dimension.preferredValue(40.dp)
////                width = Dimension.fillToConstraints
////            },
////            title = {
////                Text(text = "${item.metadata?.title}")
////            },
////            navigationIcon = {
////                Icon(
////                    imageVector = Icons.Default.ArrowBack,
////                    contentDescription = "",
////                    Modifier.clickable {
////                        homeNavHostController.navigateUp()
////                        homeViewModel.navigationState.value = true
////                    }
////                )
////            },
////            backgroundColor = Color.White,
////            elevation = 0.dp
////        )
//    }
//    Column {
//        DisposableEffect(Unit) {
//            val observer = object : LifecycleObserver {
//                @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
//                fun onCreate() {
//                    detailViewModel.initializeController()
//                }
//
//                @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
//                fun onResume() {
//                    detailViewModel.init(item.metadata?.mediaId ?: "")
//                }
//            }
//            lifecycleOwner.lifecycle.addObserver(observer)
//            this.onDispose {
//                detailViewModel.releaseBrowser()
//                lifecycleOwner.lifecycle.removeObserver(observer)
//            }
//        }
//        Text(text = "${item.metadata?.title}")
//    }
}