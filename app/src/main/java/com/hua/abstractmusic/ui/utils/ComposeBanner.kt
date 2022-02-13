package com.hua.abstractmusic.ui.utils

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.transform.RoundedCornersTransformation
import com.google.accompanist.pager.*
import com.hua.abstractmusic.R
import kotlinx.coroutines.delay
import kotlin.math.absoluteValue


/**
 * @author : huaweikai
 * @Date   : 2022/02/12
 * @Desc   : banner
 */

@OptIn(ExperimentalPagerApi::class)
@Composable
fun <T> HorizontalBanner(
    items: List<T>,
    repeat: Boolean = true,
    time: Long = 3000L,
    onClick: (Int) -> Unit
) {
    if (items.isEmpty()) {
        Banner(
            items = arrayListOf(R.drawable.music),
            repeat = repeat,
            time = time,
            onClick = {}
        )
    } else {
        Banner(items = items, repeat = repeat, time = time, onClick = onClick)
    }
}

@ExperimentalPagerApi
@Composable
private fun <T> Banner(
    items: List<T>,
    repeat: Boolean,
    time: Long,
    onClick: (Int) -> Unit
) {
    val count = 1e6.toInt()
    val startIndex = count / 2
    val state = rememberPagerState(startIndex)
    val pageCount = items.size

    val isTouch = remember {
        mutableStateOf(false)
    }
    if (repeat) {
        LaunchedEffect(state.currentPage, isTouch.value) {
            delay(time)
            if (!isTouch.value) {
                state.animateScrollToPage((state.currentPage + 1) % state.pageCount)
            }
        }
    }
    val indicatorState = rememberPagerState()

    LaunchedEffect(state.currentPage) {
        val page = (state.currentPage - startIndex).floorMod(pageCount)
        indicatorState.scrollToPage(page)
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        HorizontalPager(
            state = state,
            count = count,
            contentPadding = PaddingValues(horizontal = 32.dp),
            modifier = Modifier
                .moreClick(
                    isTouch = isTouch,
                    state = state
                )
                .padding(horizontal = 10.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.Top,
        ) { index ->
            val page = (index - startIndex).floorMod(pageCount)
            val modifier = Modifier
                .fillMaxSize()
                .bannerItem(calculateCurrentOffsetForPage(index).absoluteValue)
                .fillMaxWidth()
                .aspectRatio(1.75f)
                .clickable {
                    onClick(page)
                }
            Image(
                painter = rememberAsyncImagePainter(model = ImageRequest.Builder(LocalContext.current)
                    .data(items[page])
                    .apply {
                        this.transformations(RoundedCornersTransformation(10f))
                    }
                    .build()
                ),
                contentDescription = null,
                modifier = modifier
            )
        }
        HorizontalPager(
            count = items.size,
            state = indicatorState,
        ) {}
        HorizontalPagerIndicator(
            pagerState = indicatorState,
            activeColor = Color.White,
            inactiveColor = Color.Gray,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 15.dp)
        )
    }
}

private fun Int.floorMod(other: Int): Int = when (other) {
    0 -> this
    else -> this - floorDiv(other) * other
}

private fun Modifier.bannerItem(
    pageOffset: Float
) =
    this.graphicsLayer {
        lerp(
            start = 0.85f,
            stop = 1f,
            fraction = 1f - pageOffset.coerceIn(0f, 1f)
        ).also { scale ->
            scaleX = scale
            scaleY = scale
        }
        alpha = lerp(
            start = 0.5f,
            stop = 1f,
            fraction = 1f - pageOffset.coerceIn(0f, 1f)
        )
    }

@ExperimentalPagerApi
private fun Modifier.moreClick(
    isTouch: MutableState<Boolean> = mutableStateOf(false),
    state: PagerState
) =
    this.pointerInput(Unit) {
        awaitPointerEventScope {
            var pageIndex = 0
            while (true) {
                val event = awaitPointerEvent(PointerEventPass.Initial)
                val dragEvent = event.changes.firstOrNull()
                when {
                    dragEvent!!.positionChangeConsumed() -> {
                        return@awaitPointerEventScope
                    }
                    dragEvent.changedToDownIgnoreConsumed() -> {
                        pageIndex = state.currentPage
                        isTouch.value = true
                    }
                    dragEvent.changedToUpIgnoreConsumed() -> {
                        if (pageIndex == state.currentPage) {
                            isTouch.value = false
                        }
                    }
                }
            }
        }
    }