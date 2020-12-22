plugins {
    id("org.jetbrains.kotlin.js") version "1.4.21"
}

repositories {
    jcenter()
    maven(url = File(projectDir, "libs").toURI().toString())
}

kotlin {
    js(IR) {
        browser {
            webpackTask {
                cssSupport.enabled = true
            }

            runTask {
                cssSupport.enabled = true
            }

            testTask {
                useKarma {
                    useChromeHeadless()
                    webpackConfig.cssSupport.enabled = true
                }
            }
        }
        binaries.executable()

        compilations.forEach {
            it.kotlinOptions.freeCompilerArgs += listOf(
                "-Xopt-in=kotlin.RequiresOptIn",
                "-P", "plugin:androidx.compose.compiler.plugins.kotlin:generateDecoys=true"
            )
        }
    }
}

dependencies {
    kotlinCompilerPluginClasspath(files("libs/embedded.jar"))

    implementation("androidx.compose.runtime:runtime-js:1.0.0-alpha10")
    implementation(kotlin("stdlib-js"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.1")
}
