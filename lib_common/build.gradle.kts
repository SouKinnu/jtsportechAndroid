@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinAndroid)
//    alias(libs.plugins.kspApplication)
    // 临时增加 kapt ，后期有空可以替换成 ksp
    kotlin("kapt")
    id("kotlin-parcelize")

}

android {
    namespace = "com.cloudhearing.android.lib_common"
    compileSdk = libs.versions.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
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
    buildFeatures {
        // APG 8.0 默认情况下是禁用
        buildConfig = true
    }
}

dependencies {

    api(project(":lib_base"))
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(platform(libs.okhttpBom))
    implementation(libs.okhttp)
    implementation(libs.retrofit)
    implementation(libs.gsonConverter)
    api(libs.gsyVideoPlayer)
    api(libs.glide)
    api(libs.camera.core)
    api(libs.camera.camera2)
    api(libs.camera.lifecycle)
    api(libs.camera.view)
    api(libs.barcode.scanning)
    kapt(libs.glideCompiler)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
}