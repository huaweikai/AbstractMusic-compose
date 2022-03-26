package com.hua.abstractmusic.ui.home.net.detail

/**
 * @author : huaweikai
 * @Date   : 2022/02/14
 * @Desc   :
 */
//@ExperimentalFoundationApi
//@SuppressLint("UnsafeOptInUsageError")
//@Composable
//fun NetDetail(
//    type: String,
//    netViewModel: NetViewModel,
//    navHostController: NavHostController = LocalAppNavController.current
//) {
//    when (type) {
//        ALL_MUSIC_TYPE -> {
//            MusicLazyItems(
//                list = netViewModel.musicList.value
//            ) {
//                netViewModel.setPlaylist(it, netViewModel.musicList.value)
//            }
//        }
//        NET_ALBUM_TYPE -> {
//            AlbumLazyGrid(
//                list = netViewModel.albumList.value
//            ) {
//               navHostController.navigate("${Screen.AlbumDetailScreen.route}?albumId=$it")
//            }
//        }
//    }
//}
