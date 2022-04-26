package com.hua.taglib

import android.util.Log

/**
 * @author Xiaoc
 * @since 2021/4/8
 *
 * Taglib原生库
 * 定义了native原生方法，以便其调用
 */

class TaglibLibrary {

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
            System.loadLibrary("taglib")
            true
        } catch (error: UnsatisfiedLinkError){
            false
        }
    }

    /**
     * 得到媒体基本通用信息TAG
     * 将其包装为 [MediaTag] 类
     * 如果native层返回null代表获取失败，那么会返回 [MediaTag] 默认值，其中所有内容均为默认空值
     *
     * @param fd 文件描述符，通过该媒体描述符进行native层读取数据
     */
    fun getMediaTag(fd: Int): MediaTag {
        return if(isAvailable){
            return taglibGetMediaTag(fd) ?: run {
                MediaTag()
            }
        } else {
            MediaTag()
        }
    }

    /**
     * 设置媒体基本通用信息TAG
     * 需传递包装为 [MediaTag] 的类
     * 如果native层返回false代表更改失败，返回true则为更改成功
     *
     * @param mediaTag 媒体标签内容
     * @param fd 文件描述符，通过该媒体描述符进行native层读取数据
     */
    fun setMediaTag(mediaTag: MediaTag, fd: Int): Boolean{
        return if(isAvailable){
            taglibSetMediaTag(fd,mediaTag)
        } else {
            false
        }

    }

    /**
     * 得到歌曲内嵌歌词
     *
     * @param fd 文件描述符，通过该媒体描述符进行native层读取数据
     * @return 歌词字符串
     */
    fun getLyricsByTaglib(fd: Int): String{
        return if(isAvailable){
            taglibGetLyrics(fd)
        } else {
            ""
        }
    }

    private external fun taglibGetMediaTag(fd: Int): MediaTag?

    private external fun taglibSetMediaTag(fd: Int,mediaTag: MediaTag): Boolean

    private external fun taglibGetLyrics(fd: Int): String

}

/**
 * MediaTag标签内容
 */
class MediaTag {
    var fileName: String? = null

    var title: String? = null

    var track: Int = 0

    var year: Int = 0

    var genre: String? = null

    var artist: String? = null

    var composer: String? = null

    var album: String? = null

    var comment: String? = null

    var albumArtist: String? = null

    var disc: Int = 0

    var copyright: String? = null

    var bitrate: Int = 0

    var channels: Int = 0

    var sampleRate: Int = 0

    var length: Int = 0

    var size: Long = 0

    override fun toString(): String {
        return "MediaTag(fileName=$fileName, title=$title, track=$track, year=$year, genre=$genre, artist=$artist, composer=$composer, album=$album, comment=$comment, albumArtist=$albumArtist, disc=$disc, copyright=$copyright, bitrate=$bitrate, channels=$channels, sampleRate=$sampleRate, length=$length, size=$size)"
    }

}