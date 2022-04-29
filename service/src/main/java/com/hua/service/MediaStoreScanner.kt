package com.hua.service

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import cn.hutool.extra.pinyin.PinyinUtil
import com.hua.model.other.Constants
import com.hua.model.other.Constants.ALBUM_ART_URI
import com.hua.model.other.Constants.DURATION
import com.hua.model.other.Constants.LOCAL_ALBUM_ID
import com.hua.service.usecase.UseCase
import kotlinx.coroutines.runBlocking
import java.util.concurrent.TimeUnit

/**
 * @author : huaweikai
 * @Date   : 2021/11/27
 * @Desc   : 扫描音乐和从数据库提取音乐
 */
@SuppressLint("UnsafeOptInUsageError")
class MediaStoreScanner(
    private val useCase: UseCase,
) {

    //获取音乐
    private val mediaProjection = arrayOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.ALBUM_ID,
        MediaStore.Audio.Media.ALBUM,
        DURATION,
        MediaStore.Audio.Media.TRACK,
        MediaStore.Audio.Media.ARTIST_ID,
        MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media.YEAR
    )
    private val albumProjection = arrayOf(
        MediaStore.Audio.Albums._ID,
        MediaStore.Audio.Albums.ALBUM,
        MediaStore.Audio.Albums.ARTIST,
        MediaStore.Audio.Albums.FIRST_YEAR,
        MediaStore.Audio.Albums.NUMBER_OF_SONGS
    )
    private val artistProjection = arrayOf(
        MediaStore.Audio.Artists._ID,
        MediaStore.Audio.Artists.ARTIST,
        MediaStore.Audio.Artists.NUMBER_OF_TRACKS,
        MediaStore.Audio.Artists.NUMBER_OF_ALBUMS
    )
    private val mediaSelection = "${MediaStore.Audio.Media.IS_MUSIC} != 0 AND $DURATION > ?"

    //搜索规则排除小于5秒
    private val mediaSelectionArgs = arrayOf(
        TimeUnit.MILLISECONDS.convert(5, TimeUnit.MILLISECONDS).toString()
    )

    //从mediaStore媒体库扫描音乐
    fun scanAllFromMediaStore(context: Context, parentId: Uri): List<MediaItem> {
        val cursor = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            mediaProjection,
            mediaSelection,
            mediaSelectionArgs,
            MediaStore.Audio.Media.DEFAULT_SORT_ORDER
        )
        return handleMediaMusicCursor(cursor, parentId)
    }

    //从MediaStore媒体库中扫描专辑
    fun scanAlbumFromMediaStore(context: Context, parentId: Uri): List<MediaItem> {
        val cursor = context.contentResolver.query(
            MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
            albumProjection,
            null,
            null,
            MediaStore.Audio.Albums.DEFAULT_SORT_ORDER
        )
        return handleAlbumCursor(cursor, parentId, context)
    }

    //从MediaStore中获取专辑中的音乐
    private val albumMediaSelection =
        "${MediaStore.Audio.Media.IS_MUSIC} != 0 AND $DURATION > ? AND ${MediaStore.Audio.Media.ALBUM_ID} = ? "

    fun scanAlbumMusic(context: Context, parentId: Uri): List<MediaItem> {
        val albumMediaSelectionArgs = arrayOf(
            TimeUnit.MILLISECONDS.convert(5, TimeUnit.MILLISECONDS).toString(),
            parentId.lastPathSegment
        )
        val cursor = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            mediaProjection,
            albumMediaSelection,
            albumMediaSelectionArgs,
            "${MediaStore.Audio.Media.TRACK} ASC"
        )
        return handleMediaMusicCursor(cursor, parentId)
    }

    private fun handleMediaMusicCursor(cursor: Cursor?, parentId: Uri): List<MediaItem> {
        val localMusicList = mutableListOf<MediaItem>()
        cursor?.use {
            val colId = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            //歌曲标题
            val colTitle = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val colAlbum = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val colAlbumId = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
            val colArtist = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val colArtistId = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST_ID)
            val colDuration = it.getColumnIndexOrThrow(DURATION)
            val colTrack = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK)

            while (it.moveToNext()) {
                val id = it.getLong(colId)
                val title = it.getString(colTitle)
                val album = it.getString(colAlbum)
                val artist = it.getString(colArtist)
                val duration = it.getLong(colDuration)
                val track = it.getInt(colTrack)
                val albumId = it.getLong(colAlbumId)
                val artistId = it.getLong(colArtistId)

                val musicUri =
                    ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
                val albumUri = getAlbumUri(albumId.toString())
                val metadata = MediaMetadata.Builder()
                    .setTitle(title)
                    .setArtist(artist)
                    .setDisplayTitle(title)
                    .setAlbumTitle(album)
                    .setDescription(album)
                    .setTrackNumber(track)
                    .setExtras(
                        Bundle().apply {
                            putLong("albumId", albumId)
                            putLong("artistId", artistId)
                        }
                    )
                    .setArtworkUri(albumUri)
                    .setIsPlayable(true)
                    .setMediaUri(musicUri)
                    .setFolderType(MediaMetadata.FOLDER_TYPE_NONE)
                    .build()
                localMusicList.add(
                    MediaItem.Builder()
                        .setMediaId(parentId.buildUpon().appendPath(id.toString()).toString())
                        .setMediaMetadata(metadata)
                        .setUri(musicUri)
                        .build()
                )
            }
        }
        localMusicList.sortBy {
            PinyinUtil.getPinyin("${it.mediaMetadata.title}")
        }
        return localMusicList
    }

    private fun handleAlbumCursor(
        cursor: Cursor?,
        parentId: Uri,
        context: Context
    ): List<MediaItem> {
        val albumList = mutableListOf<MediaItem>()

        cursor?.use {
            val albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums._ID)
            val albumTitleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ARTIST)
            val trackNumColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.NUMBER_OF_SONGS)
            val yearColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.FIRST_YEAR)
            while (it.moveToNext()) {
                val albumId = it.getLong(albumIdColumn)
                val albumTitle = it.getString(albumTitleColumn)
                val artist = it.getString(artistColumn)
                val trackNum = it.getLong(trackNumColumn)
                val year = it.getLong(yearColumn)

//                val id = parentId.buildUpon().appendPath(albumId.toString()).toString()
                //歌手专辑和专辑适用版
                val id = Uri.parse(LOCAL_ALBUM_ID).buildUpon().appendPath(albumId.toString()).toString()
                //去除秒数小于8的专辑
                if (scanAlbumMusic(context, Uri.parse(id)).isEmpty()) {
                    continue
                }

                val albumUri = getAlbumUri(albumId.toString())
                val mediaMetadata = MediaMetadata.Builder()
                    .setArtworkUri(albumUri)
                    .setReleaseYear(year.toInt())
                    .setTitle(albumTitle)
                    .setAlbumArtist(artist)
                    .setAlbumTitle(albumTitle)
                    .setTrackNumber(trackNum.toInt())
                    .setIsPlayable(false)
                    .setFolderType(MediaMetadata.FOLDER_TYPE_ALBUMS)
                    .setArtist(artist)
                    .build()

                albumList.add(
                    MediaItem.Builder()
                        .setMediaId(id)
                        .setMediaMetadata(mediaMetadata)
                        .build()
                )
            }
        }
        albumList.sortBy {
            PinyinUtil.getPinyin("${it.mediaMetadata.title}")
        }
        return albumList
    }

    /**
     * 从MediaStore媒体库中扫描歌手
     * */
    fun scanArtistFromMediaStore(context: Context, parentId: Uri): List<MediaItem> {
        val cursor = context.contentResolver.query(
            MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
            artistProjection,
            null,
            null,
            MediaStore.Audio.Artists.DEFAULT_SORT_ORDER
        )
        return handleArtistCursor(cursor, parentId, context)
    }

    //从MediaStore中获取歌手的专辑
    fun scanArtistAlbumFromMediaStore(context: Context, parentId: Uri): List<MediaItem> {
        val artistId = parentId.lastPathSegment?.toLong() ?: 0
        val cursor = context.contentResolver.query(
            MediaStore.Audio.Artists.Albums.getContentUri("external", artistId),
            albumProjection,
            null,
            null,
            null
        )
        return handleAlbumCursor(cursor, parentId, context)
    }

    private fun handleArtistCursor(
        cursor: Cursor?,
        parentId: Uri,
        context: Context
    ): List<MediaItem> {
        val localArtists = mutableListOf<MediaItem>()
        cursor?.use {
            val artistIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists._ID)
            val artistTitleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.ARTIST)
            val albumNumColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.NUMBER_OF_ALBUMS)
            while (it.moveToNext()) {
                val artistId = it.getLong(artistIdColumn)
                val artistName = it.getString(artistTitleColumn)
                val albumNum = it.getLong(albumNumColumn)
                val id = parentId.buildUpon().appendPath(artistId.toString()).toString()
                val data = scanArtistMusic(context, Uri.parse(id))
                val albumArtUri = data[0].mediaMetadata.artworkUri
                val trackNum = data.size
                val metadata = MediaMetadata.Builder()
                    .setTitle(artistName)
                    .setTrackNumber(trackNum)
                    .setDiscNumber(albumNum.toInt())
                    .setArtworkUri(albumArtUri)
                    .setIsPlayable(false)
                    .setFolderType((MediaMetadata.FOLDER_TYPE_ARTISTS))
                    .build()
                localArtists.add(
                    MediaItem.Builder()
                        .setMediaId(id)
                        .setMediaMetadata(metadata)
                        .build()
                )
            }
        }
        localArtists.sortBy {
            PinyinUtil.getPinyin("${it.mediaMetadata.title}")
        }
        return localArtists
    }

    fun scanArtistMusic(context: Context, parentId: Uri): List<MediaItem> {
        val cursor = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            mediaProjection,
            "${MediaStore.Audio.Media.IS_MUSIC} != 0 AND $DURATION > ? AND ${MediaStore.Audio.Media.ARTIST_ID} = ?",
            arrayOf(
                TimeUnit.MILLISECONDS.convert(5, TimeUnit.MILLISECONDS).toString(),
                parentId.lastPathSegment
            ),
            "${MediaStore.Audio.Media.TRACK} ASC"
        )
        return handleMediaMusicCursor(cursor, parentId)
    }

    private fun getAlbumUri(albumId: String): Uri {
        val artworkUri = Uri.parse(ALBUM_ART_URI)
        return Uri.withAppendedPath(artworkUri, albumId)
    }

    fun selectLocalList(context: Context, parentId: String): List<MediaItem>? {
        //把之前的逻辑删了，并且把addchild换成setChild，
        // 目的是请求一次都是新的数据,网络请求也可以刷新获取新的，不然只会把第一次请求的返回去
        val parentIdUri = Uri.parse(parentId)
        return when (parentIdUri.authority) {
            Constants.TYPE_LOCAL_ALL -> {
                scanAllFromMediaStore(context, parentIdUri)
            }
            Constants.TYPE_LOCAL_ALBUM -> {
                if (parentIdUri.lastPathSegment.isNullOrEmpty()) {
                    scanAlbumFromMediaStore(context, parentIdUri)
                } else {
                    scanAlbumMusic(context, parentIdUri)
                }
            }
            Constants.TYPE_LOCAL_ARTIST -> {
                if (parentIdUri.lastPathSegment.isNullOrEmpty()) {
                    scanArtistFromMediaStore(context, parentIdUri)
                } else {
                    if (parentId.contains(Constants.ARTIST_TO_ALBUM)) {
                        scanArtistAlbumFromMediaStore(context, parentIdUri)
                    } else {
                        scanArtistMusic(context, parentIdUri)
                    }
                }
            }
            Constants.TYPE_LOCAL_SHEET -> {
                if (parentIdUri.lastPathSegment.isNullOrEmpty()) {
                    runBlocking {
                        useCase.selectInfoBySheet()
                    }
                } else {
                    runBlocking {
                        useCase.selectInfoBySheet(parentIdUri)
                    }
                }
            }
            else -> null
        }
    }
}