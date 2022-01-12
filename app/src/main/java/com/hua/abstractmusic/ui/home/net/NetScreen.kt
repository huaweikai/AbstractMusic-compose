package com.hua.abstractmusic.ui.home.net

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.media2.common.MediaMetadata
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.rememberImagePainter
import com.hua.abstractmusic.R
import com.hua.abstractmusic.other.Constant.NETWORK_ALBUM_ID
import com.hua.abstractmusic.other.Constant.NETWORK_ARTIST_ID
import com.hua.abstractmusic.ui.home.MusicItem
import com.hua.abstractmusic.ui.home.viewmodels.HomeViewModel
import com.hua.abstractmusic.utils.*
import kotlinx.coroutines.launch


/**
 * @author : huaweikai
 * @Date   : 2022/01/08
 * @Desc   : 在线音乐的screen
 */
@Composable
fun NetScreen(
    navHostController: NavHostController,
    viewModel: HomeViewModel
) {

    val scope = rememberCoroutineScope()
    Column(
        Modifier.fillMaxSize()
    ) {
        Spacer(modifier = Modifier.padding(top = 100.dp))
        Row(
            Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_album),
                contentDescription = "专辑",
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .size(30.dp)
                    .clickable {
//                        scope.launch {
//                            viewModel.navigationState.value.animateTo(130.dp)
//                        }
                        viewModel.init(NETWORK_ALBUM_ID)
                    }
            )
            Image(
                painter = painterResource(id = R.drawable.ic_artist),
                contentDescription = "歌手",
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .size(30.dp)
                    .clickable {
//                        scope.launch {
//                            viewModel.navigationState.value.animateTo(60.dp)
//                        }
                        viewModel.init(NETWORK_ARTIST_ID)
                    }
            )
        }
        Spacer(modifier = Modifier.padding(top = 20.dp))
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            itemsIndexed(viewModel.netAlbum.value) { index, item ->
                MusicItem(
                    data = item
                ){
                    if (item.mediaItem.metadata?.isPlayable == true &&
                        item.mediaItem.metadata?.browserType == MediaMetadata.BROWSABLE_TYPE_NONE
                    ) {
                        viewModel.setPlaylist(index, viewModel.netAlbum.value)
                    } else {
                        viewModel.init(item.mediaId)
                    }
                }
            }
        }
    }
}
