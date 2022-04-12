package com.hua.abstractmusic.repository

import android.annotation.SuppressLint
import android.net.Uri
import androidx.media3.common.MediaItem
import com.hua.abstractmusic.base.repository.BaseRepository
import com.hua.abstractmusic.other.Constant
import com.hua.abstractmusic.utils.toMediaItem
import com.hua.model.sheet.SheetMusicPO
import com.hua.model.sheet.SheetPO
import com.hua.model.sheet.SheetToMusicPO
import com.hua.model.sheet.SheetVO
import com.hua.network.ApiError
import com.hua.network.ApiResult
import com.hua.network.Error
import com.hua.service.MediaConnect
import com.hua.model.other.Constants
import com.hua.service.room.dao.MusicDao
import com.hua.service.usecase.events.MusicInsertError
import com.hua.taglib.TaglibLibrary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @author : huaweikai
 * @Date   : 2022/04/10
 * @Desc   :
 */
@SuppressLint("UnsafeOptInUsageError")
class LocalRepository(
    private val mediaConnect: MediaConnect,
    private val taglibLibrary: TaglibLibrary,
    private val dao: MusicDao
) : BaseRepository {
    override suspend fun selectTypeList(parentId: Uri): ApiResult<List<MediaItem>> {
        val cache = mediaConnect.itemTree.getCacheItems("$parentId")
        return ApiResult.Success(
            cache.ifEmpty {
                mediaConnect.getChildren("$parentId")
            }
        )
    }

    override suspend fun selectMusicByType(parentId: Uri): ApiResult<List<MediaItem>> {
        val cache = mediaConnect.itemTree.getChildren("$parentId")
        return ApiResult.Success(
            if (cache.isNullOrEmpty()) {
                mediaConnect.getChildren("$parentId")
            } else {
                cache
            }
        )
    }

    override suspend fun selectLyrics(id: Uri?): String {
        id?:return ""
        val lyrics = if (taglibLibrary.isAvailable) {
            try {
                mediaConnect.context.contentResolver.openFileDescriptor(
                    id,
                    "r"
                )?.use {
                    taglibLibrary.getLyricsByTaglib(it.detachFd())
                } ?: ""
            } catch (e: Exception) {
                ""
            }
        } else {
            ""
        }
        return lyrics
    }


    override suspend fun selectArtistByMusicId(item: MediaItem): ApiResult<List<MediaItem>> {
        val artistId: Long = item.mediaMetadata.extras?.getLong("artistId") ?: 0L
        val parentId = "${Constants.LOCAL_ARTIST_ID}/$artistId"
        return ApiResult.Success(
            listOf(mediaConnect.itemTree.getItem(parentId) ?: Constant.NULL_MEDIA_ITEM)
        )
    }

    override suspend fun selectAlbumByMusicId(item: MediaItem): ApiResult<MediaItem> {
        val parentId =
            "${Constants.LOCAL_ALBUM_ID}/${item.mediaMetadata.extras?.getLong("albumId") ?: 0L}"
        return ApiResult.Success(
            mediaConnect.itemTree.getItem(parentId) ?: Constant.NULL_MEDIA_ITEM
        )
    }

    override suspend fun selectAlbumByArtist(parentId: Uri): ApiResult<List<MediaItem>> {
        return ApiResult.Success(emptyList())
    }

    override suspend fun createSheet(title: String): ApiResult<Unit> {
        val list = dao.selectLocalSheetTitle()
        list?.let {
            if (title in list) return ApiResult.Failure(Error("歌单已存在，无法新建"))
        }
        dao.insertSheet(SheetPO(sheetId = 0, title = title))
        return ApiResult.Success(Unit)
    }

    override suspend fun insertMusicToSheet(
        sheetId: String,
        mediaItem: MediaItem
    ): ApiResult<Unit> {
        val getSheetId = sheetId.toIntOrNull() ?: return ApiResult.Failure(Error("歌单有误"))
        val song = dao.selectMusicIdBySheetId(getSheetId)
        val sheet = dao.selectSheetBySheetId(getSheetId)
        val id = "${Uri.parse(mediaItem.mediaId).lastPathSegment}"
        song?.let {
            if (id in song) return ApiResult.Failure(Error("音乐已经存在！"))
        }
        with(mediaItem.mediaMetadata) {
            SheetMusicPO(
                musicId = "${Uri.parse(mediaItem.mediaId).lastPathSegment}",
                title = "$title",
                displayTitle = "$displayTitle",
                displaySubtitle = "$displayTitle",
                album = "$albumTitle",
                artist = "$artist",
                trackerNumber = trackNumber,
                mediaUri = "$mediaUri",
                albumUri = "$artworkUri",
                artistId = extras?.getLong("artistId") ?: 0L,
                albumId = extras?.getLong("albumId") ?: 0L
            ).also {
                if (sheet.artUri == null) {
                    dao.insertSheet(
                        sheet.copy(
                            artUri = it.albumUri
                        )
                    )
                }
                dao.insertIntoSheet(it)
                dao.insertMusicToSheet(SheetToMusicPO(getSheetId, id))
            }
        }
        return ApiResult.Success(Unit)

    }

    override suspend fun updateSheet(sheet: SheetVO): ApiResult<Unit> {
        dao.insertSheet(
            SheetPO(
                sheetId = sheet.id,
                title = sheet.title,
                artUri = sheet.artUri,
                desc = sheet.sheetDesc
            )
        )
        return ApiResult.Success(Unit)
    }

    override suspend fun selectSheetById(parentId: Uri): ApiResult<MediaItem> {
        val id = parentId.lastPathSegment?.toIntOrNull() ?: return ApiResult.Failure(Error())
        return ApiResult.Success(
            dao.selectSheetBySheetId(id).toMediaItem()
        )
    }

    override suspend fun removeSheetItem(sheetId: String, musicId: String): ApiResult<Unit> {
        val id = sheetId.toIntOrNull() ?: return ApiResult.Failure(ApiError.requestError)
        dao.removeSheetMusicItem(id, musicId)
        return ApiResult.Success(Unit)
    }

    override suspend fun deleteSheet(sheetId: String): ApiResult<Unit> {
        dao.deleteSheet(sheetId)
        return ApiResult.Success(Unit)
    }

    override suspend fun selectUserSheet(): ApiResult<List<MediaItem>> {
        return ApiResult.Success(
            mediaConnect.getChildren(Constants.LOCAL_SHEET_ID)
        )
    }
}