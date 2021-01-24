package compose.web

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import compose.web.internal.JsApplier
import compose.web.internal.NodeWrapper
import kotlinx.browser.document
import org.w3c.dom.Text

@Composable
fun tag(modifier: Modifier, tagName: String, content: @Composable () -> Unit) {
    ComposeNode<NodeWrapper, JsApplier>(
        factory = { NodeWrapper(tagName) },
        update = {
            set(modifier) { this.modifier = modifier }
        },
        content = content
    )
}

@Composable
fun text(value: String) {
    ComposeNode<NodeWrapper, JsApplier>(
        factory = { NodeWrapper(document.createTextNode("")) },
        update = {
            set(value) { value ->
                (realNode as Text).data = value
            }
        },
    )
}

