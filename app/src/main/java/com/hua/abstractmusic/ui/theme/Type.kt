package com.hua.abstractmusic.ui.theme

import androidx.compose.material.Text
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

val Typography = Typography(
    titleMedium = TextStyle(
        fontWeight = FontWeight.W400,
        fontSize = 16.sp,
        textAlign = TextAlign.Start,
    ),
    titleSmall = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.W300,
        textAlign = TextAlign.Start
    )
)