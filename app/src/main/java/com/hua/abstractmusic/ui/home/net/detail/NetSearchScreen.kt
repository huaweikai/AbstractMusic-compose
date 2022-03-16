package com.hua.abstractmusic.ui.home.net.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

/**
 * @author : huaweikai
 * @Date   : 2022/02/26
 * @Desc   :
 */
@Composable
fun NetSearchScreen(
    searchViewModel: SearchViewModel = hiltViewModel()
) {
    Column(
        modifier = Modifier.statusBarsPadding()
    ) {
        val (searchText,setSearchText ) = remember {
            mutableStateOf("")
        }
        BasicTextField(value = searchText, onValueChange = setSearchText)
    }
}