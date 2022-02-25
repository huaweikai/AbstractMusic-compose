package com.hua.abstractmusic.utils

import android.text.format.DateUtils
import com.hua.abstractmusic.ui.play.detail.LyricsEntry
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

/**
 * @author : huaweikai
 * @Date   : 2022/02/24
 * @Desc   :
 */
object LyricsUtils {
    private val dateFormat = SimpleDateFormat("mm:ss.SSS", Locale.CHINESE)

    fun stringToLyrics(lyrics: String): Pair<Boolean, List<LyricsEntry>> {
        if (lyrics.isEmpty()) {
            return Pair(false, emptyList())
        }
        val lyricsList = lyrics.split("\n")

        val lyricsListAll = mutableListOf<LyricsEntry>()

        if (lyricsList.isNullOrEmpty()) {
            return Pair(false, emptyList())
        }
        val canScroll = checkCanScroll(lyricsList[0])
        if (canScroll) {
            for (line in lyricsList) {
                val end = line.indexOf("]") + 1
                if (line.substring(end).isNotBlank()) {
                    val matcher = Pattern.compile("\\[(\\d\\d):(\\d\\d)\\.(\\d+)]").matcher(line)
                    while (matcher.find()) {
                        val min = matcher.group(1)?.toLong() ?: 0L
                        val mus = matcher.group(2)?.toLong() ?: 0L
                        val ss = matcher.group(3)?.toLong() ?: 0L
                        val time =
                            min * DateUtils.MINUTE_IN_MILLIS + mus * DateUtils.SECOND_IN_MILLIS + ss
                        lyricsListAll.add(LyricsEntry(false,time, line.substring(end)))
                    }
                }
            }
        } else {
            for (line in lyricsList) {
                lyricsListAll.add(LyricsEntry(false,null, line))
            }
        }
        return Pair(canScroll, lyricsListAll)
    }

    /**
     * 检测是否是可以滚动的lrc歌词
     * @return true 可滚动 false 不可滚动
     */
    private fun checkCanScroll(line: String): Boolean {
        return Regex("((\\[.+])+)(.*)").containsMatchIn(line)
    }

}