package com.hua.abstractmusic.ui.monet

import androidx.annotation.ColorInt
import com.hua.abstractmusic.ui.monet.colors.Srgb
import com.hua.abstractmusic.ui.monet.theme.DynamicMonetColor
import com.hua.abstractmusic.ui.monet.theme.MaterialYouTargets
import com.hua.abstractmusic.ui.monet.theme.MonetColor

/**
 * @author: Chen
 * @createTime: 2021/12/10 5:00 下午
 * @description:
 **/
object Monet {
    //    var factory:ColorSchemeFactory = ColorSchemeFactory.getFactory()
    fun getMonetColor(@ColorInt seed: Int,chromaFactor:Double =1.0): MonetColor {
        return  DynamicMonetColor(
            MaterialYouTargets(chromaFactor),
            seedColor = Srgb(seed),
            chromaFactor= chromaFactor,
        )
    }
}