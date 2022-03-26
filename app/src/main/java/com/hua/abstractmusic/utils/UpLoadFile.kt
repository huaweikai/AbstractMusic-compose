package com.hua.abstractmusic.utils

import androidx.documentfile.provider.DocumentFile
import com.hua.abstractmusic.other.Constant
import com.obs.services.ObsClient
import com.obs.services.model.ObjectMetadata
import com.obs.services.model.PutObjectRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import java.io.InputStream

/**
 * @author : huaweikai
 * @Date   : 2022/03/24
 * @Desc   :
 */
class UpLoadFile(
    private val obsClient: ObsClient
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
            onError(it.message ?:"")
        }.onCompletion {
            onCompletion()
        }.flowOn(Dispatchers.IO).collect()
    }

}