package com.hua.abstractmusic.services

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.google.common.collect.ImmutableList
import com.hua.abstractmusic.other.Constant.ALBUM_ID
import com.hua.abstractmusic.other.Constant.ALL_ID
import com.hua.abstractmusic.other.Constant.ARTIST_ID
import com.hua.abstractmusic.other.Constant.LOCAL_SHEET_ID
import com.hua.abstractmusic.other.Constant.NETWORK_ALBUM_ID
import com.hua.abstractmusic.other.Constant.NETWORK_ALL_MUSIC_ID
import com.hua.abstractmusic.other.Constant.NETWORK_ARTIST_ID
import com.hua.abstractmusic.other.Constant.NETWORK_BANNER_ID
import com.hua.abstractmusic.other.Constant.NETWORK_RECOMMEND_ID
import com.hua.abstractmusic.other.Constant.NET_SHEET_ID
import com.hua.abstractmusic.other.Constant.ROOT_SCHEME


/**
 * @author : huaweikai
 * @Date   : 2021/11/26
 * @Desc   : mediaItem树
 */
@SuppressLint("UnsafeOptInUsageError")
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
            ARTIST_ID, LOCAL_SHEET_ID, NETWORK_ALBUM_ID,
            NETWORK_ARTIST_ID, NETWORK_BANNER_ID,
            NETWORK_RECOMMEND_ID, NETWORK_ALL_MUSIC_ID, NET_SHEET_ID
        )
        list.forEach {
            treeNodes[it] = MediaItemNode(
                MediaItem.Builder()
                    .setMediaMetadata(
                        MediaMetadata.Builder()
                            .setIsPlayable(false)
                            .setFolderType(MediaMetadata.FOLDER_TYPE_MIXED)
                            .build()
                    )
                    .setMediaId(it)
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
            it.mediaId == mediaId
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