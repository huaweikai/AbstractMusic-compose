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
    const val material = "com.google.android.material:material:1.5.0"

    const val hilt_android = "com.google.dagger:hilt-android:$hilt_version"
    const val hilt_compiler = "com.google.dagger:hilt-compiler:$hilt_version"
    const val hilt_viewModel = "androidx.hilt:hilt-lifecycle-viewmodel:1.0.0-alpha03"

    const val gson = "com.google.code.gson:gson:2.8.5"

//        const val exoplayer_core = "com.google.android.exoplayer:exoplayer-core:$exoplayer_version"
//        const val exoplayer_ui = "com.google.android.exoplayer:exoplayer-ui:${exoplayer_version}"
//        const val exoplayer_media2 = "com.google.android.exoplayer:extension-media2:$exoplayer_version"


}

object Compose {
    const val compose_ui = "androidx.compose.ui:ui:${compose_version}"
    const val compose_material = "androidx.compose.material:material:1.2.0-alpha01"
    const val compose_ui_tool_preview = "androidx.compose.ui:ui-tooling-preview:$compose_version"
    const val compose_activity =
        "androidx.activity:activity-compose:${Version.activity_compose_version}"
    const val compose_material_md3 = "androidx.compose.material3:material3:$compose_md3_version"
    const val compose_ui_util = "androidx.compose.ui:ui-util:$compose_version"
    const val compose_navigation = "androidx.navigation:navigation-compose:2.4.0"

    const val compose_hilt_navigation =
        "androidx.hilt:hilt-navigation-compose:$hilt_compose_version"

    const val compose_systemuiController =
        "com.google.accompanist:accompanist-systemuicontroller:${Version.compose_accompanist}"
    const val compose_pager = "com.google.accompanist:accompanist-pager:0.20.3"
    const val compose_pager_indicator = "com.google.accompanist:accompanist-pager-indicators:0.20.3"
    const val compose_insets =
        "com.google.accompanist:accompanist-insets:${Version.compose_accompanist}"
    const val compose_flow_layout = "com.google.accompanist:accompanist-flowlayout:${Version.compose_accompanist}"
    const val compose_swipeRefresh =
        "com.google.accompanist:accompanist-swiperefresh:${Version.compose_accompanist}"
    const val compose_constraintlayout = "androidx.constraintlayout:constraintlayout-compose:1.0.0"

    //navigation-material
    const val compose_navigation_material =
        "com.google.accompanist:accompanist-navigation-material:${Version.compose_accompanist}"
    const val compose_navigation_animate =
        "com.google.accompanist:accompanist-navigation-animation:${Version.compose_accompanist}"


    //lottie
    const val compose_lottie = "com.airbnb.android:lottie-compose:5.0.2"

    const val compose_scroll_bar = "me.onebone:toolbar-compose:2.3.2"

    const val lifecycle_viewModel_compose =
        "androidx.lifecycle:lifecycle-viewmodel-compose:1.0.0-alpha07"

    const val compose_glance = "androidx.glance:glance-appwidget:1.0.0-alpha03"

}

object Other {
    const val mmkv = "com.tencent:mmkv:$mmkv_version"
    const val coil = "io.coil-kt:coil-compose:$coil_version"

    const val retrofit2 = "com.squareup.retrofit2:retrofit:$retrofit_version"
    const val retrofit2_gson = "com.squareup.retrofit2:converter-gson:$retrofit_version"
    const val okhttp = "com.squareup.okhttp3:okhttp:5.0.0-alpha.6"

    const val huawei_obs = "com.huaweicloud:esdk-obs-android:3.21.12"
    const val uCrop = "com.github.yalantis:ucrop:2.2.8"
    const val status_bar_color = "cn.chitanda:dynamicstatusbar:2.5.1"

    const val monet = "com.github.KieronQuinn:MonetCompat:0.4.1"

    const val tinyPinyin = "com.github.promeg:tinypinyin:2.0.3"
    const val tinyAndroid = "com.github.promeg:tinypinyin-lexicons-android-cncity:2.0.3"
    const val hutool = "cn.hutool:hutool-extra:5.6.0"

}

object Module{
    const val taglib = ":taglib"
    const val blur = ":blur"
    const val model = ":model"
    const val network = ":network"
    const val service = ":service"
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

    const val media1 = "androidx.media:media:1.5.0"

    const val media3_session = "androidx.media3:media3-exoplayer:$media3_version"
    const val media3_exoplayer = "androidx.media3:media3-session:$media3_version"
    const val media3_ui = "androidx.media3:media3-ui:$media3_version"
    const val media3_common = "androidx.media3:media3-common:$media3_version"
    const val media_okhttp_datasource = "androidx.media3:media3-datasource-okhttp:$media3_version"


    const val room_runtime = "androidx.room:room-runtime:$room_version"
    const val room_compiler = "androidx.room:room-compiler:${room_version}"
    const val room_ktx = "androidx.room:room-ktx:$room_version"
    const val lifecycle_viewModel_ktx =
        "androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version"
    const val lifecycle_runtime_ktx = "androidx.lifecycle:lifecycle-runtime-ktx:$lifecycle_version"

    const val palette = "androidx.palette:palette-ktx:1.0.0"

    const val window = "androidx.window:window:1.0.0"
}