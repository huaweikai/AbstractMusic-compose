package com.hua.abstractmusic.ui.utils

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


/**
 * @author : huaweikai
 * @Date   : 2022/02/10
 * @Desc   :
 */
@Composable
fun AnimateAlbumEmptyItem(){
    val infiniteTransition = rememberInfiniteTransition()
// 这个是可以使透明度从0到1，重复
    val alpha by infiniteTransition.animateFloat(
        // 开始状态值
        initialValue = 0.4f,
        //目标状态值
        targetValue = 0.2f,
        // 设置动画
        animationSpec = infiniteRepeatable(
            // 关键帧
            animation = keyframes {
                durationMillis = 1000
            },
            repeatMode = RepeatMode.Reverse
        )
    )
    Row(
        modifier = Modifier
            .height(70.dp)
            .padding(start = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .background(MaterialTheme.colorScheme.onBackground.copy(alpha), RoundedCornerShape(5.dp))
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .height(20.dp)
                    .fillMaxWidth(0.6f)
                    .background(MaterialTheme.colorScheme.onBackground.copy(alpha))
            )
            Spacer(modifier = Modifier.padding(5.dp))
            Box(
                modifier = Modifier
                    .height(20.dp)
                    .fillMaxWidth(0.4f)
                    .background(MaterialTheme.colorScheme.onBackground.copy(alpha))
            )
        }
    }
}

@Composable
fun AnimateAlbumItem(
    modifier: Modifier = Modifier
){
    val infiniteTransition = rememberInfiniteTransition()
// 这个是可以使透明度从0到1，重复
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.2f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1000
            },
            repeatMode = RepeatMode.Reverse
        )
    )
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(
                    MaterialTheme.colorScheme.onBackground.copy(alpha),
                    RoundedCornerShape(10.dp)
                )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ){
            Box(
                modifier = Modifier
                    .height(20.dp)
                    .fillMaxWidth(0.6f)
                    .background(MaterialTheme.colorScheme.onBackground.copy(alpha))
            )
            Spacer(modifier = Modifier.padding(5.dp))
            Box(
                modifier = Modifier
                    .height(20.dp)
                    .fillMaxWidth(0.4f)
                    .background(MaterialTheme.colorScheme.onBackground.copy(alpha))
            )
            Spacer(modifier = Modifier.padding(5.dp))
            Box(
                modifier = Modifier
                    .height(20.dp)
                    .fillMaxWidth(0.2f)
                    .background(MaterialTheme.colorScheme.onBackground.copy(alpha))
            )
        }
    }
}