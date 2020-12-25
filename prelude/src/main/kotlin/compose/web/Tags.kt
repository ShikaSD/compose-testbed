package compose.web

import androidx.compose.runtime.Composable
import org.w3c.dom.events.Event

@Composable
fun h1(content: @Composable () -> Unit) {
    tag("h1", content = content)
}

@Composable
fun h2(content: @Composable () -> Unit) {
    tag("h2", content = content)
}

@Composable
fun button(onClick: (Event) -> Unit, content: @Composable () -> Unit) {
    tag("button", onClick, content = content)
}

