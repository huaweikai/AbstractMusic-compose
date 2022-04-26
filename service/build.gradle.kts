plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id ("dagger.hilt.android.plugin")
}

android {
    compileSdk = 31

    defaultConfig {
        minSdk = 23
        targetSdk = 31

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    api(AndroidX.media3_common)
    api(AndroidX.media3_exoplayer)
    api(AndroidX.media3_session)
    implementation(AndroidX.media3_ui)
    implementation(AndroidX.media1)

    implementation(Other.coil)
    implementation(Google.hilt_android)
    kapt(Google.hilt_compiler)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.6.0")
    implementation(Other.mmkv)

    implementation(AndroidX.room_runtime)
    kapt(AndroidX.room_compiler)
    implementation(AndroidX.room_ktx)
    implementation(Other.hutool)
    implementation(Other.tinyPinyin)
    implementation(Other.tinyAndroid)
    implementation(AndroidX.core)

    implementation(project(Module.model))
}