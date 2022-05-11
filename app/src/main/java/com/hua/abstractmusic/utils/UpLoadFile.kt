package com.hua.abstractmusic.utils

import android.content.Context
import androidx.documentfile.provider.DocumentFile
import com.hua.abstractmusic.R
import com.hua.abstractmusic.other.Constant
import com.hua.abstractmusic.ui.utils.UiText
import com.obs.services.ObsClient
import com.obs.services.exception.ObsException
import com.obs.services.model.ObjectMetadata
import com.obs.services.model.ObsObject
import com.obs.services.model.PutObjectRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import java.io.InputStream
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author : huaweikai
 * @Date   : 2022/03/24
 * @Desc   :
 */
@Singleton
class UpLoadFile @Inject constructor(
    private val obsClient: ObsClient,
    @ApplicationContext private val context: Context
){

    suspend fun putFile(
        fileName:String,
        byte:InputStream?,
        file:DocumentFile?,
        onSuccess:suspend (String)->Unit,
        onError:suspend (String)->Unit,
        onCompletion:()->Unit
    ){
        val request = PutObjectRequest(Constant.BUCKET_NAME, fileName)
            .apply {
                metadata = ObjectMetadata().apply {
                    this.contentLength = file?.length()
                }
                input = byte
            }
        flow<String> {
            emit(obsClient.putObject(request).objectUrl)
        }.onEach {
            onSuccess(it)
        }.catch {
            onError(UiText.StringResource(R.string.base_network_description_connection_error).asString(context))
        }.flowOn(Dispatchers.IO).onCompletion {
            onCompletion()
        }.flowOn(Dispatchers.Main).collect()
    }

}