package com.hua.taglib

class NativeLib {

    /**
     * A native method that is implemented by the 'taglib' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(fd:Int): String

    companion object {
        // Used to load the 'taglib' library on application startup.
        init {
            System.loadLibrary("taglib")
        }
    }
}