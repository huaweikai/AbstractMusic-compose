package com.hua.abstractmusic.ui.home.local.album

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import coil.size.ViewSizeResolver
import coil.transform.CircleCropTransformation
import coil.transform.RoundedCornersTransformation
import com.hua.abstractmusic.R
import com.hua.abstractmusic.ui.home.viewmodels.HomeViewModel
import com.hua.abstractmusic.ui.route.Screen
import com.hua.abstractmusic.utils.albumArtUri
import com.hua.abstractmusic.utils.artist
import com.hua.abstractmusic.utils.title
import kotlinx.coroutines.launch


/**
 * @author : huaweikai
 * @Date   : 2022/01/12
 * @Desc   : localalbum
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LocalAlbum(
    viewModel: HomeViewModel,
    homeNavController: NavHostController
) {
    val scope = rememberCoroutineScope()
    LazyVerticalGrid(
        cells = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp)
    ) {
        itemsIndexed(viewModel.localAlbumList.value) { index, item ->
            Column(
                modifier = Modifier
                    .padding(10.dp),
            ) {
                Image(
                    painter = rememberImagePainter(
                        data = item.mediaItem.metadata?.albumArtUri
                    ) {
                        transformations(RoundedCornersTransformation(40f))
                        error(R.drawable.music)
                    },
                    modifier = Modifier
                        .size(190.dp)
                        .clickable {
                            viewModel.navigationState.value = false
                            homeNavController.navigate("${Screen.LocalAlbumDetail.route}?albumId=${item.mediaId}")
//                            homeNavController.navigate("${Screen.LocalAlbumDetail.route}?albumIndex=${index}")
                        },
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                )
                Text(
                    text = "${item.mediaItem.metadata?.title}",
                    Modifier
                        .height(20.dp)
                        .fillMaxWidth(),
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = "${item.mediaItem.metadata?.artist}",
                    Modifier
                        .height(20.dp)
                        .fillMaxWidth(),
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}