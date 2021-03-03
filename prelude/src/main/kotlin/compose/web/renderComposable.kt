package compose.web

import androidx.compose.runtime.*
import compose.web.internal.GlobalSnapshotManager
import compose.web.internal.JsApplier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.w3c.dom.HTMLElement

@OptIn(ExperimentalComposeApi::class)
fun renderComposable(root: HTMLElement, content: @Composable () -> Unit): Composition {
    GlobalSnapshotManager.ensureStarted()

    val context = DefaultMonotonicFrameClock + Dispatchers.Main
    val recomposer = Recomposer(context)

    CoroutineScope(context).launch(start = CoroutineStart.UNDISPATCHED) {
        recomposer.runRecomposeAndApplyChanges()
    }

    val composition = Composition(
        applier = JsApplier(root),
        parent = recomposer
    )
    composition.setContent(content)

    return composition
}
