package com.hua.abstractmusic.ui.home.net.detail

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.hua.abstractmusic.bean.MediaData
import com.hua.abstractmusic.bean.toNavType
import com.hua.abstractmusic.other.NetWork.SERVER_ERROR
import com.hua.abstractmusic.ui.LocalBottomControllerHeight
import com.hua.abstractmusic.ui.LocalHomeNavController
import com.hua.abstractmusic.ui.home.local.artist.ArtistLazyItem
import com.hua.abstractmusic.ui.route.Screen
import com.hua.abstractmusic.ui.utils.*
import kotlinx.coroutines.launch

/**
 * @author : huaweikai
 * @Date   : 2022/02/26
 * @Desc   :
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NetSearchScreen(
    searchViewModel: SearchViewModel = hiltViewModel(),
    navController: NavHostController = LocalHomeNavController.current
) {
    val searchText = searchViewModel.searchText.value
    DisposableEffect(Unit) {
        searchViewModel.initializeController()
        this.onDispose {
            searchViewModel.releaseBrowser()
        }
    }
    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = {
                    TransparentHintTextField(
                        text = searchText.text,
                        hint = searchText.hint,
                        onValueChange = { searchViewModel.addEvent(TextEvent.TextValueChange(it))},
                        onFocusChange = {
                            searchViewModel.addEvent(
                                TextEvent.TextFocusChange(it)
                            )
                        },
                        isHintVisible = searchText.isHintVisible,
                        singleLine = true,
                        textStyle = TextStyle(fontSize = 20.sp),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Search
                        ),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                searchViewModel.search()
                            }
                        )
                    )
                },
                navigationIcon = {
                    NavigationBack {
                        navController.navigateUp()
                        searchViewModel.clear()
                    }
                },
                modifier = Modifier.statusBarsPadding()
            )
        }
    ) {
        val history = searchViewModel.searchHistory.collectAsState(emptyList())
        Column(
            modifier = Modifier
                .padding(it)
                .padding(horizontal = 8.dp)
        ) {
            if (searchViewModel.detailVis.value) {
                SearchSuccess(searchViewModel = searchViewModel)
            } else {
                Text(
                    text = "最近搜索",
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow {
                    repeat(history.value.size) {
                        val title = history.value[it].history
                        Surface(
                            modifier = Modifier
                                .padding(4.dp)
                                .clickable {
                                    searchViewModel.addEvent(TextEvent.TextValueChange(title))
                                    searchViewModel.search()
                                },
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Row {
                                Spacer(
                                    modifier = Modifier
                                        .width(8.dp)
                                        .height(4.dp)
                                )
                                Text(text = title, style = MaterialTheme.typography.bodyLarge)
                                Spacer(
                                    modifier = Modifier
                                        .width(8.dp)
                                        .height(4.dp)
                                )
                            }
                        }

                    }
                }
            }
        }
    }
    BackHandler(true) {
        navController.navigateUp()
        searchViewModel.clear()
    }
}

private val tabs = listOf(
    "歌曲", "专辑", "歌手", "歌单"
)


@OptIn(ExperimentalPagerApi::class)
@Composable
fun SearchSuccess(
    searchViewModel: SearchViewModel
) {
    val pagerState = rememberPagerState()
    val scope = rememberCoroutineScope()
    Column(
        Modifier.fillMaxSize()
    ) {
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            indicator = {
                TabRowDefaults.Indicator(
                    modifier = Modifier.indicatorOffset3(pagerState, it, 30.dp)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(32.dp)
        ) {
            tabs.forEachIndexed { index, s ->
                Tab(selected = index == pagerState.currentPage, onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                }) {
                    Text(text = s, fontSize = 16.sp)
                }
            }
        }
        HorizontalPager(
            count = 4,
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.Top,
            state = pagerState,
            contentPadding = PaddingValues(
                top = 8.dp,
                bottom = LocalBottomControllerHeight.current
            )
        ) { page ->
            val pages = searchViewModel.searchMaps.keys.map { it }
            SearchItem(searchObject = pages[page], searchViewModel = searchViewModel)
        }
    }
}

@SuppressLint("UnsafeOptInUsageError")
@Composable
private fun SearchItem(
    searchObject: SearchObject,
    searchViewModel: SearchViewModel,
    hostController: NavHostController = LocalHomeNavController.current
) {
    val data = searchViewModel.searchMaps[searchObject]?.value
    if (data?.code == SERVER_ERROR) {
        SearchEmpty {

        }
    } else {
        LazyColumn(Modifier.fillMaxWidth()) {
            itemsIndexed(data?.data ?: emptyList()) { index, it ->
                when (searchObject) {
                    is SearchObject.Music -> {
                        MusicItem(data = MediaData(it)) {
                            searchViewModel.setPlayList(
                                index,
                                searchViewModel.searchMusic.value.data!!
                            )
                        }
                    }
                    is SearchObject.Album -> {
                        AlbumItem(item = it) {
                            hostController.navigate("${Screen.LocalAlbumDetail.route}?mediaItem=${it.toNavType()}")
                        }
                    }
                    is SearchObject.Artist -> {
                        ArtistLazyItem(item = it, onClick = {
                            hostController.navigate("${Screen.LocalArtistDetail.route}?mediaItem=${it.toNavType()}")
                        })
                    }
                    is SearchObject.Sheet -> {
                        SheetItem(item = it) {
                            hostController.navigate("${Screen.LocalSheetDetailScreen.route}?sheetId=$it&isSearch=true")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchEmpty(
    onClick: () -> Unit
) {
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "当前并未找到，点击重试")
        Button(onClick = onClick) {
            Text(text = "点击重试")
        }
    }
}

sealed class SearchObject(val value: String) {
    data class Music(val name: String) : SearchObject(name)
    data class Album(val name: String) : SearchObject(name)
    data class Artist(val name: String) : SearchObject(name)
    data class Sheet(val name: String) : SearchObject(name)
}