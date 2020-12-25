package compose.web

import androidx.compose.runtime.Composable
import androidx.compose.runtime.emit
import kotlinx.browser.document
import org.w3c.dom.Node
import org.w3c.dom.Text
import org.w3c.dom.events.Event

@Composable
fun tag(name: String, onClick: ((Event) -> Unit)? = null, content: @Composable () -> Unit) {
    emit<Node, JsApplier>(
        ctor = { document.createElement(name) },
        update = {
            set(onClick) { listener ->
                // todo clear as well
                addEventListener("click", listener)
            }
        },
        content = content
    )
}

@Composable
fun text(value: String) {
    emit<Node, JsApplier>(
        ctor = { document.createTextNode("") },
        update = {
            set(value) { value ->
                (this as Text).data = value
            }
        },
    )
}
