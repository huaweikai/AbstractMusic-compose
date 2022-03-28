package com.hua.abstractmusic.ui.route

/**
 * @author : huaweikai
 * @Date   : 2022/01/07
 * @Desc   : 路由，这个比较好记
 */
sealed class Screen(val route: String) {
    object HelloScreen : Screen("hello_screen")
    object Splash : Screen("splash_screen")
    object HomeScreen : Screen("home_screen")

    object NetScreen : Screen("home_net_screen")
    object LocalScreen : Screen("home_local_screen")
    object MineScreen : Screen("home_mine_screen")

    object AlbumDetailScreen : Screen("album_screen")
    object ArtistDetailScreen : Screen("artist_screen")

    object SettingScreen : Screen("setting_screen")

    object RegisterScreen : Screen("register_screen")
    object LoginScreen : Screen("login_screen")

    object SheetDetailScreen : Screen("sheet_detail_screen")
    object NetSearchScreen : Screen("net_search_screen")


    object PlayScreen : Screen("play_screen")

    object PlayListScreen : Screen("playlist_screen")

    object OtherDetail:Screen("other_screen")
}
