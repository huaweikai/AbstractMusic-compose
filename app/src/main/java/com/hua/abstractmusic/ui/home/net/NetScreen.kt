package com.hua.abstractmusic.ui.home.net

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.hua.abstractmusic.ui.LocalHomeNavController
import com.hua.abstractmusic.ui.utils.HorizontalBanner
import com.hua.abstractmusic.ui.viewmodels.HomeViewModel
import com.hua.abstractmusic.ui.viewmodels.NetViewModel
import com.hua.abstractmusic.utils.albumArtUri


/**
 * @author : huaweikai
 * @Date   : 2022/01/08
 * @Desc   : 在线音乐的screen
 */
@Composable
fun NetScreen(
    navHostController: NavHostController = LocalHomeNavController.current,
    netViewModel: NetViewModel = hiltViewModel()
) {
    DisposableEffect(Unit) {
//        netViewModel.initializeController()

        this.onDispose {
            netViewModel.releaseBrowser()
        }
    }
    if(netViewModel.state.value){
        LazyColumn(
            verticalArrangement = Arrangement.Top
        ) {
            item {
                HorizontalBanner(
                    netViewModel.bannerList.value.map {
                        it.mediaItem.metadata?.albumArtUri
                    }
                ) {

                }
            }
        }
    }
}
