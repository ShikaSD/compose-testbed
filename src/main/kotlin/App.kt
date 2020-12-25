import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import compose.web.Modifier
import compose.web.button
import compose.web.click
import compose.web.css
import compose.web.h1
import compose.web.h2
import compose.web.text

@Composable
fun App() {
    h1(Modifier.css { color = "red" }) {
        text("Hello, Compose/JS!")
    }

    val counter = remember { mutableStateOf(0) }

    h2 {
        text("Counter: ${counter.value}")
    }

    button(Modifier.click { counter.value++ }) {
        text("Increment!")
    }
}
