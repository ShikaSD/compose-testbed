import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import compose.web.Modifier
import compose.web.button
import compose.web.canvas
import compose.web.component.Checkbox
import compose.web.component.CheckboxState
import compose.web.component.InputState
import compose.web.component.TextInput
import compose.web.css
import compose.web.h1
import compose.web.h2
import compose.web.onClick
import compose.web.ref
import compose.web.text
import org.w3c.dom.HTMLElement

@Composable
fun App() {
    h1(Modifier.css { color = "red" }) {
        text("Hello, Compose/JS!")
    }

    var counter by remember { mutableStateOf(0) }

    h2 {
        text("Counter: ${counter}")
    }

    button(Modifier.onClick { counter++ }) {
        text("Increment!")
    }

    h1 {
        text("Input something below")
    }

    val inputState = remember { InputState("") }
    TextInput(state = inputState)

    val checkboxState = remember { CheckboxState(false) }

    Checkbox(state = checkboxState)

    text("input value: ${inputState.value}, checkbox checked: ${checkboxState.value}")

    var canvas: HTMLElement? by remember { mutableStateOf(null) }
    canvas(Modifier.ref { canvas = it }, width = 300, height = 150)
}
