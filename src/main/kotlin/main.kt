@file:OptIn(ExperimentalComposeApi::class, ExperimentalTime::class)

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.runtime.emptyContent
import compose.web.renderComposable
import kotlinx.browser.document
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

fun main() {
    renderComposable(document.body!!) {
        App()
    }
}

fun Benchmark(content: @Composable (round: Int) -> Unit, rounds: Int = 100): Duration {
    val composition = renderComposable(document.body!!, emptyContent())

    var duration = Duration.ZERO
    for (i in 0 until rounds) {
        duration += measureTime {
            composition.setContent { content(-1) }
        }
        composition.setContent { emptyContent() }
    }
    return duration / rounds
}

