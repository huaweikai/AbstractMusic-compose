package com.hua.abstractmusic.ui.home.local.album.detail

import android.util.Log
import android.view.KeyEvent.ACTION_UP
import android.view.KeyEvent.KEYCODE_BACK
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight.Companion.W400
import androidx.compose.ui.text.font.FontWeight.Companion.W600
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstrainedLayoutReference
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media2.common.MediaItem
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import coil.transform.RoundedCornersTransformation
import com.hua.abstractmusic.R
import com.hua.abstractmusic.ui.home.MusicItem
import com.hua.abstractmusic.ui.home.viewmodels.AlbumDetailViewModel
import com.hua.abstractmusic.ui.home.viewmodels.HomeViewModel
import com.hua.abstractmusic.utils.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

/**
 * @author : huaweikai
 * @Date   : 2022/01/13
 * @Desc   : detail
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LocalAlbumDetail(
    item: MediaItem,
    detailViewModel: AlbumDetailViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(Unit){
        val observer = object :LifecycleObserver{
            @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
            fun onCreate(){
                detailViewModel.initializeController()
                detailViewModel.id = item.metadata?.id?:""
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        this.onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            detailViewModel.releaseBrowser()
        }
    }
    LazyColumn(
        modifier = Modifier.fillMaxWidth()
    ){
        item {
            ConstraintLayout(
                modifier = Modifier.fillMaxWidth()
            ) {
                val (albumArt,albumDesc) = createRefs()
                val centerPercent = createGuidelineFromStart(140.dp)
                val albumDescStartPercent = createGuidelineFromTop(20.dp)
                val albumDescEndPercent = createGuidelineFromTop(180.dp)
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .apply {
                            data(item.metadata?.albumArtUri)
                            error(R.drawable.music)
                            transformations(RoundedCornersTransformation(10f))
                        }
                        .build(),
                    contentDescription = "",
                    modifier = Modifier
                        .constrainAs(albumArt) {
                            start.linkTo(parent.start, 10.dp)
                            top.linkTo(albumDescStartPercent)
                            end.linkTo(centerPercent)
                            bottom.linkTo(albumDescEndPercent)
                        }
                        .size(120.dp)
                )
                Column(
                    modifier = Modifier
                        .constrainAs(albumDesc) {
                            start.linkTo(centerPercent,10.dp)
                            end.linkTo(parent.end, 5.dp)
                            top.linkTo(albumArt.top)
                            bottom.linkTo(albumArt.bottom)
                            width = Dimension.fillToConstraints
                        }
                ) {
                    Text(
                        text = "${item.metadata?.title}",
                        fontWeight = W400,
                        fontSize = 22.sp,
                        textAlign = TextAlign.Start
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = item.metadata?.artist ?: "-",
                        textAlign = TextAlign.Start
                    )
                }
            }
        }
        item{
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .padding(horizontal = 15.dp)
            ) {
                for (i in 0..1) {
                    PlayIcon(
                        Modifier
                            .padding(
                                start = if (i == 1) 15.dp else 0.dp,
                                end = if (i == 0) 15.dp else 0.dp
                            )
                            .fillMaxHeight()
                            .background(
                                MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .weight(1f)
                    ){
                        when(i){
                            0 -> {
                                detailViewModel.setPlaylist(0,detailViewModel.albumDetail.value)
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.padding(bottom = 10.dp))
        }
        item {
            if(!detailViewModel.state.value){
                Row(
                    modifier = Modifier
                        .height(70.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(
                            Alignment.CenterVertically
                        )
                    )
                }
            }
        }
        itemsIndexed(detailViewModel.albumDetail.value){index, item ->
            if(detailViewModel.state.value){
                MusicItem(data = item,Modifier
                    .animateItemPlacement()) {
                    detailViewModel.setPlaylist(index,detailViewModel.albumDetail.value)
                }
            }
        }
        item {
            Column(
                modifier = Modifier.padding(start = 10.dp)
            ){
                val year = item.metadata?.year
                val yearText=if (year != null && year > 0L) {
                    year.toDate()
                }else{
                    "-"
                }
                Spacer(modifier = Modifier.height(5.dp))
                Text(text = "发行年份: $yearText")
                Spacer(modifier = Modifier.height(5.dp))
                Text(text = "歌曲数量: ${item.metadata?.trackCount}")
            }
        }

    }
}
@Composable
fun PlayIcon(
    modifier: Modifier = Modifier,
    onclick:()->Unit
){
    IconButton(onClick = {
        onclick()
    },
        modifier = modifier
    ) {
        Row {
            Icon(
                painter = painterResource(id = R.drawable.ic_mode_play),
                contentDescription = "",
                tint =  Color.White,
            )
            Text(text = "播放全部" , color = Color.White)
        }
    }
}