package compose.web

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Composition
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.runtime.Recomposer
import androidx.compose.runtime.compositionFor
import compose.web.internal.GlobalSnapshotManager
import compose.web.internal.JsApplier
import org.w3c.dom.HTMLElement

@OptIn(ExperimentalComposeApi::class)
fun renderComposable(root: HTMLElement, content: @Composable () -> Unit): Composition {
    GlobalSnapshotManager.ensureStarted()

    val composition = compositionFor(
        key = 0,
        applier = JsApplier(root),
        parent = Recomposer.current()
    )

    composition.setContent(content)
    return composition
}
