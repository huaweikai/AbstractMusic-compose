package com.hua.abstractmusic.ui.utils

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


/**
 * @author : huaweikai
 * @Date   : 2022/01/16
 * @Desc   :
 */
@Composable
fun TitleAndArtist(
    title: String,
    subTitle: String,
    modifier: Modifier = Modifier,
    titleStyle :TextStyle.()->TextStyle = {this},
    subTitleStyle: TextStyle.()->TextStyle = {this},
    height:Dp = 0.dp,
    color: Color = MaterialTheme.colorScheme.onBackground
) {
    Text(
        modifier = modifier,
        text = title,
        color = color,
        maxLines = 1,
        overflow = TextOverflow.Visible,
        style = MaterialTheme.typography.titleMedium.titleStyle()
    )
    Spacer(modifier = Modifier.height(height))
    Text(
        modifier = modifier,
        text = subTitle,
        color = color,
        maxLines = 1,
        style = MaterialTheme.typography.titleSmall.subTitleStyle()
    )
}