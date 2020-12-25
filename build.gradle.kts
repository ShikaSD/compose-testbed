plugins {
    kotlin("js")
}

repositories {
    jcenter()
    libsRepository(rootDir)
}

subprojects {
    repositories {
        jcenter()
        libsRepository(rootDir)
    }
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
    }
}

configureComposeCompiler()

dependencies {
    implementation(project(":prelude"))
    implementation(composeRuntimeJs())
}

