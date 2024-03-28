import com.android.build.api.dsl.ApplicationDefaultConfig
import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("dagger.hilt.android.plugin")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.example.smsretriverstudy"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.smsretriverstudy"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        loadEnvKey()
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
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

fun ApplicationDefaultConfig.loadEnvKey() {
    val key = "BASE_URL"
    val value = gradleLocalProperties(rootDir, providers).getProperty(key)
    buildConfigField("String", key, requireNotNull(value))
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.appcompat)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)


    // 컴포즈 뷰모델
    implementation (libs.androidx.lifecycle.viewmodel.compose)

    // 전화번호 포맷
    implementation (libs.libphonenumber)

    // Hilt
    implementation (libs.hilt.android)
    ksp (libs.hilt.compiler)

    // Hilt Compose
    implementation (libs.androidx.hilt.navigation.compose)
    implementation (libs.kotlin.reflect)

    // Retrofit (proguard rules 추가)
    implementation (libs.retrofit)
    implementation (libs.converter.moshi)

    // Moshi (proguard rules 추가 (DTO Enum))
    implementation (libs.moshi.kotlin)
    ksp (libs.moshi.kotlin.codegen)

    // Okhttp
    implementation (libs.okhttp)
    implementation (libs.logging.interceptor)


    // API 24 이상에서만 지원하는 Java8 라이브러리 대체
    coreLibraryDesugaring (libs.desugar.jdk.libs)
}