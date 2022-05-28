package com.hua.abstractmusic.ui.home.detail

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.state.GlanceState.getValue
import com.hua.abstractmusic.R
import com.hua.abstractmusic.preference.getValue
import com.hua.model.parcel.ParcelizeMediaItem
import com.hua.model.parcel.defaultParcelizeMediaItem
import kotlinx.coroutines.launch

/**
 * @author : huaweikai
 * @Date   : 2022/05/26
 * @Desc   :
 */
@Composable
fun DescDetailBottomSheet(
    item: ParcelizeMediaItem
){
    Column(
        modifier = Modifier.height(IntrinsicSize.Min),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_level_button),
            modifier = Modifier.padding(16.dp),
            contentDescription = ""
        )
        Text(text = item.title, fontSize = 24.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Column(Modifier.fillMaxWidth()) {
            Text(
                text = item.desc ?: "暂无介绍",
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 32.dp),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
            )
        }
    }
}