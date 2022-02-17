package com.hua.abstractmusic.services

import android.content.Context
import android.net.Uri
import androidx.media2.common.MediaItem
import androidx.media2.common.MediaMetadata
import com.google.common.collect.ImmutableList
import com.hua.abstractmusic.other.Constant.ALBUM_ID
import com.hua.abstractmusic.other.Constant.ALL_ID
import com.hua.abstractmusic.other.Constant.ARTIST_ID
import com.hua.abstractmusic.other.Constant.ARTIST_TO_ALBUM
import com.hua.abstractmusic.other.Constant.NETWORK_ALBUM_ID
import com.hua.abstractmusic.other.Constant.NETWORK_ALL_MUSIC_ID
import com.hua.abstractmusic.other.Constant.NETWORK_ARTIST_ID
import com.hua.abstractmusic.other.Constant.NETWORK_BANNER_ID
import com.hua.abstractmusic.other.Constant.NETWORK_RECOMMEND_ID
import com.hua.abstractmusic.other.Constant.ROOT_SCHEME
import com.hua.abstractmusic.other.Constant.SHEET_ID
import com.hua.abstractmusic.other.Constant.TYPE_LOCAL_ALBUM
import com.hua.abstractmusic.other.Constant.TYPE_LOCAL_ARTIST
import com.hua.abstractmusic.other.Constant.TYPE_LOCAL_ALL
import com.hua.abstractmusic.other.Constant.TYPE_LOCAL_SHEET
import com.hua.abstractmusic.utils.*


/**
 * @author : huaweikai
 * @Date   : 2021/11/26
 * @Desc   : mediaItem树
 */
class MediaItemTree(
    private val context: Context,
    private val scanner: MediaStoreScanner
) {

    private var treeNodes: MutableMap<String, MediaItemNode?> = mutableMapOf()

    init {
        initialize()
    }

    private fun initialize() {
        val list = listOf(
            ROOT_SCHEME, ALL_ID, ALBUM_ID,
            ARTIST_ID, SHEET_ID, NETWORK_ALBUM_ID,
            NETWORK_ARTIST_ID, NETWORK_BANNER_ID,
            NETWORK_RECOMMEND_ID, NETWORK_ALL_MUSIC_ID
        )
        list.forEach {
            treeNodes[it] = MediaItemNode(
                MediaItem.Builder()
                    .setMetadata(
                        MediaMetadata.Builder().apply {
                            id = it
                            isPlayable = false
                            browserType = MediaMetadata.BROWSABLE_TYPE_MIXED
                        }.build()
                    )
                    .build()
            )
            if (it != ROOT_SCHEME) {
                treeNodes[ROOT_SCHEME]?.addChild(listOf(treeNodes[it]!!.item))
            }
        }
    }

    fun getRootItem(): MediaItem {
        return treeNodes[ROOT_SCHEME]!!.item
    }

    fun getChildren(parentId: String): List<MediaItem>? {
        return scanner.selectLocalList(context, parentId).apply {
            if (treeNodes[parentId] == null) {
                treeNodes[parentId] = MediaItemNode(
                    MediaItem.Builder().build()
                )
            }
            this?.let {
                treeNodes[parentId]!!.setChild(it)
            }
        }
    }

    suspend fun networkGetChildren(parentId: String): List<MediaItem>? {
        val parentIdUri = Uri.parse(parentId)
        return if (parentIdUri.lastPathSegment.isNullOrEmpty()) {
            scanner.selectList(parentIdUri)
        } else {
            scanner.selectMusicById(parentIdUri)
        }.apply {
            if (treeNodes[parentId] == null) {
                treeNodes[parentId] = MediaItemNode(
                    MediaItem.Builder().build()
                )
            }
            this?.let {
                treeNodes[parentId]!!.setChild(it)
            }
        }
    }

    fun getChildItem(
        parentId: String
    ): List<MediaItem> {
        //想的是既然是回调回来拿数据的，那么数据肯定是存在的，直接返回。
        return treeNodes[parentId]?.getChildren() ?: emptyList()
    }

    fun getItem(mediaId: String): MediaItem? {
        val parentUri = Uri.parse(mediaId)
        val realMediaId = parentUri.lastPathSegment
        val parentIndex = mediaId.lastIndexOf(realMediaId ?: "")
        val parentId = mediaId.substring(0, parentIndex - 1)
        val children = treeNodes[parentId]?.getChildren() ?: return null
        return children.find {
            it.metadata?.mediaId == mediaId
        }
    }


    class MediaItemNode(val item: MediaItem) {
        private val children = mutableListOf<MediaItem>()

        fun addChild(mediaItems: List<MediaItem>) {
            this.children.addAll(mediaItems)
        }

        fun setChild(mediaItems: List<MediaItem>) {
            children.clear()
            children.addAll(mediaItems)
        }

        fun getChildren(): List<MediaItem> {
            return ImmutableList.copyOf(children)
        }
    }
}