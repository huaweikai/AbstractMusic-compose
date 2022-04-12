package com.hua.abstractmusic.ui

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import androidx.activity.ComponentActivity
import androidx.annotation.CallSuper
import androidx.annotation.IntRange
import androidx.appcompat.view.menu.ListMenuItemView
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.kieronquinn.monetcompat.core.MonetActivityAccessException
import com.kieronquinn.monetcompat.core.MonetCompat
import com.kieronquinn.monetcompat.extensions.toArgb
import com.kieronquinn.monetcompat.interfaces.MonetColorsChangedListener
import dev.kdrag0n.monet.theme.ColorScheme

/**
 * @author : huaweikai
 * @Date   : 2022/03/21
 * @Desc   :
 */
abstract class MonetActivity: ComponentActivity(), MonetColorsChangedListener {
    private var _monet: MonetCompat? = null
    val monet: MonetCompat
        get() {
            return if(_monet == null) throw MonetActivityAccessException()
            else _monet!!
        }

    /**
     *  Set to true to automatically apply the Monet background color to the Window's background
     *  Requires [recreateMode] to be `false`
     */
    open val applyBackgroundColorToWindow = false

    /**
     *  When s to true to automatically apply the Monet background color to a Toolbar's dropdown menu
     *  background
     */
    open val applyBackgroundColorToMenu = true

    /**
     *  Set to true to call [MonetCompat.updateMonetColors] when the Activity is created.
     *  If disabled, [MonetColorsChangedListener.onMonetColorsChanged] will still be called when
     *  the listener is attached, but using the current colors (if available)
     */
    open val updateOnCreate = true

    /**
     *  When set to true, you can use [onMonetColorsChanged] to create a Configuration change style
     *  setup - if `isInitialChange` is `false`, simply call `recreate()` to recreate the activity.
     *  Please note that as [recreate] recreates the entire activity, this is not recommended unless
     *  you have *already* got Monet colors, for example using
     */
    open val recreateMode = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //This only sets up if needed, otherwise it'll do nothing
        MonetCompat.setup(this)
        _monet = MonetCompat.getInstance()
        monet.addMonetColorsChangedListener(this, (!updateOnCreate && !recreateMode))
        if(updateOnCreate) monet.updateMonetColors()
    }

    override fun onDestroy() {
        super.onDestroy()
        monet.removeMonetColorsChangedListener(this)
        _monet = null
    }

    override fun onCreateView(
        parent: View?,
        name: String,
        context: Context,
        attrs: AttributeSet
    ): View? {
        if (applyBackgroundColorToMenu && name == ListMenuItemView::class.java.name) {
            //Toolbar dropdown menu list item
            (parent?.parent as? View)?.run {
                val background = monet.getBackgroundColorSecondary(context)
                    ?: monet.getBackgroundColor(context)
                backgroundTintList = ColorStateList.valueOf(background)
            }
        }
        val view = super.onCreateView(parent, name, context, attrs)
        return view
    }

    /**
     *  Called when Monet compat has new colors for use, usually when the Activity loads
     *  or the user changes their wallpaper while the app is running.
     */
    @CallSuper
    override fun onMonetColorsChanged(monet: MonetCompat, monetColors: ColorScheme, isInitialChange: Boolean) {
        if(recreateMode && !isInitialChange){
            recreate()
            return
        }
        if(applyBackgroundColorToWindow){
            window.setBackgroundDrawable(ColorDrawable(monet.getBackgroundColor(this)))
        }
    }

}

private fun ColorScheme.getMonetNeutralColor(
    @IntRange(from = 1, to = 2) type: Int,
    @IntRange(from = 50, to = 900) shade: Int
): Color {
    val monetColor = when (type) {
        1 -> neutral1[shade]
        else -> neutral2[shade]
    }?.toArgb() ?: throw Exception("Neutral$type shade $shade doesn't exist")
    return Color(monetColor)
}

private fun ColorScheme.getMonetAccentColor(
    @IntRange(from = 1, to = 3) type: Int,
    @IntRange(from = 50, to = 900) shade: Int
): Color {
    val monetColor = when (type) {
        1 -> accent1[shade]
        2 -> accent2[shade]
        else -> accent3[shade]
    }?.toArgb() ?: throw Exception("Accent$type shade $shade doesn't exist")
    return Color(monetColor)
}

