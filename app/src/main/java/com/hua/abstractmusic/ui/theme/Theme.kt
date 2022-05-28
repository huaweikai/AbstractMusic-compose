package com.hua.abstractmusic.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import com.hua.abstractmusic.ui.*
import com.hua.abstractmusic.ui.utils.Monet
import com.hua.material.materialcolor.scheme.Scheme
import com.kieronquinn.monetcompat.core.MonetCompat
import dev.kdrag0n.monet.theme.ColorScheme

private val DarkColorPalette = darkColorScheme()

private val LightColorPalette = lightColorScheme()

val defaultColor = Color(red = 103, green = 80, blue = 164)

//@SuppressLint("NewApi")
@Composable
fun AbstractMusicTheme(
    monet: MonetCompat,
    customColor: ColorScheme? = null,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
//    val dynamicColor = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val montMicColor = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
    val colors = when {
//        dynamicColor && darkTheme && customColor == null -> dynamicDarkColorScheme(LocalContext.current)
//        dynamicColor && !darkTheme && customColor == null -> dynamicLightColorScheme(LocalContext.current)
        montMicColor && !darkTheme && customColor == null -> monet.getMonetColors().toLightMaterialColors()
        montMicColor && darkTheme && customColor == null -> monet.getMonetColors().toDarkMaterialColors()
        customColor != null && darkTheme -> customColor.toDarkMaterialColors()
        customColor != null && !darkTheme -> customColor.toLightMaterialColors()
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

fun ColorScheme.toLightMaterialColors(
    primary: Color = getMonetAccentColor(1, 700)
): androidx.compose.material3.ColorScheme {
    val colors = Scheme.light(primary.toArgb())
    return lightColorScheme(
        primary = colors.primary.argbToColor(),
        onPrimary = colors.onPrimary.argbToColor(),
        primaryContainer = colors.primaryContainer.argbToColor(),
        onPrimaryContainer = colors.onPrimaryContainer.argbToColor(),
        inversePrimary = colors.inversePrimary.argbToColor(),
        secondary = colors.secondary.argbToColor(),
        onSecondary = colors.onSecondary.argbToColor(),
        secondaryContainer = colors.secondaryContainer.argbToColor(),
        onSecondaryContainer = colors.onSecondaryContainer.argbToColor(),
        tertiary = colors.tertiary.argbToColor(),
        onTertiary = colors.onTertiary.argbToColor(),
        tertiaryContainer = colors.tertiaryContainer.argbToColor(),
        onTertiaryContainer = colors.onTertiaryContainer.argbToColor(),
        background = colors.background.argbToColor(),
        onBackground = colors.onBackground.argbToColor(),
        surface = colors.surface.argbToColor(),
        onSurface = colors.onSurface.argbToColor(),
        surfaceVariant = colors.surfaceVariant.argbToColor(),
        onSurfaceVariant = colors.onSurfaceVariant.argbToColor(),
        surfaceTint = primary,
        inverseSurface = colors.inverseSurface.argbToColor(),
        inverseOnSurface = colors.inverseOnSurface.argbToColor(),
        error = colors.error.argbToColor(),
        onError = colors.onError.argbToColor(),
        errorContainer = colors.errorContainer.argbToColor(),
        onErrorContainer = colors.onErrorContainer.argbToColor(),
        outline = colors.outline.argbToColor()
    )
}

fun ColorScheme.toDarkMaterialColors(
    primary: Color = getMonetAccentColor(1, 700)
): androidx.compose.material3.ColorScheme {
    val colors = Scheme.dark(primary.toArgb())
    return darkColorScheme(
        primary = colors.primary.argbToColor(),
        onPrimary = colors.onPrimary.argbToColor(),
        primaryContainer = colors.primaryContainer.argbToColor(),
        onPrimaryContainer = colors.onPrimaryContainer.argbToColor(),
        inversePrimary = colors.inversePrimary.argbToColor(),
        secondary = colors.secondary.argbToColor(),
        onSecondary = colors.onSecondary.argbToColor(),
        secondaryContainer = colors.secondaryContainer.argbToColor(),
        onSecondaryContainer = colors.onSecondaryContainer.argbToColor(),
        tertiary = colors.tertiary.argbToColor(),
        onTertiary = colors.onTertiary.argbToColor(),
        tertiaryContainer = colors.tertiaryContainer.argbToColor(),
        onTertiaryContainer = colors.onTertiaryContainer.argbToColor(),
        background = colors.background.argbToColor(),
        onBackground = colors.onBackground.argbToColor(),
        surface = colors.surface.argbToColor(),
        onSurface = colors.onSurface.argbToColor(),
        surfaceVariant = colors.surfaceVariant.argbToColor(),
        onSurfaceVariant = colors.onSurfaceVariant.argbToColor(),
        surfaceTint = primary,
        inverseSurface = colors.inverseSurface.argbToColor(),
        inverseOnSurface = colors.inverseOnSurface.argbToColor(),
        error = colors.error.argbToColor(),
        onError = colors.onError.argbToColor(),
        errorContainer = colors.errorContainer.argbToColor(),
        onErrorContainer = colors.onErrorContainer.argbToColor(),
        outline = colors.outline.argbToColor()
    )
}


fun Int.argbToColor() = Color(this)
