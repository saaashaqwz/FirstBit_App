plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.firstbit_app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.firstbit_app"
        minSdk = 30
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    implementation ("androidx.appcompat:appcompat:1.6.1")
    implementation ("androidx.recyclerview:recyclerview:1.3.0")
    implementation ("androidx.viewpager2:viewpager2:1.0.0")
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Core testing libraries
    testImplementation ("junit:junit:4.13.2")
    androidTestImplementation ("androidx.test.ext:junit:1.1.5")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.5.1")

    // Optional: Mockito for mocking dependencies (очень полезно!)
    testImplementation ("org.mockito:mockito-core:5.11.0")
    androidTestImplementation ("org.mockito:mockito-android:5.11.0")

    // Для тестирования работы с базой данных (Room или SQLite)
    androidTestImplementation ("androidx.room:room-testing:2.6.1") // если используете Room
}