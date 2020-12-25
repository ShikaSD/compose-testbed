plugins {
    kotlin("js")
}

kotlin {
    js(IR) {
        browser()
    }
}

configureComposeCompiler()

dependencies {
    implementation(composeRuntimeJs())
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.1")
}