/**
 * Any values that are not set will be chosen to best represent default values given by [dynamicLightColorScheme][androidx.compose.material3.dynamicLightColorScheme]
 * on Android 12+ devices
 */
@Composable
fun ColorScheme.lightMonetCompatScheme(
    primary: Color = getMonetAccentColor(1, 700),
    onPrimary: Color = getMonetNeutralColor(1, 50),
    primaryContainer: Color = getMonetAccentColor(2, 100),
    onPrimaryContainer: Color = getMonetAccentColor(1, 900),
    inversePrimary: Color = getMonetAccentColor(1, 200),
    secondary: Color = getMonetAccentColor(2, 700),
    onSecondary: Color = getMonetNeutralColor(1, 50),
    secondaryContainer: Color = getMonetAccentColor(2, 100),
    onSecondaryContainer: Color = getMonetAccentColor(2, 900),
    tertiary: Color = getMonetAccentColor(3, 600),
    onTertiary: Color = getMonetNeutralColor(1, 50),
    tertiaryContainer: Color = getMonetAccentColor(3, 100),
    onTertiaryContainer: Color = getMonetAccentColor(3, 900),
    background: Color = getMonetNeutralColor(1, 50),
    onBackground: Color = getMonetNeutralColor(1, 900),
    surface: Color = getMonetNeutralColor(1, 50),
    onSurface: Color = getMonetNeutralColor(1, 900),
    surfaceVariant: Color = getMonetNeutralColor(2, 100),
    onSurfaceVariant: Color = getMonetNeutralColor(2, 700),
    inverseSurface: Color = getMonetNeutralColor(1, 800),
    inverseOnSurface: Color = getMonetNeutralColor(2, 50),
    outline: Color = getMonetAccentColor(2, 500),
): androidx.compose.material3.ColorScheme =
    lightColorScheme(
        primary = primary,
        onPrimary = onPrimary,
        primaryContainer = primaryContainer,
        onPrimaryContainer = onPrimaryContainer,
        inversePrimary = inversePrimary,
        secondary = secondary,
        onSecondary = onSecondary,
        secondaryContainer = secondaryContainer,
        onSecondaryContainer = onSecondaryContainer,
        tertiary = tertiary,
        onTertiary = onTertiary,
        tertiaryContainer = tertiaryContainer,
        onTertiaryContainer = onTertiaryContainer,
        background = background,
        onBackground = onBackground,
        surface = surface,
        onSurface = onSurface,
        surfaceVariant = surfaceVariant,
        onSurfaceVariant = onSurfaceVariant,
        inverseSurface = inverseSurface,
        inverseOnSurface = inverseOnSurface,
        outline = outline,
    )

/**
 * Any values that are not set will be chosen to best represent default values given by [dynamicDarkColorScheme][androidx.compose.material3.dynamicDarkColorScheme]
 * on Android 12+ devices
 */
