import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import compose.web.Modifier
import compose.web.button
import compose.web.canvas
import compose.web.click
import compose.web.css
import compose.web.h1
import compose.web.h2
import compose.web.input
import compose.web.ref
import compose.web.text
import compose.web.value
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLInputElement

@Composable
fun App() {
    h1(Modifier.css { color = "red" }) {
        text("Hello, Compose/JS!")
    }

    var counter by remember { mutableStateOf(0) }

    h2 {
        text("Counter: ${counter}")
    }

    button(Modifier.click { counter++ }) {
        text("Increment!")
    }

    h1 {
        text("Input something below")
    }

    var inputState by remember { mutableStateOf("") }
    input(
        modifier = Modifier
            .value(inputState)
            .input { inputState = (it.target as? HTMLInputElement)?.value.orEmpty() }
    )

    text(inputState)

    var canvas: HTMLElement? by mutableStateOf(null)
    canvas(Modifier.ref { canvas = it }, width = 300, height = 150)

    console.log(canvas)
}
