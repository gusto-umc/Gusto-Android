import org.gradle.api.JavaVersion
import org.gradle.internal.impldep.bsh.commands.dir
import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt") // 추가
    id("com.google.gms.google-services")
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
        versionCode = 5
        versionName = "Gusto:0.4"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "API_BASE", localProperties.getProperty("api_base"))
        buildConfigField("String", "KAKAO_NATIVE_APP_KEY", localProperties.getProperty("KAKAO_NATIVE_APP_KEY"))
        buildConfigField("String", "GOOGLE_CLINET_ID", localProperties.getProperty("GOOGLE_CLINET_ID"))
        buildConfigField("String", "GOOGLE_SECRET", localProperties.getProperty("GOOGLE_SECRET"))
        buildConfigField("String", "GOOGLE_REDIRECT", localProperties.getProperty("GOOGLE_REDIRECT"))
        buildConfigField("String", "NAVER_CLIENT_ID", localProperties.getProperty("NAVER_CLIENT_ID"))
        buildConfigField("String", "NAVER_CLIENT_SECRET", localProperties.getProperty("NAVER_CLIENT_SECRET"))
        buildConfigField("String", "SGIS_CONSUMER_KEY", localProperties.getProperty("SGIS_CONSUMER_KEY"))
        buildConfigField("String", "SGIS_CONSUMER_SECRET", localProperties.getProperty("SGIS_CONSUMER_SECRET"))
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
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")
    implementation(project(":nativetemplates"))
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")

    // Bottom Navigation
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")


    // 위치 권한 의존성 추가
    implementation("com.google.android.gms:play-services-location:21.3.0")

    // Glide library
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

    // Retrofit 라이브러리
    implementation ("com.squareup.retrofit2:retrofit:2.11.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation ("com.squareup.retrofit2:converter-scalars:2.11.0")
    implementation ("com.squareup.okhttp3:okhttp:4.12.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Extended Floating Button
    implementation("com.google.android.material:material:1.12.0")

    // Ted Permission - RxJava3
    implementation("io.github.ParkSangGwon:tedpermission-rx3:3.4.2")

    // kakao
    implementation("com.kakao.maps.open:android:2.6.0")

// Java language implementation
    implementation("androidx.activity:activity:1.9.1")
    // Kotlin
    implementation("androidx.activity:activity-ktx:1.9.1")

    //네이버 로그인
    implementation ("com.navercorp.nid:oauth-jdk8:5.10.0") // jdk 8
    // 카카오 로그인
    implementation ("com.kakao.sdk:v2-all:2.17.0")
    implementation ("com.kakao.sdk:v2-user:2.12.1")
    // 구글 로그인
    implementation ("com.google.gms:google-services:4.4.2")
    implementation ("com.google.firebase:firebase-auth:23.0.0")
    implementation ("com.google.firebase:firebase-bom:33.2.0")
    implementation ("com.google.android.gms:play-services-auth:21.2.0")

    implementation(platform("com.google.firebase:firebase-bom:33.2.0"))
    implementation("com.google.firebase:firebase-analytics")

    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

    //flexbox - hashtag rv
    implementation("com.google.android.flexbox:flexbox:3.0.0")

    // Admob
    implementation("com.google.android.gms:play-services-ads:23.3.0")

    // android version updare
    implementation("com.google.android.play:app-update-ktx:2.1.0")
}