@Composable
fun ColorScheme.darkMonetCompatScheme(
    primary: Color = getMonetAccentColor(1, 200),
    onPrimary: Color = getMonetAccentColor(1, 800),
    primaryContainer: Color = getMonetAccentColor(1, 600),
    onPrimaryContainer: Color = getMonetAccentColor(2, 100),
    inversePrimary: Color = getMonetAccentColor(1, 600),
    secondary: Color = getMonetAccentColor(2, 200),
    onSecondary: Color = getMonetAccentColor(2, 800),
    secondaryContainer: Color = getMonetAccentColor(2, 700),
    onSecondaryContainer: Color = getMonetAccentColor(2, 100),
    tertiary: Color = getMonetAccentColor(3, 200),
    onTertiary: Color = getMonetAccentColor(3, 700),
    tertiaryContainer: Color = getMonetAccentColor(3, 700),
    onTertiaryContainer: Color = getMonetAccentColor(3, 100),
    background: Color = getMonetNeutralColor(1, 900),
    onBackground: Color = getMonetNeutralColor(1, 100),
    surface: Color = getMonetNeutralColor(1, 900),
    onSurface: Color = getMonetNeutralColor(1, 100),
    surfaceVariant: Color = getMonetNeutralColor(2, 700),
    onSurfaceVariant: Color = getMonetNeutralColor(2, 200),
    inverseSurface: Color = getMonetNeutralColor(1, 100),
    inverseOnSurface: Color = getMonetNeutralColor(1, 800),
    outline: Color = getMonetNeutralColor(2, 500)
): androidx.compose.material3.ColorScheme =
    darkColorScheme(
        primary = primary,
        onPrimary = onPrimary,
        primaryContainer = primaryContainer,
        onPrimaryContainer = onPrimaryContainer,
        inversePrimary = inversePrimary,
        secondary = secondary,
        onSecondary = onSecondary,
        secondaryContainer = secondaryContainer,
        onSecondaryContainer = onSecondaryContainer,
        tertiary = tertiary,
        onTertiary = onTertiary,
        tertiaryContainer = tertiaryContainer,
        onTertiaryContainer = onTertiaryContainer,
        background = background,
        onBackground = onBackground,
        surface = surface,
        onSurface = onSurface,
        surfaceVariant = surfaceVariant,
        onSurfaceVariant = onSurfaceVariant,
        inverseSurface = inverseSurface,
        inverseOnSurface = inverseOnSurface,
        outline = outline,
    )


@Composable
fun androidx.compose.material3.ColorScheme.animateColor() = androidx.compose.material3.ColorScheme(
    primary = animateColorAsState(targetValue = this.primary, tween(600)).value,
    onPrimary = animateColorAsState(targetValue = this.onPrimary, tween(600)).value,
    primaryContainer = animateColorAsState(targetValue = this.primaryContainer, tween(600)).value,
    onPrimaryContainer = animateColorAsState(
        targetValue = this.onPrimaryContainer,
        tween(600)
    ).value,
    inversePrimary = animateColorAsState(targetValue = this.inversePrimary, tween(600)).value,
    secondary = animateColorAsState(targetValue = this.secondary, tween(600)).value,
    onSecondary = animateColorAsState(targetValue = this.onSecondary, tween(600)).value,
    secondaryContainer = animateColorAsState(
        targetValue = this.secondaryContainer,
        tween(600)
    ).value,
    onSecondaryContainer = animateColorAsState(
        targetValue = this.onSecondaryContainer,
        tween(600)
    ).value,
    tertiary = animateColorAsState(targetValue = this.tertiary, tween(600)).value,
    onTertiary = animateColorAsState(targetValue = this.onTertiary, tween(600)).value,
    tertiaryContainer = animateColorAsState(targetValue = this.tertiaryContainer, tween(600)).value,
    onTertiaryContainer = animateColorAsState(
        targetValue = this.onTertiaryContainer,
        tween(600)
    ).value,
    background = animateColorAsState(targetValue = this.background, tween(600)).value,
    onBackground = animateColorAsState(targetValue = this.onBackground, tween(600)).value,
    surface = animateColorAsState(targetValue = this.surface, tween(600)).value,
    onSurface = animateColorAsState(targetValue = this.onSurface, tween(600)).value,
    surfaceVariant = animateColorAsState(targetValue = this.surfaceVariant, tween(600)).value,
    onSurfaceVariant = animateColorAsState(targetValue = this.onSurfaceVariant, tween(600)).value,
    inverseSurface = animateColorAsState(targetValue = this.inverseSurface, tween(600)).value,
    inverseOnSurface = animateColorAsState(targetValue = this.inverseOnSurface, tween(600)).value,
    error = animateColorAsState(targetValue = this.error, tween(600)).value,
    onError = animateColorAsState(targetValue = this.onError, tween(600)).value,
    errorContainer = animateColorAsState(targetValue = this.errorContainer, tween(600)).value,
    onErrorContainer = animateColorAsState(targetValue = this.onErrorContainer, tween(600)).value,
    outline = animateColorAsState(targetValue = this.outline, tween(600)).value,
    surfaceTint = animateColorAsState(targetValue = this.surfaceTint, tween(600)).value
)