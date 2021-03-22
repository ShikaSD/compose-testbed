import androidx.compose.runtime.Composable
import androidx.compose.runtime.Composition
import androidx.compose.runtime.DefaultMonotonicFrameClock
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.runtime.Recomposer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch
import mock.View
import mock.ViewApplier
import kotlin.native.concurrent.ensureNeverFrozen

@OptIn(ExperimentalComposeApi::class)
fun CoroutineScope.renderComposable(root: View, content: @Composable () -> Unit): Composition {
    GlobalSnapshotManager.ensureStarted()

    val context = coroutineContext + DefaultMonotonicFrameClock
    val recomposer = Recomposer(context)

    recomposer.ensureNeverFrozen()

    launch(context = context, start = CoroutineStart.UNDISPATCHED) {
        recomposer.runRecomposeAndApplyChanges()
    }

    val composition = Composition(
        applier = ViewApplier(root),
        parent = recomposer
    )
    composition.setContent(content)

    return composition
}
