package com.hua.abstractmusic.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
//import androidx.compose.material.MaterialTheme
//import androidx.compose.material.darkColors
//import androidx.compose.material.lightColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColorScheme(
    primary = Purple200,
    secondary = Teal200,
    onPrimary = Purple200,
    onPrimaryContainer = Purple200,
    background = Color.Black
)


private val LightColorPalette = lightColorScheme(
    primary = Purple200,
    secondary = Teal200,
    onPrimary = Purple200,
    onPrimaryContainer = Purple200,
    onSecondary = Color.White,
    background = Color.White,
    //md3 bottom阴影
    secondaryContainer = Color.White

    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

@Composable
fun AbstractMusicTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable() () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

//    MaterialTheme(
//        colors = colors,
//        typography = Typography,
//        shapes = Shapes,
//        content = content
//    )
    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}