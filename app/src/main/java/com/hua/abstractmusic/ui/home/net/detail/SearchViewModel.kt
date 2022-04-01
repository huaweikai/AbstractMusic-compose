package com.hua.abstractmusic.ui.home.net.detail

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.focus.FocusState
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import com.hua.abstractmusic.base.viewmodel.BaseViewModel
import com.hua.abstractmusic.bean.net.NetData
import com.hua.abstractmusic.bean.user.SearchHistory
import com.hua.abstractmusic.db.user.UserDao
import com.hua.abstractmusic.other.NetWork.ERROR
import com.hua.abstractmusic.other.NetWork.SERVER_ERROR
import com.hua.abstractmusic.repository.NetRepository
import com.hua.abstractmusic.services.MediaConnect
import com.hua.abstractmusic.services.MediaItemTree
import com.hua.abstractmusic.ui.utils.LCE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @author : huaweikai
 * @Date   : 2022/03/16
 * @Desc   :
 */
@HiltViewModel
class SearchViewModel @Inject constructor(
    mediaConnect: MediaConnect,
    private val netRepository: NetRepository,
    private val userDao: UserDao,
) : BaseViewModel(mediaConnect) {
    private val _searchText = mutableStateOf(ChangeText(hint = "搜你想要的"))
    val searchText: State<ChangeText> = _searchText

    val searchMaps = HashMap<SearchObject, MutableState<NetData<List<MediaItem>>>>()

    val nullNetData = NetData<List<MediaItem>>(SERVER_ERROR, emptyList(), "")
    val searchHistory = userDao.selectHistory()

    val searchMusic = mutableStateOf(nullNetData)
    val searchAlbum = mutableStateOf(nullNetData)
    val searchArtist = mutableStateOf(nullNetData)
    val searchSheet = mutableStateOf(nullNetData)

    val detailVis = mutableStateOf(false)

    val searchState = mutableStateOf<LCE>(LCE.Loading)

    fun addEvent(event: TextEvent) {
        when (event) {
            is TextEvent.TextValueChange -> {
                _searchText.value = searchText.value.copy(
                    text = event.value,
                    isHintVisible = event.value.isBlank()
                )
                if (event.value.isBlank()) {
                    clear()
                }
            }
            is TextEvent.TextFocusChange -> {
                _searchText.value = searchText.value.copy(
                    isHintVisible = !event.focus.isFocused &&
                            searchText.value.text.isBlank()
                )
            }
        }
    }

    fun search() {
        searchState.value = LCE.Loading
        detailVis.value = true
        val search = searchText.value.text
        searchMaps[SearchObject.Music(search)] = searchMusic
        searchMaps[SearchObject.Album(search)] = searchAlbum
        searchMaps[SearchObject.Artist(search)] = searchArtist
        searchMaps[SearchObject.Sheet(search)] = searchSheet
        viewModelScope.launch {
            searchMaps.keys.forEach {
                searchMaps[it]!!.value = netRepository.search(it)
            }
            searchState.value = if (searchMusic.value.code != ERROR) LCE.Success else LCE.Error
            val c = userDao.selectHistoryList().find { it.history == search }
            if(c != null){
                userDao.deleteHistory(c.id)
            }
            userDao.insertHistory(SearchHistory(history = search))
        }
    }
    fun clear(){
        detailVis.value = false
        searchMaps.forEach{
            it.value.value = nullNetData
        }
        searchMaps.clear()
        _searchText.value = searchText.value.copy(text = "")
    }
}

data class ChangeText(
    val text: String = "",
    val hint: String = "",
    val isHintVisible: Boolean = true
)

sealed class TextEvent {
    data class TextValueChange(val value: String) : TextEvent()
    data class TextFocusChange(val focus: FocusState) : TextEvent()
}