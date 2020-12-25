@file:OptIn(ExperimentalComposeApi::class)

import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.runtime.Recomposer
import androidx.compose.runtime.compositionFor
import compose.web.GlobalSnapshotManager
import compose.web.internal.JsApplier
import kotlinx.browser.document

fun main() {
    GlobalSnapshotManager.ensureStarted()

    val composition = compositionFor(
        key = 0,
        applier = JsApplier(document.body!!),
        parent = Recomposer.current()
    )

    composition.setContent {
        App()
    }
}

