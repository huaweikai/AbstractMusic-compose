package com.hua.abstractmusic.ui.home.local.artist

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.hua.abstractmusic.R
import com.hua.abstractmusic.bean.MediaData
import com.hua.abstractmusic.ui.home.viewmodels.HomeViewModel
import com.hua.abstractmusic.ui.route.Screen
import com.hua.abstractmusic.ui.utils.TitleAndArtist
import com.hua.abstractmusic.utils.albumArtUri
import com.hua.abstractmusic.utils.displayDescription
import com.hua.abstractmusic.utils.title


/**
 * @author : huaweikai
 * @Date   : 2022/01/18
 * @Desc   :
 */
@Composable
fun LocalArtist(
    homeViewModel: HomeViewModel,
    homeNavHostController: NavHostController
){
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
    ){
        itemsIndexed(
            homeViewModel.localArtistList.value,
            key = {_,item-> item.mediaId }
        ){index: Int, item: MediaData ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, bottom = 20.dp)
                    .clickable {
//                        homeViewModel.navigationState.value = false
                        homeNavHostController.navigate("${Screen.LocalArtistDetail.route}?artistIndex=${index}")
                    }
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .apply {
                            data(item.mediaItem.metadata?.albumArtUri)
                            error(R.drawable.music)
                            transformations(CircleCropTransformation())
                        }
                        .build(),
                    contentDescription = "",
                    modifier = Modifier
                        .size(45.dp)
                )
/*                Image(
                    painter = rememberImagePainter(data = item.mediaItem.metadata?.albumArtUri){
                          this.error(R.drawable.music)
                        this.transformations(CircleCropTransformation())
                    },
                    contentDescription = "",
                    Modifier
                        .size(45.dp)
                )*/
                Spacer(modifier = Modifier.padding(horizontal = 5.dp))
                Column(
                    modifier = Modifier.align(Alignment.CenterVertically)
                ){
                    TitleAndArtist(
                        title = "${item.mediaItem.metadata?.title}",
                        artist = "${item.mediaItem.metadata?.displayDescription} 首"
                    )
                }

            }
        }
    }
}