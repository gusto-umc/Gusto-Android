import org.gradle.api.JavaVersion
import org.gradle.internal.impldep.bsh.commands.dir
import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.gst.gusto"
    compileSdk = 34

    val localProperties = Properties()
    localProperties.load(project.rootProject.file("local.properties").inputStream())

    defaultConfig {
        applicationId = "com.gst.gusto"
        minSdk = 27
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "API_BASE", localProperties.getProperty("api_base"))
        buildConfigField("String", "KAKAO_NATIVE_APP_KEY", localProperties.getProperty("KAKAO_NATIVE_APP_KEY"))
        buildConfigField("String", "GOOGLE_CLINET_ID", localProperties.getProperty("GOOGLE_CLINET_ID"))
        buildConfigField("String", "NAVER_CLIENT_ID", localProperties.getProperty("NAVER_CLIENT_ID"))
        buildConfigField("String", "NAVER_CLIENT_SECRET", localProperties.getProperty("NAVER_CLIENT_SECRET"))
        manifestPlaceholders["KAKAO_NATIVE_APP_KEY"] = properties["KAKAO_NATIVE_APP_KEY"] as? String ?: ""

    }

    buildTypes {
        release {
            isMinifyEnabled = false
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

    buildFeatures {
        buildConfig = true
        viewBinding = true
        dataBinding = true
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.7.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.6")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.6")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // Bottom Navigation
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.6")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.6")


    // 위치 권한 의존성 추가
    implementation("com.google.android.gms:play-services-location:18.0.0")

    // Glide library
    implementation("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")

    // Retrofit 라이브러리
    implementation ("com.squareup.retrofit2:retrofit:2.6.4")
    implementation ("com.squareup.retrofit2:converter-gson:2.6.4")
    implementation ("com.squareup.retrofit2:converter-scalars:2.6.4")
    implementation ("com.squareup.okhttp3:okhttp:4.9.2")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.2")

    // Extended Floating Button
    implementation("com.google.android.material:material:1.4.0-alpha02")

    // Ted Permission - RxJava3
    implementation("io.github.ParkSangGwon:tedpermission-rx3:3.3.0")

    // kakao
    implementation("com.kakao.maps.open:android:2.6.0")

// Java language implementation
    implementation("androidx.activity:activity:1.6.0")
    // Kotlin
    implementation("androidx.activity:activity-ktx:1.6.0")

    //네이버 로그인
    implementation ("com.navercorp.nid:oauth-jdk8:5.1.0") // jdk 8
    // 카카오 로그인
    implementation ("com.kakao.sdk:v2-all:2.17.0")
    implementation ("com.kakao.sdk:v2-user:2.12.1")
    // 구글 로그인
    implementation ("com.google.gms:google-services:4.3.15")
    implementation ("com.google.firebase:firebase-auth:22.0.0")
    implementation ("com.google.firebase:firebase-bom:32.0.0")
    implementation ("com.google.android.gms:play-services-auth:20.5.0")

    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.9")
}
