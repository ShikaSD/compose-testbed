@file:OptIn(ExperimentalComposeApi::class, ExperimentalTime::class)

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ExperimentalComposeApi
import compose.web.Modifier
import compose.web.renderComposable
import compose.web.tag
import compose.web.text
import kotlinx.browser.document
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

fun main() {
//    renderComposable(document.body!!) {
//        App()
//        FooTyped<String>().apply {
//            this.l()
//        }
//    }
    val duration = Benchmark(content = {
        tag(Modifier, "div") {
            for (i in 0 until 1000) {
                tag(Modifier, "div") {
                    text("$it")
                }
            }
        }
    })

    println(duration)
}

@Composable
fun TestRange(to: Int) {
    for (i in 0 until to) {
        tag(Modifier, "div") {
            text("$i")
        }
    }
}

@OptIn(ExperimentalTime::class)
fun Benchmark(content: @Composable (round: Int) -> Unit, rounds: Int = 1): Duration {
    val composition = renderComposable(document.body!!) {}

    var duration = Duration.ZERO
    for (i in 0 until rounds) {
        duration += measureTime {
            composition.setContent { content(i) }
//            composition.setContent { }
        }
    }
    return duration / rounds
}
