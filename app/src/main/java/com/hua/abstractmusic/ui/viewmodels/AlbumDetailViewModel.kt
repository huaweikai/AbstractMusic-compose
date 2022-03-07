package com.hua.abstractmusic.ui.viewmodels

import android.annotation.SuppressLint
import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.hua.abstractmusic.base.viewmodel.BaseBrowserViewModel
import com.hua.abstractmusic.bean.MediaData
import com.hua.abstractmusic.services.MediaItemTree
import com.hua.abstractmusic.use_case.UseCase
import com.hua.blur.BlurLibrary
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * @author : huaweikai
 * @Date   : 2022/01/13
 * @Desc   : viewmodel
 */
@SuppressLint("UnsafeOptInUsageError")
@HiltViewModel
class AlbumDetailViewModel @Inject constructor(
    application: Application,
    useCase: UseCase,
    itemTree: MediaItemTree,
    private val blurLibrary: BlurLibrary,
) : BaseBrowserViewModel(application, useCase, itemTree) {
    var id: String? = null
//    var item: MediaItem? = null
    override fun onMediaConnected() {
        localListMap[id!!] = _albumDetail
        playListMap[id!!] = _albumDetail
//        item?.let {
//            getBlurBitmap(item!!.mediaMetadata.artworkUri)
//        }
        refresh()
    }

//    @Inject
//    lateinit var imageLoader: ImageLoader
//
//    private val _blurBitmap = MutableStateFlow<Bitmap>(
//        BitmapFactory.decodeResource(
//            getApplication<Application>().resources,
//            R.drawable.music
//        )
//    )
//    val blurBitmap :StateFlow<Bitmap> = _blurBitmap.asStateFlow()

    private val _albumDetail = mutableStateOf<List<MediaData>>(emptyList())
    val albumDetail: State<List<MediaData>> get() = _albumDetail


//    private fun getBlurBitmap(uri: Any?) {
//        val context = getApplication<Application>().applicationContext
//        if (blurLibrary.isAvailable) {
//            val imageRequest =
//                ImageRequest
//                    .Builder(getApplication<Application>().applicationContext)
//                    .data(uri)
//                    .error(R.drawable.music)
//                    .target(
//                        onSuccess = {
//                            _blurBitmap.value = it.toBitmap().blur(50)
//                        },
//                        onError = {
//                            _blurBitmap.value = BitmapFactory.decodeResource(
//                                context.resources,
//                                R.drawable.music
//                            ).blur(50)
//                        }
//                    )
//                    .build()
//            imageLoader.enqueue(imageRequest)
//        }
//    }

}