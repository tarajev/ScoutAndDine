plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("com.google.gms.google-services")
    id("kotlin-kapt")
    id ("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}


android {
    namespace = "com.example.scoutanddine"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.scoutanddine"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        compose = true
        viewBinding = true //kreira binding klase za layout-e
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

dependencies {

    implementation(platform("com.google.firebase:firebase-bom:33.1.2"))
    implementation ("com.google.firebase:firebase-analytics-ktx")
    implementation ("com.google.firebase:firebase-auth-ktx")
    implementation ("com.google.firebase:firebase-firestore-ktx")
    implementation ("com.google.firebase:firebase-storage-ktx")
    implementation ("com.google.firebase:firebase-messaging-ktx")
    implementation ("com.google.firebase:firebase-database-ktx")
    implementation("androidx.room:room-ktx:2.6.1")
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.play.services.location)
    implementation(libs.play.services.maps)
    kapt("androidx.room:room-compiler:2.6.1")
    androidTestImplementation("androidx.room:roomtesting:2.6.1")
    implementation("androidx.compose.material3:material3:1.0.0-alpha01")
    implementation("androidx.compose.ui:ui:1.0.0")
    implementation("androidx.compose.ui:ui-tooling:1.0.0")
    implementation("androidx.compose.foundation:foundation:1.0.0")
    implementation("androidx.navigation:navigation-compose:2.4.0-alpha10")
    implementation("androidx.compose.material:material-icons-extended:1.0.0")

    implementation("com.google.maps.android:maps-compose:4.4.1")
    implementation ("com.google.android.gms:play-services-maps:18.2.0")
    implementation ("com.google.android.gms:play-services-location:21.0.1")

    implementation(platform("com.google.firebase:firebase-bom:33.1.2"))
    implementation("com.google.firebase:firebase-firestore")
    implementation ("io.coil-kt:coil-compose:2.0.0")


    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.firebase.auth)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.room.common)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

}