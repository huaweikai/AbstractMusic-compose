package com.hua.abstractmusic.ui.home.detail

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.state.GlanceState.getValue
import com.hua.abstractmusic.R
import com.hua.abstractmusic.preference.getValue
import com.hua.model.parcel.ParcelizeMediaItem
import com.hua.model.parcel.defaultParcelizeMediaItem
import kotlinx.coroutines.launch
import kotlin.math.max

/**
 * @author : huaweikai
 * @Date   : 2022/05/26
 * @Desc   :
 */
@Composable
fun DescDetailBottomSheet(
    item: ParcelizeMediaItem,
    config:Configuration = LocalConfiguration.current
){
    Column(
        modifier = Modifier.heightIn(
            min = config.screenHeightDp.dp * 0.4f,
            max = config.screenHeightDp.dp * 0.6f
        ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_level_button),
            modifier = Modifier.padding(16.dp),
            contentDescription = ""
        )
        Text(text = item.title, fontSize = 24.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Column(modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
        ) {
            val desc = item.desc?.let {
                it.ifBlank {
                    "暂无介绍"
                }
            }?:"暂无介绍"
            Text(
                text = desc,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 32.dp),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
            )
        }
    }
}