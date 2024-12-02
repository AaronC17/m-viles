plugins {
    alias(libs.plugins.android.application) // Alias para el plugin Android Application
    alias(libs.plugins.kotlin.android) // Alias para el plugin Kotlin Android
    id("com.google.gms.google-services") // Plugin para Google Services (Firebase)
}

android {
    namespace = "com.example.moviles_proyecto"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.moviles_proyecto"
        minSdk = 23
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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

    // Habilitar ViewBinding
    viewBinding {
        enable = true
    }
}

dependencies {
    // Core AndroidX libraries
    implementation("androidx.core:core-ktx:1.15.0") // Última versión de Android Core KTX
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.activity:activity-ktx:1.7.2")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.recyclerview:recyclerview:1.3.1") // Última versión de RecyclerView

    // Firebase BoM (Gestiona las versiones de las bibliotecas Firebase)
    implementation(platform("com.google.firebase:firebase-bom:33.6.0"))

    // Firebase servicios
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth") // Firebase Auth
    implementation("com.google.firebase:firebase-firestore") // Firestore
    implementation("com.google.firebase:firebase-storage") // Firebase Storage

    // Google Sign-In
    implementation("com.google.android.gms:play-services-auth:20.7.0") // Google Sign-In Library

    // Testing libraries
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
