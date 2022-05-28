package com.hua.abstractmusic.ui.weight

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.glance.*
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.*
import androidx.glance.appwidget.action.actionStartService
import androidx.glance.layout.*
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import coil.ImageLoader
import coil.request.ImageRequest
import com.hua.abstractmusic.R
import com.hua.abstractmusic.ui.MainActivity
import com.hua.model.parcel.ParcelizeMediaItem
import com.hua.service.MediaConnect
import com.hua.service.PlayerService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @author : huaweikai
 * @Date   : 2022/03/26
 * @Desc   :
 */
class MusicWidget(
    private val parcelItem: ParcelizeMediaItem,
    private val bitmap: Bitmap?,
    private val state: Boolean
) : GlanceAppWidget() {
    @Composable
    override fun Content() {
        val context = LocalContext.current

        Column(
            modifier = GlanceModifier.fillMaxSize().padding(16.dp).background(Color.White).appWidgetBackground().clickable(
                actionStartActivity<MainActivity>()
            )
        ) {
            Row(
                modifier = GlanceModifier.fillMaxWidth().height(80.dp)
            ) {
                Image(
                    provider = BitmapImageProvider(
                        bitmap ?: BitmapFactory.decodeResource(context.resources, R.drawable.music)
                    ),
                    modifier = GlanceModifier.size(64.dp).cornerRadius(8.dp),
                    contentScale = ContentScale.Crop,
                    contentDescription = ""
                )
                Spacer(modifier = GlanceModifier.width(16.dp))
                Column(
                    modifier = GlanceModifier.fillMaxHeight(),
                    verticalAlignment = Alignment.Vertical.CenterVertically,
                    horizontalAlignment = Alignment.Horizontal.CenterHorizontally
                ) {
                    Text(
                        text = parcelItem.title,
                        modifier = GlanceModifier.fillMaxWidth(),
                        style = TextStyle(textAlign = TextAlign.Start)
                    )
                    Spacer(modifier = GlanceModifier.height(8.dp))
                    Text(
                        text = parcelItem.artist,
                        modifier = GlanceModifier.fillMaxWidth(),
                        style = TextStyle(textAlign = TextAlign.Start)
                    )
                    Spacer(GlanceModifier.height(8.dp))
                }
            }
            Spacer(modifier = GlanceModifier.height(16.dp))
            val icon =
                if (state) R.drawable.ic_controller_pause else R.drawable.ic_controller_play
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.Vertical.CenterVertically,
                horizontalAlignment = Alignment.Horizontal.CenterHorizontally
            ) {
                Image(
                    provider = ImageProvider(R.drawable.ic_play_prev),
                    modifier = GlanceModifier.size(24.dp).clickable(

                        actionStartService(
                            Intent(context, PlayerService::class.java).apply {
                                action = "controller_prev"
                            },true
                        )
                    ),
                    contentDescription = ""
                )
                Spacer(modifier = GlanceModifier.width(16.dp))
                Image(
                    provider = ImageProvider(icon),
                    modifier = GlanceModifier.size(32.dp).clickable(
                        actionStartService(
                            Intent(context, PlayerService::class.java).apply {
                                action = "controller"
                            },true
                        )
                    ),
                    contentDescription = ""
                )
                Spacer(modifier = GlanceModifier.width(16.dp))
                Image(
                    provider = ImageProvider(R.drawable.ic_play_next),
                    modifier = GlanceModifier.size(24.dp).clickable(
                        actionStartService(
                            Intent(context, PlayerService::class.java).apply {
                                action = "controller_next"
                            },
                            true
                        )
                    ),
                    contentDescription = ""
                )
            }
        }
    }
}

@AndroidEntryPoint
class MusicWidgetReceiver : GlanceAppWidgetReceiver() {

    @Inject
    lateinit var mediaConnect : MediaConnect
    private val job = SupervisorJob()
    private val scope = CoroutineScope(job + Dispatchers.Main)
    private val nullItem = ParcelizeMediaItem(
        mediaId = "0",
        title = "暂未播放",
        artist = "",
        artUri = "",
        album = ""
    )
    override val glanceAppWidget: GlanceAppWidget
        get() = MusicWidget(nullItem, null, false)

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        val data = when (intent.action) {
            "tran" -> intent.getParcelableExtra<ParcelizeMediaItem>("mediaItem")
            else -> null
        }
        val state = when (intent.action) {
            "tran" -> intent.getBooleanExtra("state", false)
            else -> null
        }
        scope.launch {
            val imageLoader = ImageLoader(context)
            val request = ImageRequest.Builder(context)
                .data(data?.artUri)
                .error(R.drawable.music)
                .build()
            val result = imageLoader.execute(request)
            val bitmap = try {
                (result.drawable as BitmapDrawable).toBitmap()
            } catch (e: Exception) {
                BitmapFactory.decodeResource(context.resources, R.drawable.music)
            }
            MusicWidget(data ?: nullItem, bitmap, state ?: false).updateAll(context)
        }
    }
}