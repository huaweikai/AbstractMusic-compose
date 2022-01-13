package com.hua.abstractmusic.ui.home.local.album.detail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.TopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.media2.common.MediaItem
import androidx.navigation.NavHostController
import com.hua.abstractmusic.R
import com.hua.abstractmusic.ui.home.viewmodels.HomeViewModel
import com.hua.abstractmusic.utils.title
import kotlinx.coroutines.launch

/**
 * @author : huaweikai
 * @Date   : 2022/01/13
 * @Desc   : detail
 */
@Composable
fun LocalAlbumDetail(
    homeNavHostController: NavHostController,
    item: MediaItem,
    viewModel: HomeViewModel
){
    val scope = rememberCoroutineScope()
    TopAppBar(
        navigationIcon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = "back",
                modifier = Modifier
                    .clickable {
                    homeNavHostController.navigateUp()
                        viewModel.navigationState2.value = true
                }
            )
        },
        title = {
            Text(text = "${item.metadata?.title}")
        },
        backgroundColor = Color.White,
        modifier = Modifier.padding(top=40.dp),
        elevation = 0.dp
    )
}