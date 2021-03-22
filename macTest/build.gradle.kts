plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
}

version = "1.0"

kotlin {
//    cocoapods {
//        // Configure fields required by CocoaPods.
//        summary = "Test compose"
//        homepage = "lol"
//        frameworkName = "Compose"
//        podfile = project.file("App/Test/Podfile")
//    }

    macosX64("macOS") {
        binaries.executable()
    }

    sourceSets {
        val macOSMain by getting {
            dependencies {
                implementation(composeRuntimeMacOs())
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.1")
            }
        }
    }
}

configureComposeCompiler()
