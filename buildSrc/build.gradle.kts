plugins {
    kotlin("jvm") version "1.5.0"
    `kotlin-dsl`
}

repositories {
    jcenter()
}

dependencies {
    implementation(kotlin("gradle-plugin"))
    implementation(gradleApi())
    implementation(gradleKotlinDsl())
}
