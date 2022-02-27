package com.hua.abstractmusic.ui.utils

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer


/**
 * @author : huaweikai
 * @Date   : 2022/02/27
 * @Desc   :
 */
fun Modifier.translucent()= this.graphicsLayer { alpha = 0.99F }
    .drawWithContent {
        val colors = listOf(
            Color.Transparent, Color.Black, Color.Black, Color.Black, Color.Transparent
        )
        drawContent()
        drawRect(
            brush = Brush.verticalGradient(colors),
            blendMode = BlendMode.DstIn
        )
    }