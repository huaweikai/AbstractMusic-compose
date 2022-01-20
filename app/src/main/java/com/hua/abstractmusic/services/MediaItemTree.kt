package com.hua.abstractmusic.services

import android.content.Context
import android.media.browse.MediaBrowser
import android.net.Uri
import android.util.Log
import androidx.media2.common.MediaItem
import androidx.media2.common.MediaMetadata
import com.google.common.collect.ImmutableList
import com.hua.abstractmusic.other.Constant.ALBUM_ID
import com.hua.abstractmusic.other.Constant.ALL_ID
import com.hua.abstractmusic.other.Constant.ARTIST_ID
import com.hua.abstractmusic.other.Constant.ARTIST_TO_ALBUM
import com.hua.abstractmusic.other.Constant.NETWORK_ALBUM_ID
import com.hua.abstractmusic.other.Constant.NETWORK_ARTIST_ID
import com.hua.abstractmusic.other.Constant.ROOT_SCHEME
import com.hua.abstractmusic.other.Constant.SHEET_ID
import com.hua.abstractmusic.other.Constant.TYPE_ALBUM
import com.hua.abstractmusic.other.Constant.TYPE_ARTIST
import com.hua.abstractmusic.other.Constant.TYPE_NETWORK_ALBUM
import com.hua.abstractmusic.other.Constant.TYPE_NETWORK_ARTIST
import com.hua.abstractmusic.other.Constant.TYPE_ROOT
import com.hua.abstractmusic.other.Constant.TYPE_SHEET
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

    private var treeNodes :MutableMap<String,MediaItemNode?> = mutableMapOf()

    init {
        initialize()
    }

    private fun initialize() {
        treeNodes[ROOT_SCHEME] = MediaItemNode(
            MediaItem.Builder()
                .setMetadata(
                    MediaMetadata.Builder().apply {
                        id = ROOT_SCHEME
                        isPlayable = false
                        browserType = MediaMetadata.BROWSABLE_TYPE_MIXED
                    }.build()
                )
                .build()
        )
        treeNodes[ALL_ID] = MediaItemNode(
            MediaItem.Builder()
                .setMetadata(
                    MediaMetadata.Builder().apply {
                        id = ALL_ID
                        isPlayable = false
                        browserType = MediaMetadata.BROWSABLE_TYPE_MIXED
                    }.build()
                )
                .build()
        )
        treeNodes[ALBUM_ID] = MediaItemNode(
            MediaItem.Builder()
                .setMetadata(
                    MediaMetadata.Builder().apply {
                        id = ALBUM_ID
                        isPlayable = false
                        browserType = MediaMetadata.BROWSABLE_TYPE_MIXED
                    }.build()
                )
                .build()
        )
        treeNodes[ARTIST_ID] = MediaItemNode(
            MediaItem.Builder()
                .setMetadata(
                    MediaMetadata.Builder().apply {
                        id = ARTIST_ID
                        isPlayable = false
                        browserType = MediaMetadata.BROWSABLE_TYPE_MIXED
                    }.build()
                )
                .build()
        )
        treeNodes[SHEET_ID] = MediaItemNode(
            MediaItem.Builder()
                .setMetadata(
                    MediaMetadata.Builder().apply {
                        id = SHEET_ID
                        isPlayable = false
                        browserType = MediaMetadata.BROWSABLE_TYPE_MIXED
                    }.build()
                )
                .build()
        )

        treeNodes[NETWORK_ALBUM_ID] = MediaItemNode(
            MediaItem.Builder()
                .setMetadata(
                    MediaMetadata.Builder().apply {
                        id = NETWORK_ALBUM_ID
                        isPlayable = false
                        browserType = MediaMetadata.BROWSABLE_TYPE_MIXED
                    }.build()
                )
                .build()
        )

        treeNodes[NETWORK_ARTIST_ID] = MediaItemNode(
            MediaItem.Builder()
                .setMetadata(
                    MediaMetadata.Builder().apply {
                        id = NETWORK_ARTIST_ID
                        isPlayable = false
                        browserType = MediaMetadata.BROWSABLE_TYPE_MIXED
                    }.build()
                )
                .build()
        )


        treeNodes[ROOT_SCHEME]?.addChild(listOf(treeNodes[ALL_ID]!!.item))
        treeNodes[ROOT_SCHEME]?.addChild(listOf(treeNodes[ALBUM_ID]!!.item))
        treeNodes[ROOT_SCHEME]?.addChild(listOf(treeNodes[ARTIST_ID]!!.item))
        treeNodes[ROOT_SCHEME]?.addChild(listOf(treeNodes[SHEET_ID]!!.item))
        treeNodes[ROOT_SCHEME]?.addChild(listOf(treeNodes[NETWORK_ARTIST_ID]!!.item))
        treeNodes[ROOT_SCHEME]?.addChild(listOf(treeNodes[NETWORK_ALBUM_ID]!!.item))
    }

    fun getRootItem(): MediaItem {
        return treeNodes[ROOT_SCHEME]!!.item
    }

    suspend fun getChildren(parentId:String):List<MediaItem>?{
        //把之前的逻辑删了，并且把addchild换成setChild，
        // 目的是请求一次都是新的数据,网络请求也可以刷新获取新的，不然只会把第一次请求的返回去
        val parentIdUri = Uri.parse(parentId)
        return when(parentIdUri.authority){
            TYPE_ROOT->{
                val result = scanner.scanAllFromMediaStore(context,parentIdUri)
                treeNodes[parentId]!!.setChild(result)
                result
            }
            TYPE_ALBUM->{
                val result = if(parentIdUri.lastPathSegment.isNullOrEmpty()){
                    scanner.scanAlbumFromMediaStore(context,parentIdUri)
                }else{
                    scanner.scanAlbumMusic(context,parentIdUri)
                }
                if(treeNodes[parentId] == null){
                    treeNodes[parentId] = MediaItemNode(
                        MediaItem.Builder().build()
                    )
                }
                treeNodes[parentId]!!.setChild(result)
                result
            }
            TYPE_ARTIST->{
                val result = if(parentIdUri.lastPathSegment.isNullOrEmpty()){
                    scanner.scanArtistFromMediaStore(context, parentIdUri)
                } else {
                    if (parentId.contains(ARTIST_TO_ALBUM)){
                        scanner.scanArtistAlbumFromMediaStore(context, parentIdUri)
                    }else{
                        scanner.scanArtistMusic(context, parentIdUri)
                    }
                }
                if(treeNodes[parentId] == null){
                    treeNodes[parentId] = MediaItemNode(
                        MediaItem.Builder().build()
                    )
                }
                treeNodes[parentId]!!.setChild(result)
                result
            }
            TYPE_SHEET->{
                //TODO(自定义歌单逻辑，先不动)
                return if(parentIdUri.lastPathSegment.isNullOrEmpty()){
                    scanner.scanSheetListFromRoom(parentIdUri)
                }else{
                    scanner.scanSheetDecs(parentIdUri)
                }
            }
            TYPE_NETWORK_ALBUM->{
                val result =  if(parentIdUri.lastPathSegment.isNullOrEmpty()){
                    scanner.selectAlbumList()
                }else{
                    scanner.selectMusicByAlbum(parentIdUri)
                }
                if(treeNodes[parentId] == null){
                    treeNodes[parentId] = MediaItemNode(
                        MediaItem.Builder().build()
                    )
                }
                treeNodes[parentId]!!.setChild(result)
                result
            }
            TYPE_NETWORK_ARTIST->{
                val result = if(parentIdUri.lastPathSegment.isNullOrEmpty()){
                    scanner.selectArtistList()
                }else{
                    scanner.selectMusicByArtist(parentIdUri)
                }
                if(treeNodes[parentId] == null){
                    treeNodes[parentId] = MediaItemNode(
                        MediaItem.Builder().build()
                    )
                }
                treeNodes[parentId]!!.setChild(result)
                result
            }
            else->null
        }
    }

    fun getChildItem(
        parentId: String
    ):List<MediaItem>{
        //想的是既然是回调回来拿数据的，那么数据肯定是存在的，直接返回。
        return treeNodes[parentId]?.getChildren()?: emptyList()
    }

    fun getItem(mediaId: String):MediaItem?{
        val parentUri = Uri.parse(mediaId)
        val realMediaId = parentUri.lastPathSegment
        val parentIndex = mediaId.lastIndexOf(realMediaId?:"")
        val parentId = mediaId.substring(0,parentIndex - 1)
        val children = treeNodes[parentId]?.getChildren()?:return null
        return children.find{
            it.metadata?.mediaId == mediaId
        }
    }



    class MediaItemNode(val item: MediaItem){
        private val children = mutableListOf<MediaItem>()

        fun addChild(mediaItems: List<MediaItem>){
            this.children.addAll(mediaItems)
        }

        fun setChild(mediaItems: List<MediaItem>){
            children.clear()
            children.addAll(mediaItems)
        }

        fun getChildren():List<MediaItem>{
            return ImmutableList.copyOf(children)
        }
    }
}