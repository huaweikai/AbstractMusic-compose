package com.hua.abstractmusic.services

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.media2.common.MediaItem
import androidx.media2.common.MediaMetadata
import com.hua.abstractmusic.other.Constant.ALBUM_ART_URI
import com.hua.abstractmusic.other.Constant.ALBUM_ID
import com.hua.abstractmusic.other.Constant.DURATION
import com.hua.abstractmusic.use_case.UseCase
import com.hua.abstractmusic.utils.*
import kotlinx.coroutines.runBlocking
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * @author : huaweikai
 * @Date   : 2021/11/27
 * @Desc   : 扫描音乐和从数据库提取音乐
 */
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

                val metaDataBuilder = MediaMetadata.Builder().apply {
                    this.id = parentId.buildUpon().appendPath(id.toString()).toString()
                    this.title = title
                    this.displayTitle = title
                    this.displaySubtitle = artist
                    this.displayDescription = album
                    this.album = album
                    this.artist = artist
                    this.duration = duration
                    this.trackNumber = track.toLong()
                    this.mediaUri = musicUri.toString()
                    this.isPlayable = true
                    this.browserType = MediaMetadata.BROWSABLE_TYPE_NONE
                }
                val albumUri = getAlbumUri(albumId.toString())
                metaDataBuilder.albumArtUri = albumUri
                metaDataBuilder.displayIconUri = albumUri

                localMusicList.add(
                    MediaItem.Builder()
                        .setMetadata(metaDataBuilder.build())
                        .build()
                )
            }
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
                val id = Uri.parse(ALBUM_ID).buildUpon().appendPath(albumId.toString()).toString()
                //去除秒数小于8的专辑
                if (scanAlbumMusic(context, Uri.parse(id)).isEmpty()) {
                    continue
                }

                val metadataBuilder = MediaMetadata.Builder().apply {
                    this.id = id
                    this.title = albumTitle
                    this.displayTitle = albumTitle
                    this.displaySubtitle = artist
                    this.displayDescription = trackNum.toString()
                    this.album = albumTitle
                    this.artist = artist
                    this.trackCount = trackNum
                    this.year = year
                    this.isPlayable = false
                    this.browserType = MediaMetadata.BROWSABLE_TYPE_ALBUMS
                }

                val albumUri = getAlbumUri(albumId.toString())
                metadataBuilder.albumArtUri = albumUri
                metadataBuilder.displayIconUri = albumUri

                albumList.add(
                    MediaItem.Builder()
                        .setMetadata(metadataBuilder.build())
                        .build()
                )
            }
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
    fun scanArtistAlbumFromMediaStore(context: Context,parentId: Uri):List<MediaItem>{
        val artistId = parentId.lastPathSegment?.toLong()?:0
        val cursor = context.contentResolver.query(
            MediaStore.Audio.Artists.Albums.getContentUri("external",artistId),
            albumProjection,
            null,
            null,
            null
        )
        return handleAlbumCursor(cursor,parentId, context)
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
                val albumArtUri = data[0].metadata?.albumArtUri
                val trackNum = data.size
                val metadataBuilder = MediaMetadata.Builder().apply {
                    this.id = id
                    this.title = artistName
                    this.displaySubtitle = albumNum.toString()
                    this.displayDescription = trackNum.toString()
                    this.albumArtUri = albumArtUri.toString()
                    this.trackNumber = albumNum
                    this.isPlayable = false
                    this.browserType = MediaMetadata.BROWSABLE_TYPE_MIXED
                }
                localArtists.add(
                    MediaItem.Builder()
                        .setMetadata(metadataBuilder.build())
                        .build()
                )
            }
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


    //处理逻辑都放在了usecase
    fun scanSheetListFromRoom(parentId: Uri): List<MediaItem> {
        return useCase.getSheetNameCase(parentId)
    }

    fun scanSheetDecs(parentId: Uri): List<MediaItem>? {
        if (parentId.lastPathSegment.isNullOrEmpty()) return null
        return useCase.getSheetMusicListCase(parentId.lastPathSegment!!, parentId)
    }

    suspend fun getCurrentPlayList(): List<MediaItem> {
        return useCase.getCurrentListCase()
    }

    private fun getAlbumUri(albumId: String): String {
        val artworkUri = Uri.parse(ALBUM_ART_URI)
        return Uri.withAppendedPath(artworkUri, albumId).toString()
    }

    var executor: ExecutorService = Executors.newFixedThreadPool(5)

    suspend fun selectAlbumList(): List<MediaItem> {
        return useCase.selectNetAlbumCase()
    }

    suspend fun selectMusicByAlbum(parentId: Uri): List<MediaItem> {
        return useCase.selectNetAlbumCase(parentId)
    }

    suspend fun selectArtistList(): List<MediaItem> {
        return useCase.selectNetArtistCase()
    }

    suspend fun selectMusicByArtist(parentId: Uri): List<MediaItem> {
        return useCase.selectNetArtistCase(parentId)
    }
}