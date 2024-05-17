import java.text.SimpleDateFormat
import java.util.Date
import com.android.build.gradle.internal.api.ApkVariantOutputImpl
import org.jetbrains.kotlin.storage.CacheResetOnProcessCanceled.enabled
import java.util.Properties
import java.io.FileInputStream


@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    id("androidx.navigation.safeargs.kotlin")
//    alias(libs.plugins.safeargsKotlin)
    id("kotlin-parcelize")
    id("kotlin-kapt")
}

val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties()
keystoreProperties.load(FileInputStream(keystorePropertiesFile))
android {
    namespace = "com.jtsportech.visport.android"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.jtsportech.visport.android"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = libs.versions.versionCode.get().toInt()
        versionName = libs.versions.versionName.get()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        ndk {
            abiFilters.addAll(arrayListOf("arm64-v8a", "armeabi-v7a"))
        }

        // 配置 aab 名称
        setProperty(
            "archivesBaseName",
            "jtsportech_v${defaultConfig.versionName}_${SimpleDateFormat("yyyyMMdd").format(Date())}"
        )
    }

    signingConfigs {
        create("release") {
            keyAlias = keystoreProperties["keyAlias"] as String
            keyPassword = keystoreProperties["keyPassword"] as String
            storeFile = file(keystoreProperties["storeFile"] as String)
            storePassword = keystoreProperties["storePassword"] as String
        }
    }

    buildTypes {
        release {
            //混淆设置（代码缩减）
            isMinifyEnabled = true
            //资源缩减 （资源缩减只有在与代码缩减配合使用时才能发挥作用）
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            //混淆设置（代码缩减）
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
        }
    }

    // 输出类型
    android.applicationVariants.all {
        // 编译类型
        val buildType = this.buildType.name
        outputs.all {
            // 判断是否是输出 apk 类型
            if (this is ApkVariantOutputImpl) {
                this.outputFileName =
                    "jtsportech_v${defaultConfig.versionName}_${
                        SimpleDateFormat("yyyyMMdd").format(
                            Date()
                        )
                    }_$buildType.apk"
            }
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
        viewBinding = true
        //noinspection DataBindingWithoutKapt
        dataBinding = true
        // APG 8.0 默认情况下是禁用
        buildConfig = true
    }

    lint {
        baseline = file("lint-baseline.xml")
        checkReleaseBuilds = false
        abortOnError = false
    }
//    dataBinding {
//        enabled = true
//    }
}

dependencies {
    implementation(fileTree(mapOf("include" to listOf("*.jar", "*.aar"), "dir" to "libs")))
    api(project(":lib_common"))
    implementation(project(":activityResultLauncherLibrary"))
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.legacy.support.v4)
    implementation(libs.jetpackLivedata)
    implementation(libs.jetpackViewModel)
    implementation(libs.jetpackNavigation)
    implementation(libs.jetpackNavigationUi)
    implementation(libs.startup)
    implementation(libs.banner)
    implementation(libs.zxing)
//    implementation(libs.activityResultLauncher)
    implementation(libs.shadowlayout)
    implementation(libs.mpAndroidChart)
    implementation(libs.gsyVideoPlayer)
    implementation(libs.weChat)
    implementation(libs.umsdkCommon)
    implementation(libs.umsdkAsms)
    implementation(libs.umsdkUverify)
    implementation(libs.umsdkUverifyMain)
    implementation(libs.umsdkUverifyLogger)
    implementation(libs.buglyCrashreport)
    implementation(libs.androidx.activity)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(kotlin("reflect"))
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation(libs.editImage)

}