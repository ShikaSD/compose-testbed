import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.maven
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinSingleTargetExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import java.io.File

fun RepositoryHandler.libsRepository(rootDir: File) {
    maven(url = File(rootDir, "libs").toURI())
}

fun Project.composeRuntimeJs(): Dependency = dependencies.create("androidx.compose.runtime:runtime-js:1.0.0-beta02")
fun Project.composeRuntimeMacOs(): Dependency = dependencies.create("androidx.compose.runtime:runtime-macos:1.0.0-beta02")

fun Project.configureComposeCompiler() {
    dependencies {
        "kotlinCompilerPluginClasspath"(files("$rootDir/libs/embedded.jar"))
        "kotlinNativeCompilerPluginClasspath"(files("$rootDir/libs/native.jar"))
    }

    fun configure(target: KotlinTarget) {
        target.compilations.configureEach {
            kotlinOptions.freeCompilerArgs += listOf(
                "-Xopt-in=kotlin.RequiresOptIn",
//                "-P", "plugin:androidx.compose.compiler.plugins.kotlin:generateDecoys=true",
                "-P", "plugin:androidx.compose.compiler.plugins.kotlin:suppressKotlinVersionCompatibilityCheck=true"
            )
        }
    }

    extensions.findByType<KotlinMultiplatformExtension>()?.apply {
        targets.configureEach {
            configure(this)
        }
    }
    extensions.findByType<KotlinSingleTargetExtension>()?.apply {
        configure(target)
    }
}
