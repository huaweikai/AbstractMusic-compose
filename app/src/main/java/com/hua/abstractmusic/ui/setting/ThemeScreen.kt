package com.hua.abstractmusic.ui.setting

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Colors
import androidx.compose.material.Slider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.extractor.CeaUtil.consume
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.google.accompanist.insets.statusBarsHeight
import com.hua.abstractmusic.ui.*
import com.hua.abstractmusic.ui.home.MainPageItem
import com.hua.abstractmusic.ui.home.pages
import com.hua.abstractmusic.ui.theme.*
import com.hua.abstractmusic.ui.utils.Monet
import com.hua.abstractmusic.ui.viewmodels.ThemeViewModel
import com.kieronquinn.monetcompat.core.MonetCompat
import kotlinx.coroutines.delay
import java.util.*
import kotlin.math.roundToInt

/**
 * @author : huaweikai
 * @Date   : 2022/03/28
 * @Desc   :
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeScreen(
    settingNavController: NavHostController
) {
    val themeViewModel = hiltViewModel<ThemeViewModel>()
    val globalThemeViewModel = LocalThemeViewModel.current
    val primary by themeViewModel.monetColor
    Scaffold {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(it), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(top = 16.dp)
            )
            ColorPicker(themeViewModel, MaterialTheme.colorScheme.primary, cancelCustom = {
                globalThemeViewModel.closeCustomThemeColor()
                settingNavController.navigateUp()
            })
            ThemePreview(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .weight(1f)
                    .padding(vertical = 36.dp),
                color = primary
            )
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun ThemePreview(modifier: Modifier = Modifier, color: dev.kdrag0n.monet.theme.ColorScheme?) {
    PreviewTheme(color) {
        var offsetX by remember {
            mutableStateOf(0f)
        }
        var offsetY by remember {
            mutableStateOf(0f)
        }
        Scaffold(
            modifier,
            topBar = {
                SmallTopAppBar(title = {
                    Text(text = "ThemePreview")
                })
            },
            floatingActionButton = {
                FloatingActionButton(
                    modifier = Modifier
                        .offset {
                            IntOffset(offsetX.roundToInt(), offsetY.roundToInt())
                        }
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consumeAllChanges()
                                offsetX += dragAmount.x
                                offsetY += dragAmount.y
                            }
                        },
                    onClick = { }) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                }
            },
            bottomBar = {
                val list =
                    remember { listOf(MainPageItem.Net, MainPageItem.Local, MainPageItem.Mine) }
                var currentPage by remember {
                    mutableStateOf<MainPageItem>(MainPageItem.Net)
                }

                NavigationBar(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    list.forEach { scene ->
                        NavigationBarItem(selected =
                        currentPage == scene, onClick = {
                            currentPage = scene
                        }, icon = {
                            Icon(
                                painter = painterResource(id = scene.icon),
                                contentDescription = null
                            )
                        }, label = {
                            Text(text = stringResource(id = scene.label))
                        }, alwaysShowLabel = false
                        )
                    }
                }
            }
        ) {
            val colors = MaterialTheme.colorScheme.toList()
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = it
            ) {
                items(colors) { item ->
                    ColorItem(color = item.first, name = item.second)
                }
            }
        }
    }
}

@Composable
fun ColorPicker(
    themeViewModel: ThemeViewModel,
    themeColor: Color,
    cancelCustom: () -> Unit
) {
    val globalThemeViewModel = LocalThemeViewModel.current
    var red by remember {
        mutableStateOf((255 * themeColor.red).roundToInt())
    }
    var green by remember {
        mutableStateOf((255 * themeColor.green).roundToInt())
    }
    var blue by remember {
        mutableStateOf((255 * themeColor.blue).roundToInt())
    }
    val color = Color(red, green, blue)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,

            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(text = "RED:$red", modifier = Modifier.weight(1.3f))
            Slider(
                value = red.toFloat(),
                onValueChange = { red = it.roundToInt() },
                valueRange = 0f..255f,
                modifier = Modifier.weight(4f)
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(text = "GREEN:$green", modifier = Modifier.weight(1.3f))
            Slider(
                value = green.toFloat(),
                onValueChange = { green = it.roundToInt() },
                valueRange = 0f..255f,
                modifier = Modifier.weight(4f)
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(text = "BLUE:$blue", modifier = Modifier.weight(1.3f))
            Slider(
                value = blue.toFloat(),
                onValueChange = { blue = it.roundToInt() },
                valueRange = 0f..255f,
                modifier = Modifier.weight(4f)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        TextButton(onClick = {
            globalThemeViewModel.setCustomThemeColor(color.toArgb())
        }) {
            Text(text = "确认")
        }
        TextButton(onClick = cancelCustom) {
            Text(text = "取消自定义主题色")
        }
    }
    LaunchedEffect(key1 = color) {
        delay(500)
        if (color != themeColor) themeViewModel.getMonetColor(color)
    }
}

@Composable
private fun ColorItem(color: Color, name: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(30.dp), verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(color = color), contentAlignment = Alignment.Center
        ) {
            Text(
                text = "#${
                    color.value.toString(16).subSequence(0, 8)
                }".uppercase(Locale.getDefault()),
                color = contentColorFor(backgroundColor = color)
            )
        }
        Text(text = name, Modifier.weight(1f), textAlign = TextAlign.Center)
    }
}

private fun ColorScheme.toList(): List<Pair<Color, String>> {
    return listOf(
        this.primary to "primary",
        this.onPrimary to "onPrimary",
        this.primaryContainer to "primaryContainer",
        this.onPrimaryContainer to "onPrimaryContainer",
        this.inversePrimary to "inversePrimary",
        this.secondary to "secondary",
        this.onSecondary to "onSecondary",
        this.secondaryContainer to "secondaryContainer",
        this.onSecondaryContainer to "onSecondaryContainer",
        this.tertiary to "tertiary",
        this.onTertiary to "onTertiary",
        this.tertiaryContainer to "tertiaryContainer",
        this.onTertiaryContainer to "onTertiaryContainer",
        this.background to "background",
        this.onBackground to "onBackground",
        this.surface to "surface",
        this.onSurface to "onSurface",
        this.surfaceVariant to "surfaceVariant",
        this.onSurfaceVariant to "onSurfaceVariant",
        this.inverseSurface to "inverseSurface",
        this.inverseOnSurface to "inverseOnSurface",
        this.error to "error",
        this.onError to "onError",
        this.errorContainer to "errorContainer",
        this.onErrorContainer to "onErrorContainer",
        this.outline to "outline",
    )
}

@Composable
fun PreviewTheme(
    customColor: dev.kdrag0n.monet.theme.ColorScheme? = null,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = when {
        customColor != null && darkTheme -> customColor.toDarkMaterialColors()
        customColor != null && !darkTheme -> customColor.toLightMaterialColors()
        darkTheme -> Monet.getMonetColor(defaultColor.toArgb()).toDarkMaterialColors()
        else -> Monet.getMonetColor(defaultColor.toArgb()).toLightMaterialColors()
    }
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