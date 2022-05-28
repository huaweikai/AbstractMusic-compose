package com.hua.abstractmusic.ui.sheet

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.state.GlanceState.getValue
import com.google.gson.Gson
import com.hua.abstractmusic.preference.getValue
import com.hua.abstractmusic.ui.LocalAppNavController
import com.hua.abstractmusic.ui.route.Dialog
import com.hua.abstractmusic.ui.route.Screen
import com.hua.abstractmusic.ui.utils.CoilImage
import com.hua.model.parcel.ParcelizeMediaItem
import com.hua.model.parcel.defaultParcelizeMediaItem
import java.util.regex.Pattern


/**
 * @author : huaweikai
 * @Date   : 2022/05/26
 * @Desc   :
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareSheetDialog(
    item: ParcelizeMediaItem
) {
    val configuration = LocalConfiguration.current
    val clipboardManager = LocalClipboardManager.current
    val appNavController = LocalAppNavController.current
    Card(
        modifier = Modifier
            .width(configuration.screenWidthDp.dp * 0.8f)
            .height(
                configuration.screenHeightDp.dp * 0.4f
            ),
        shape = RoundedCornerShape(32.dp)
    ) {
        Text(
            text = "发现了分享的歌单~",
            fontSize = 22.sp,
            modifier = Modifier.padding(top = 16.dp, start = 16.dp)
        )
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CoilImage(
                url = item.artUri,
                modifier = Modifier
                    .size(configuration.screenHeightDp.dp * 0.2f)
                    .clip(
                        RoundedCornerShape(16.dp)
                    )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = item.title, fontSize = 22.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = {
                    appNavController.navigate(
                        "${Screen.SheetDetailScreen.route}?mediaItem=${
                            Gson().toJson(item)
                        }"
                    )
                    clipboardManager.setText(AnnotatedString(""))
            }) {
                Text(text = "立即查看")
            }
        }
    }
}

//fun String.canGoSheetDetail(
//    sheetId: String?
//): Boolean {
//    if (sheetId == null) return true
////    val pattern =
////        Pattern.compile("${Screen.SheetDetailScreen.route}\\?mediaItem=([\\w\\u4e00-\\u9fa5-./\":,{} \\u3002\\uff1b\\uff0c\\uff1a\\u201c\\u201d\\uff08\\uff09\\u3001\\uff1f\\u300a\\u300b]+)")
////    val matcher = pattern.matcher(this)
////    return if (matcher.find()) {
////        val parcelItem = Gson().fromJson(matcher.group(1), ParcelizeMediaItem::class.java)
////        parcelItem.mediaId != sheetId
////    } else {
////        true
////    }
//}