plugins {
    id("com.android.library")
    id("kotlinx-serialization")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
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
        debug {
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
    implementation("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.6")
    implementation(Other.retrofit2)
    implementation(Other.retrofit2_gson)
    implementation(Other.okhttp)
    implementation(Google.hilt_android)
    kapt(Google.hilt_compiler)
    implementation("androidx.annotation:annotation:1.3.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
    api(project(Module.model))
//    implementation(project(":model"))
}