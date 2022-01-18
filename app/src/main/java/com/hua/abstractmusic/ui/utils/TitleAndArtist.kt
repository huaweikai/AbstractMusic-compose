package com.hua.abstractmusic.ui.utils

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.hua.abstractmusic.utils.artist
import com.hua.abstractmusic.utils.title


/**
 * @author : huaweikai
 * @Date   : 2022/01/16
 * @Desc   :
 */
@Composable
fun TitleAndArtist(
    title:String,
    artist:String,
    color: Color = Color.Black
){
    Text(
        text = title,
        color = color,
        maxLines = 1,
        fontSize = 16.sp,
        fontWeight = FontWeight.W400,
        textAlign = TextAlign.Start,
        overflow = TextOverflow.Visible
    )
    Text(
        text = artist,
        color = color,
        maxLines = 1,
        fontSize = 14.sp,
        fontWeight = FontWeight.W300,
        textAlign = TextAlign.Start
    )
}