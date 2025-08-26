import com.android.build.gradle.internal.tasks.databinding.DataBindingGenBaseClassesTask
import org.jetbrains.kotlin.gradle.tasks.AbstractKotlinCompileTool

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.ksp)
    kotlin("kapt")
    id("maven-publish")
    id("kotlin-parcelize")
}

afterEvaluate {
    publishing {
        publications {
            val versionName = project.findProperty("versionName") as String?

            create<MavenPublication>("release") {
                groupId = "com.rocketpay"
                artifactId = "mandate"
                version = versionName

                afterEvaluate {
                    from(components["release"])
                }
            }
            create<MavenPublication>("debug") {
                from(components["debug"])
                groupId = "com.rocketpay"
                artifactId = "mandate-debug"
                version = "$versionName"
            }
        }
        repositories {
            maven {
                // GitHub Packages example
                name = "GitHubPackages"
                url = uri("https://maven.pkg.github.com/UdharPayIndia/mandate-module")
                credentials {
                    username = project.findProperty("gpr.user") as String?
                    password = project.findProperty("gpr.key") as String?
                }
            }
        }
    }
}

android {
    namespace = "com.rocketpay.mandate"
    compileSdk = 35

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        javaCompileOptions {
            annotationProcessorOptions {
                arguments += mapOf(
                    "room.schemaLocation" to "$projectDir/schemas"
                )
            }
        }
    }
    buildFeatures {
        dataBinding = true
        buildConfig = true
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            val versionCode = project.findProperty("versionCode") as String?
            val versionName = project.findProperty("versionName") as String?

            buildConfigField("String", "API_BASE_URL", "\"https://api.rocketpay.co.in\"")
            buildConfigField("String", "VERSION_CODE", "\"$versionCode\"")
            buildConfigField("String", "VERSION_NAME", "\"$versionName\"")

        }
        debug {
            val versionCode = project.findProperty("versionCode") as String?
            val versionName = project.findProperty("versionName") as String?

            buildConfigField("String", "API_BASE_URL", "\"https://api-staging.rocketpay.co.in\"")
            buildConfigField("String", "VERSION_CODE", "\"$versionCode\"")
            buildConfigField("String", "VERSION_NAME", "\"$versionName\"")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    androidComponents {
        onVariants(selector().all(), { variant ->
            afterEvaluate {
                // This is a workaround for https://issuetracker.google.com/301245705 which depends on internal
                // implementations of the android gradle plugin and the ksp gradle plugin which might change in the future
                // in an unpredictable way.
                val variantNameCapitalized = variant.name.replaceFirstChar { it.uppercase() }
                val kspTaskName = "ksp${variantNameCapitalized}Kotlin"
                val dataBindingTaskName = "dataBindingGenBaseClasses${variantNameCapitalized}"

                val kspTask = tasks.named(kspTaskName).get() as AbstractKotlinCompileTool<*>
                val dataBindingTask = tasks.named(dataBindingTaskName).get() as DataBindingGenBaseClassesTask

                kspTask.setSource(dataBindingTask.sourceOutFolder)
            }
        })
    }
    publishing {
        singleVariant("release")
        singleVariant("debug")
    }
}

dependencies {

    api(libs.androidx.core.ktx)
    api(libs.androidx.appcompat)
    api(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //Room
    api(libs.room.runtime)
    api(libs.room.ktx)
    ksp(libs.room.compiler)
    androidTestImplementation(libs.room.testing)

    //Gson
    api(libs.gson)

    // Dagger
    api(libs.dagger)
    api(libs.dagger.android)
    ksp(libs.dagger.compiler)
    ksp(libs.dagger.android.processor)

    //Swipe Refresh
    api(libs.androidx.swiperefreshlayout)

    api(files("libs/network-manager.jar"))
    api(files("libs/kernel-common.jar"))
    api(files("libs/sync-manager-core.jar"))


    // Chuck - Network Debugger
    debugImplementation("com.github.chuckerteam.chucker:library:3.5.2")
    releaseImplementation("com.github.chuckerteam.chucker:library-no-op:3.5.2")

    // hyper verge
    api("co.hyperverge:hyperkyc:0.28.0", {
        isTransitive = true
        exclude(group = "co.hyperverge", module = "hyperdocdetect") // Line to exclude Document-Auto-Capture feature and its dependencies
    })

    api(libs.shimmer)

    //SMS
    api(libs.play.services.auth)
    api(libs.play.services.auth.api.phone)

    //Location
    api(libs.play.services.location)

    //Glide
    api("com.github.bumptech.glide:glide:4.12.0")
    kapt("com.github.bumptech.glide:compiler:4.11.0")

    api("com.squareup.retrofit2:retrofit:2.9.0")

    api("androidx.core:core-ktx:1.13.1")
    api("androidx.appcompat:appcompat:1.7.0")
    api("com.google.android.material:material:1.12.0")

    // To support viewModelScope, LiveData, LifecycleCoroutineScope
    api("androidx.lifecycle:lifecycle-runtime-ktx:2.8.3")
    api("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.3")
    api("androidx.lifecycle:lifecycle-livedata-ktx:2.8.3")

    //QrCodegenerator
    api("com.google.zxing:core:3.5.3")

    //Ads
    api(libs.gms.play.services.ads.identifier)

    // Room components
    api("androidx.room:room-runtime:2.6.1")
    api("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")
    api("androidx.room:room-testing:2.6.1")

    // Background execution
    api ("androidx.work:work-runtime-ktx:2.9.0")
}