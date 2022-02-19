import Version.coil_version
import Version.compose_md3_version
import Version.compose_version
import Version.hilt_compose_version
import Version.hilt_version
import Version.lifecycle_version
import Version.media2_version
import Version.media3_version
import Version.mmkv_version
import Version.retrofit_version
import Version.room_version

/**
 * @author : huaweikai
 * @Date   : 2022/02/18
 * @Desc   :
 */

object Google {
    const val material = "com.google.android.material:material:1.4.0"

    const val hilt_android = "com.google.dagger:hilt-android:$hilt_version"
    const val hilt_compiler = "com.google.dagger:hilt-compiler:$hilt_version"
    const val hilt_viewModel = "androidx.hilt:hilt-lifecycle-viewmodel:1.0.0-alpha03"

//        const val exoplayer_core = "com.google.android.exoplayer:exoplayer-core:$exoplayer_version"
//        const val exoplayer_ui = "com.google.android.exoplayer:exoplayer-ui:${exoplayer_version}"
//        const val exoplayer_media2 = "com.google.android.exoplayer:extension-media2:$exoplayer_version"


}

object Compose {
    const val compose_ui = "androidx.compose.ui:ui:${compose_version}"
    const val compose_material = "androidx.compose.material:material:$compose_version"
    const val compose_ui_tool_preview = "androidx.compose.ui:ui-tooling-preview:$compose_version"
    const val compose_activity =
        "androidx.activity:activity-compose:${Version.activity_compose_version}"
    const val compose_material_md3 = "androidx.compose.material3:material3:$compose_md3_version"
    const val compose_ui_util = "androidx.compose.ui:ui-util:$compose_version"
    const val compose_navigation = "androidx.navigation:navigation-compose:2.4.0-rc01"

    const val compose_hilt_navigation =
        "androidx.hilt:hilt-navigation-compose:$hilt_compose_version"
    const val compose_systemuiController =
        "com.google.accompanist:accompanist-systemuicontroller:0.22.0-rc"
    const val compose_pager = "com.google.accompanist:accompanist-pager:0.20.3"
    const val compose_pager_indicator = "com.google.accompanist:accompanist-pager-indicators:0.20.3"
    const val compose_insets = "com.google.accompanist:accompanist-insets:0.22.0-rc"
    const val compose_swipeRefresh = "com.google.accompanist:accompanist-swiperefresh:0.24.2-alpha"
    const val compose_constraintlayout = "androidx.constraintlayout:constraintlayout-compose:1.0.0"

}

object Other {
    const val mmkv = "com.tencent:mmkv:$mmkv_version"
    const val coil = "io.coil-kt:coil-compose:$coil_version"

    const val retrofit2 = "com.squareup.retrofit2:retrofit:$retrofit_version"
    const val retrofit2_gson = "com.squareup.retrofit2:converter-gson:$retrofit_version"

    const val huawei_obs = "com.huaweicloud:esdk-obs-android:3.21.12"
    const val taglib = ":taglib"

}

object Test {
    const val junit = "junit:junit:4.+"
    const val test_ext_junit = "androidx.test.ext:junit:1.1.3"
    const val test_espresso = "androidx.test.espresso:espresso-core:3.4.0"
    const val compose_ui_test_junit4 = "androidx.compose.ui:ui-test-junit4:$compose_version"
    const val compose_ui_tooling = "androidx.compose.ui:ui-tooling:$compose_version"
}

object AndroidX {
    const val core = "androidx.core:core-ktx:1.7.0"
    const val appcompat = "androidx.appcompat:appcompat:1.4.0"
    const val activity = "androidx.activity:activity-ktx:1.4.0"
    const val activity_ktx = "androidx.activity:activity:1.4.0"

    const val media2_session = "androidx.media2:media2-session:$media2_version"
    const val media2_widget = "androidx.media2:media2-widget:$media2_version"
    const val media2_player = "androidx.media2:media2-player:$media2_version"

    const val media3_session = "androidx.media3:media3-exoplayer:$media3_version"
    const val media3_exoplayer = "androidx.media3:media3-session:$media3_version"
    const val media3_ui = "androidx.media3:media3-ui:$media3_version"
    const val media3_common = "androidx.media3:media3-common:$media3_version"
    const val media_okhttp_datasource = "androidx.media3:media3-datasource-okhttp:$media3_version"


    const val room_runtime = "androidx.room:room-runtime:$room_version"
    const val room_compiler = "androidx.room:room-compiler:${room_version}"
    const val room_ktx = "androidx.room:room-ktx:$room_version"

    const val lifecycle_viewModel_compose =
        "androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycle_version"
    const val lifecycle_viewModel_ktx =
        "androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version"
    const val lifecycle_runtime_ktx = "androidx.lifecycle:lifecycle-runtime-ktx:$lifecycle_version"

    const val palette = "androidx.palette:palette-ktx:1.0.0"

    const val window = "androidx.window:window:1.0.0"
}