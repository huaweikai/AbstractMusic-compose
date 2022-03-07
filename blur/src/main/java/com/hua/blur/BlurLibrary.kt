package com.hua.blur

import android.graphics.Bitmap
import java.util.concurrent.Callable
import java.util.concurrent.Executors

/**
 * @author : huaweikai
 * @Date   : 2022/03/06
 * @Desc   :
 */
class BlurLibrary {

    /**
     * 当前库是否可获得
     * true 可以获得
     * false 不可获得，此时调用native层会出错
     */
    var isAvailable: Boolean = false
        private set

    init {
        // 加载tagJNI库
        isAvailable = try {
            System.loadLibrary("blur")
            true
        } catch (error: UnsatisfiedLinkError) {
            false
        }
    }
}

fun Bitmap.blur(radius: Int): Bitmap {
    val bitmapOut = copy(Bitmap.Config.ARGB_8888, true)
    val cores = Runtime.getRuntime().availableProcessors()
    val executors = Executors.newFixedThreadPool(cores)
    val horizontal = ArrayList<NativeTask>(cores)
    val vertical = ArrayList<NativeTask>(cores)
    for (i in 0 until cores) {
        horizontal.add(NativeTask(bitmapOut, radius, cores, i, 1))
        vertical.add(NativeTask(bitmapOut, radius, cores, i, 2))
    }
    try {
        executors.invokeAll(horizontal)
    } catch (e: InterruptedException) {
        return bitmapOut
    }
    try {
        executors.invokeAll(vertical)
    } catch (e: InterruptedException) {
        return bitmapOut
    }
    return bitmapOut
}

class NativeTask(
    private val _bitmapOut: Bitmap,
    private val _radius: Int,
    private val _totalCores: Int,
    private val _coreIndex: Int,
    private val _round: Int
) : Callable<Void?> {
    @Throws(Exception::class)
    override fun call(): Void? {
        blur(_bitmapOut, _radius, _totalCores, _coreIndex, _round)
        return null
    }
}


private external fun blur(
    bitmapOut: Bitmap,
    radius: Int,
    threadCount: Int,
    threadIndex: Int,
    round: Int
)