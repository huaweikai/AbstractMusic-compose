package com.hua.abstractmusic.ui.utils

import androidx.annotation.ColorInt
import dev.kdrag0n.monet.colors.Srgb
import dev.kdrag0n.monet.theme.ColorScheme
import dev.kdrag0n.monet.theme.DynamicColorScheme
import dev.kdrag0n.monet.theme.MaterialYouTargets

/**
 * @author : huaweikai
 * @Date   : 2022/03/21
 * @Desc   :
 */
object Monet {
    //    var factory:ColorSchemeFactory = ColorSchemeFactory.getFactory()
    fun getMonetColor(@ColorInt seed: Int, chromaFactor:Double =1.0): ColorScheme {
        return  DynamicColorScheme(
            MaterialYouTargets(chromaFactor),
            seedColor = Srgb(seed),
            chromaFactor= chromaFactor,
        )
    }
}