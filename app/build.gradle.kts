import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/***************************************************************************************************
 * Copyright © All Contributors. See LICENSE and AUTHORS in the root directory for details.
 **************************************************************************************************/

plugins {
    alias(libs.plugins.mikepenz.aboutLibraries)
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
}

// Android configuration
android {
    compileSdk = 36

    defaultConfig {
        applicationId = "com.cozyla.davx"

        versionCode = releaseTime().toInt()
        versionName = "1.0.0." + releaseTime()

        buildConfigField("long", "buildTime", "${System.currentTimeMillis()}L")

        setProperty("archivesBaseName", "davx5-$versionName")

        minSdk = 24        // Android 7.0
        targetSdk = 36     // Android 15

        buildConfigField("String", "userAgent", "\"DAVx5\"")

        testInstrumentationRunner = "at.bitfire.davdroid.CustomTestRunner"
    }

    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(17)
        }
    }

    compileOptions {
        // enable because ical4android requires desugaring
        isCoreLibraryDesugaringEnabled = true
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }

    // Java namespace for our classes (not to be confused with Android package ID)
    namespace = "at.bitfire.davdroid"

    flavorDimensions += "distribution"
    productFlavors {
        create("ose") {
            dimension = "distribution"
        }
    }

    sourceSets {
        getByName("androidTest") {
            assets.srcDir("$projectDir/schemas")
        }
    }

    signingConfigs {
        create("bitfire") {
            storeFile = file(properties["STORE_FILE"] ?: "/dev/null")
            storePassword = properties["STORE_PASSWORD"].toString()
            keyAlias = properties["KEY_ALIAS"].toString()
            keyPassword = properties["KEY_PASSWORD"].toString()
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules-release.pro")

            isShrinkResources = true

            signingConfig = signingConfigs.findByName("bitfire")
        }
    }

    lint {
        disable += arrayOf("GoogleAppIndexingWarning", "ImpliedQuantity", "MissingQuantity", "MissingTranslation", "ExtraTranslation", "RtlEnabled", "RtlHardcoded", "Typos", "NullSafeMutableLiveData")
    }

    packaging {
        resources {
            excludes += arrayOf("META-INF/*.md")
        }
    }

    androidResources {
        generateLocaleConfig = true
    }

    @Suppress("UnstableApiUsage")
    testOptions {
        managedDevices {
            localDevices {
                create("virtual") {
                    device = "Pixel 3"
                    apiLevel = 34
                    systemImageSource = "aosp-atd"
                }
            }
        }
    }
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

configurations {
    configureEach {
        // exclude modules which are in conflict with system libraries
        exclude(module="commons-logging")
        exclude(group="org.json", module="json")

        // Groovy requires SDK 26+, and it's not required, so exclude it
        exclude(group="org.codehaus.groovy")
    }
}

dependencies {
    // core
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlinx.coroutines)
    coreLibraryDesugaring(libs.android.desugaring)

    // Hilt
    implementation(libs.hilt.android.base)
    ksp(libs.androidx.hilt.compiler)
    ksp(libs.hilt.android.compiler)

    // support libs
    implementation(libs.androidx.activityCompose)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.browser)
    implementation(libs.androidx.core)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.hilt.work)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.base)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.paging)
    implementation(libs.androidx.paging.compose)
    implementation(libs.androidx.preference)
    implementation(libs.androidx.security)
    implementation(libs.androidx.work.base)

    // Jetpack Compose
    implementation(libs.compose.accompanist.permissions)
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.material3)
    implementation(libs.compose.materialIconsExtended)
    implementation(libs.compose.runtime.livedata)
    debugImplementation(libs.compose.ui.tooling)
    implementation(libs.compose.ui.toolingPreview)

    // Glance Widgets
    implementation(libs.glance.base)
    implementation(libs.glance.material)

    // Jetpack Room
    implementation(libs.room.runtime)
    implementation(libs.room.base)
    implementation(libs.room.paging)
    ksp(libs.room.compiler)

    // own libraries
    implementation(libs.bitfire.cert4android)
    implementation(libs.bitfire.dav4jvm) {
        exclude(group="junit")
    }
    implementation(libs.bitfire.ical4android)
    implementation(libs.bitfire.vcard4android)

    // third-party libs
    @Suppress("RedundantSuppression")
    implementation(libs.dnsjava)
    implementation(libs.guava)
    implementation(libs.mikepenz.aboutLibraries)
    implementation(libs.nsk90.kstatemachine)
    implementation(libs.okhttp.base)
    implementation(libs.okhttp.brotli)
    implementation(libs.okhttp.logging)
    implementation(libs.openid.appauth)
    implementation(libs.unifiedpush)




}

// 定义获取当前日期和时间并格式化的方法
fun releaseTime(): String {
    val current = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("yyyyMMddHH")
    return current.format(formatter)
}