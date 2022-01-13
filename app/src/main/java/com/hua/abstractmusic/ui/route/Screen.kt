package com.hua.abstractmusic.ui.route

/**
 * @author : huaweikai
 * @Date   : 2022/01/07
 * @Desc   : 路由，这个比较好记
 */
sealed class Screen(val route:String){
    object HelloScreen:Screen("hello_screen")
    object HomeScreen:Screen("home_screen")

    object NetScreen:Screen("home_net_screen")
    object LocalScreen:Screen("home_local_screen")
    object MineScreen:Screen("home_mine_screen")

    object LocalAlbumDetail:Screen("local_album_screen")
}
