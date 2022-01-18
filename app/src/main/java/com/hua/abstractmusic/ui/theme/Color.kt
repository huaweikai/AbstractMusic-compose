package com.hua.abstractmusic.ui.theme

import androidx.compose.material.Colors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver


val Purple200 = Color(0xff33b5e5)
val Purple500 = Color(0xFF03DAC5)
val Purple700 = Color(0xFF3700B3)
val Teal200 = Color(0xFF03DAC5)
val SkyBule = Color(0xff77D3D0)

@Composable
fun Colors.compositedOnSurface(alpha: Float): Color {
    return onSurface.copy(alpha = alpha).compositeOver(surface)
}

object LightColor{
    val backgroundColor = Color.White
    val playingTitleColor = Color(0xff77D3D0)
}
object NightColor{
    val backgroundColor = Color.Black
    val playingTitleColor = Color(0xff77D3D0)
}