package com.hua.abstractmusic.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.hua.abstractmusic.ui.LocalPlayingViewModel
import com.hua.abstractmusic.ui.animateColor
import com.hua.abstractmusic.ui.darkMonetCompatScheme
import com.hua.abstractmusic.ui.lightMonetCompatScheme
import com.hua.abstractmusic.ui.utils.Monet
import com.kieronquinn.monetcompat.core.MonetCompat
import dev.kdrag0n.monet.theme.ColorScheme

private val DarkColorPalette = darkColorScheme()

private val LightColorPalette = lightColorScheme()

val defaultColor = Color(red = 103, green = 80, blue = 164)

//@SuppressLint("NewApi")
@Composable
fun AbstractMusicTheme(
    monet:MonetCompat,
    customColor: ColorScheme? = null,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
//    val dynamicColor = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val montMicColor = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
    val colors = when {
//        dynamicColor && darkTheme && customColor == null -> dynamicDarkColorScheme(LocalContext.current)
//        dynamicColor && !darkTheme && customColor == null -> dynamicLightColorScheme(LocalContext.current)
        montMicColor && !darkTheme && customColor == null -> monet.getMonetColors().lightMonetCompatScheme()
        montMicColor && darkTheme && customColor == null -> monet.getMonetColors().darkMonetCompatScheme()
        customColor != null && darkTheme -> customColor.darkMonetCompatScheme()
        customColor != null && !darkTheme -> customColor.lightMonetCompatScheme()
        darkTheme -> DarkColorPalette
        else -> LightColorPalette
    }
    LocalPlayingViewModel.current.putTransDark(darkTheme)
    MaterialTheme(
        colorScheme = colors.animateColor(),
        typography = Typography,
    ) {
        androidx.compose.material.MaterialTheme(
            colors = Colors(
                primary = colors.primary,
                primaryVariant = colors.inversePrimary,
                secondary = colors.secondary,
                onSecondary = colors.onSecondary,
                secondaryVariant = colors.secondaryContainer,
                background = colors.background,
                onBackground = colors.onBackground,
                surface = colors.surface, error = colors.error,
                onPrimary = colors.onPrimary,
                onSurface = colors.onSurface,
                onError = colors.onError, isLight = darkTheme
            ),
            shapes = Shapes, content = content
        )
    }
}

//@Composable
//fun AbstractMusicTheme(
//    darkTheme: Boolean = isSystemInDarkTheme(),
//    content: @Composable() () -> Unit
//) {
//    val colors = if (darkTheme) {
//        DarkColorPalette
//    } else {
//        LightColorPalette
//    }
//    Surface() {
//        MaterialTheme(
//            colorScheme = colors,
//            typography = Typography,
//            content = content
//        )
//    }
//
//}