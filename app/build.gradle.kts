val versionMajor = 1
val versionMinor = 2
val versionPatch = 0
val versionBuild = 0

plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.monseigneur.mediacounterapp"
    compileSdkVersion(35)
    defaultConfig {
        applicationId = "com.monseigneur.MediaCounterApp"
        minSdk = 35
        targetSdk = 35
        versionCode = versionMajor * 10000 + versionMinor * 1000 + versionPatch * 100 + versionBuild
        versionName = "${versionMajor}.${versionMinor}.${versionPatch}"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    testOptions {
        unitTests.all {
            it.useJUnitPlatform()
        }
    }
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(24)
    }
}

// Found in https://stackoverflow.com/questions/75274720/a-failure-occurred-while-executing-appcheckdebugduplicateclasses/75315276#75315276 after
// upgrading to new gradle and SDK.
configurations.implementation {
    exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib-jdk8")
}

dependencies {
    implementation(libs.ion.java)
    implementation(libs.androidx.recyclerview)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.androidx.appcompat)
    testImplementation(libs.junit.jupiter)

    implementation(libs.material)
}
