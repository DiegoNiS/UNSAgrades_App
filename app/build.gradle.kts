plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    // --- PLUGINS ACTIVOS ---
    alias(libs.plugins.ksp)  // Para Room
    //id("kotlin-kapt")        // Para Hilt (Kapt clásico)
    alias(libs.plugins.hilt) // Para Hilt DI
}

android {
    namespace = "com.example.unsagrades"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.unsagrades"
        minSdk = 24
        targetSdk = 36
        versionCode = 4
        versionName = "2.0"

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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }

    applicationVariants.all {
        val variant = this
        variant.outputs
            .map { it as com.android.build.gradle.internal.api.BaseVariantOutputImpl }
            .forEach { output ->
                val appName = "UNSA-Tracker"
                val version = variant.versionName
                val fileName = "$appName-v$version.apk"
                output.outputFileName = fileName
            }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    // --- NAVEGACIÓN (Compose) ---
    // Agregamos esto para que funcionen NavHost y NavController
    implementation("androidx.navigation:navigation-compose:2.9.6")
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
// --- ROOM (Base de Datos) ---
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler) // Generador de código usando KSP
// ESTA ES LA LIBRERÍA QUE TE FALTA PARA 'hiltViewModel()':
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    // --- HILT (Inyección de Dependencias) ---
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)   // <--- AÑADE ESTA LÍNEA. ¡Ahora Hilt usa KSP!
    //ksp(libs.javapoet)
    implementation("androidx.compose.ui:ui-text-google-fonts:1.7.6")
    implementation("androidx.compose.material:material-icons-extended:1.7.6")
    
    // Vico Charts
    implementation("com.patrykandpatrick.vico:compose-m3:2.0.0-alpha.27")
}
//
//// Configuración para corregir errores de tipos en Kapt
//kapt {
//    correctErrorTypes = true
//}