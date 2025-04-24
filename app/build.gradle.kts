import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.duzceders.aicaltracker"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.duzceders.aicaltracker"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Cloudinary API anahtarlarını tanımla
        buildConfigField("String", "CLOUDINARY_CLOUD_NAME", getLocalProperty("cloudinary.cloud_name"))
        buildConfigField("String", "CLOUDINARY_API_KEY", getLocalProperty("cloudinary.api_key"))
        buildConfigField("String", "CLOUDINARY_API_SECRET", getLocalProperty("cloudinary.api_secret"))
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

// local.properties'den değerleri oku
fun getLocalProperty(key: String): String {
    val properties = Properties()
    val localProperties = File(rootDir, "local.properties")
    if (localProperties.isFile) {
        InputStreamReader(FileInputStream(localProperties), Charsets.UTF_8).use { reader ->
            properties.load(reader)
        }
    }
    return if (properties.containsKey(key)) "\"${properties.getProperty(key)}\"" else "\"\""
}

dependencies {
    implementation(libs.firebase.storage)
    implementation(libs.firebase.auth)
    implementation(libs.activity)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.legacy.support.v4)
    compileOnly (libs.lombok)
    annotationProcessor (libs.lombok)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(platform(libs.firebase.bom))

    implementation(libs.firebase.firestore)
    implementation(libs.google.firebase.storage)

 
    // Retrofit ve Cloudinary bağımlılıkları
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.gson)
    implementation(libs.okhttp.logging)
    implementation(libs.cloudinary)

    // Navigation and Drawer
    implementation (libs.navigation.fragment.v277)
    implementation (libs.navigation.ui.v277)
    implementation (libs.material)
}
