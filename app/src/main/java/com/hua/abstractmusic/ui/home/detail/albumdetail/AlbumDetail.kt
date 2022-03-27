package com.hua.abstractmusic.ui.home.detail.albumdetail

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight.Companion.W400
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.transform.RoundedCornersTransformation
import com.hua.abstractmusic.R
import com.hua.abstractmusic.bean.ParcelizeMediaItem
import com.hua.abstractmusic.ui.LocalAppNavController
import com.hua.abstractmusic.ui.LocalBottomControllerHeight
import com.hua.abstractmusic.ui.LocalComposeUtils
import com.hua.abstractmusic.ui.utils.ArtImage
import com.hua.abstractmusic.ui.utils.LCE
import com.hua.abstractmusic.ui.utils.MusicItem
import com.hua.abstractmusic.ui.utils.TitleAndArtist
import com.hua.abstractmusic.utils.isLocal
import com.hua.blur.blur


/**
 * @author : huaweikai
 * @Date   : 2022/01/13
 * @Desc   : detail
 */

@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalFoundationApi
@SuppressLint("UnsafeOptInUsageError")
@Composable
fun LocalAlbumDetail(
    item: ParcelizeMediaItem,
    navHostController: NavHostController = LocalAppNavController.current,
    detailViewModel: AlbumDetailViewModel = hiltViewModel()
) {
    val isLocal = item.mediaId.isLocal()
    val context = LocalContext.current
    val bitmap = remember {
        mutableStateOf(Bitmap.createBitmap(60,60,Bitmap.Config.ARGB_8888))
    }
    val composeUtils = LocalComposeUtils.current
    LaunchedEffect(Unit) {
        bitmap.value = composeUtils.coilToBitmap(item.artUri).blur(50)
        detailViewModel.id = item.mediaId
        detailViewModel.isLocal = isLocal
        detailViewModel.loadData()
    }

    DisposableEffect(Unit) {
        this.onDispose {
            detailViewModel.removeListener()
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            Modifier.fillMaxSize(),
            topBar = {
                SmallTopAppBar(
                    navigationIcon = {
                        IconButton(onClick = { navHostController.navigateUp() }) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "")
                        }
                    },
                    title = {
                        Text(text = "${item.title}")
                    },
                    modifier = Modifier.statusBarsPadding(),
                    colors = TopAppBarDefaults.smallTopAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            }
        ) {
            AlbumShow(item = item, detailViewModel = detailViewModel, isLocal = isLocal)
        }
        Box(
            Modifier
                .fillMaxSize(),
        ) {
            AsyncImage(
                model = bitmap.value,
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .height(300.dp)
                    .fillMaxWidth()
                    .alpha(0.2f)
                    .graphicsLayer { alpha = 0.99F }
                    .drawWithContent {
                        val colors = listOf(
                            Color.Black,
                            Color.Black,
                            Color.Black,
                            Color.Transparent
                        )
                        drawContent()
                        drawRect(
                            brush = Brush.verticalGradient(colors),
                            blendMode = BlendMode.DstIn
                        )
                    }
            )
        }
    }
}


@Composable
fun AlbumShow(
    item: ParcelizeMediaItem,
    detailViewModel: AlbumDetailViewModel,
    isLocal: Boolean = true
) {
    val bottomBarHeight = LocalBottomControllerHeight.current
    LazyColumn(
        Modifier
            .fillMaxSize()
            .background(Color.Transparent),
        contentPadding = PaddingValues(bottom = bottomBarHeight)
    ) {
        item {
            AlbumDetailDesc(item = item)
        }
        if (isLocal) {
            albumItems(detailViewModel)
        } else {
            when (detailViewModel.screenState.value) {
                is LCE.Loading -> {
                    item {
                        Column(
                            Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
                is LCE.Error -> {

                }
                is LCE.Success -> {
                    albumItems(detailViewModel)
                }
            }
            item {
                AlbumDetailTail(item = item)
            }
        }
    }
}

private fun LazyListScope.albumItems(
    detailViewModel: AlbumDetailViewModel
) {
    itemsIndexed(
        detailViewModel.albumDetail.value,
        key = { _, item -> item.mediaId }) { index, item ->
        MusicItem(
            data = item,
            isDetail = true,
            index = index,
            onClick = {
                detailViewModel.setPlayList(index, detailViewModel.albumDetail.value.map { it.mediaItem })
            }
        )
    }
}

@SuppressLint("UnsafeOptInUsageError")
@Composable
private fun AlbumDetailDesc(
    item: ParcelizeMediaItem
) {
    Spacer(modifier = Modifier.height(20.dp))
    Row(
        Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(top = 20.dp, end = 20.dp)
    ) {
        ArtImage(
            modifier = Modifier
                .padding(start = 20.dp)
                .size(120.dp),
            uri = item.artUri,
            desc = "",
            transformation = RoundedCornersTransformation(10f)
        )
        Column(
            modifier = Modifier
                .padding(start = 10.dp, end = 5.dp)
                .fillMaxHeight()
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center
        ) {
            TitleAndArtist(
                title = item.title,
                subTitle = item.artist,
                titleStyle = {
                    this.copy(fontSize = 22.sp)
                },
                subTitleStyle = {
                    this.copy(fontSize = 16.sp)
                },
                height = 5.dp
            )
            if (item.desc?.isNotBlank() == true) {
                Spacer(modifier = Modifier.height(5.dp))
                Text(text = "${item.desc}", maxLines = 2)
            }
        }
    }
    Spacer(modifier = Modifier.height(40.dp))
}


@Composable
fun PlayIcon(
    modifier: Modifier = Modifier,
    desc: String,
    onclick: () -> Unit,
) {
    IconButton(
        onClick = {
            onclick()
        },
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
            .fillMaxHeight()
            .background(
                MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(10.dp)
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_mode_play),
                contentDescription = "",
                tint = Color.White,
            )
            Text(text = desc, color = MaterialTheme.colorScheme.background)
        }
    }
}

@SuppressLint("UnsafeOptInUsageError")
@Composable
private fun AlbumDetailTail(
    item: ParcelizeMediaItem
) {
    Column(
        modifier = Modifier.padding(start = 10.dp)
    ) {
        val year = item.year
        val yearText = if (year != null && year > 0L) {
            "$year"
        } else {
            "-"
        }
        TitleAndArtist(
            title = "发行年份: $yearText",
            subTitle = "歌曲数量: ${item.trackNumber}",
            subTitleStyle = {
                this.copy(fontWeight = W400, fontSize = 14.sp)
            },
            height = 5.dp
        )
    }
}