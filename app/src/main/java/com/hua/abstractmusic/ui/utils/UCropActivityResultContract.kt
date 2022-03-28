package com.hua.abstractmusic.ui.utils

/**
 * @author : huaweikai
 * @Date   : 2022/03/03
 * @Desc   :
 */

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract
import com.yalantis.ucrop.UCrop

/**
 * @author Xiaoc
 * @since 2022-02-11
 *
 * 一个裁剪库支持新版ActivityResult API的实例
 */
class UCropActivityResultContract(
    private val options: UCrop.Options? = null
): ActivityResultContract<Pair<Uri, Uri>, Uri?>() {


    override fun createIntent(context: Context, input: Pair<Uri, Uri>): Intent {
        return UCrop.of(input.first, input.second).apply {
            if(options != null){
                withOptions(options)
            }
            withMaxResultSize(720,720)
        }.getIntent(context)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        if(resultCode != Activity.RESULT_OK || intent == null){
            return null
        }
        return UCrop.getOutput(intent)
    }
}