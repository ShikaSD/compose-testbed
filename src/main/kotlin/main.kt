@file:OptIn(ExperimentalComposeApi::class)

import GlobalSnapshotManager.ensureStarted
import androidx.compose.runtime.AbstractApplier
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.runtime.Recomposer
import androidx.compose.runtime.compositionFor
import androidx.compose.runtime.emit
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.runtime.snapshots.SnapshotWriteObserver
import kotlinx.atomicfu.atomic
import kotlinx.browser.document
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.w3c.dom.HTMLElement
import org.w3c.dom.Node
import org.w3c.dom.Text
import org.w3c.dom.events.Event
import org.w3c.dom.get

@Composable
fun tag(name: String, onClick: ((Event) -> Unit)? = null, children: @Composable () -> Unit) {
    emit<Node, JsApplier>(
        ctor = { document.createElement(name) },
        update = {
            set(onClick) { listener ->
                // todo clear as well
                addEventListener("click", listener)
            }
        },
        content = children
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

private class JsApplier(root: HTMLElement) : AbstractApplier<Node>(root) {
    override fun onClear() {
        // no-op
    }

    override fun insertBottomUp(index: Int, instance: Node) {
        val current = current
        if (current !is HTMLElement) error("Cannot to attach $instance to $current")

        if (index == current.children.length) {
            current.appendChild(instance)
        } else {
            current.insertBefore(
                instance,
                current.childNodes[index]
            )
        }
    }

    override fun insertTopDown(index: Int, instance: Node) { }

    override fun remove(index: Int, count: Int) {
        repeat(count) {
            val removed = current.childNodes[index]!!
            current.removeChild(removed)
        }
    }

    override fun move(from: Int, to: Int, count: Int) {
        // todo: simplify (copied from server-side project for now)
        if (from > to) {
            var next = to
            repeat(count) {
                val node = current.childNodes[from]!!
                current.removeChild(node)
                current.insertBefore(current.childNodes[next]!!, node)
                next++
            }
        } else {
            repeat(count) {
                val node = current.childNodes[from]!!
                current.removeChild(node)
                current.insertBefore(current.childNodes[to - 1]!!, node)
            }
        }
    }

}
fun main() {
    GlobalSnapshotManager.ensureStarted()

    val composition = compositionFor(
        key = 0,
        applier = JsApplier(document.body!!),
        parent = Recomposer.current()
    )

    composition.setContent {
        tag("h1") {
            text("Hello, Compose/JS!")
        }

        var counter by remember { mutableStateOf(0) }

        tag("h2") {
            text("Counter: ${counter}")
        }
        tag("button", onClick = {
            counter++
        }) {
            text("Increment!")
        }
    }
}

/**
 * Platform-specific mechanism for starting a monitor of global snapshot state writes
 * in order to schedule the periodic dispatch of snapshot apply notifications.
 * This process should remain platform-specific; it is tied to the threading and update model of
 * a particular platform and framework target.
 *
 * Composition bootstrapping mechanisms for a particular platform/framework should call
 * [ensureStarted] during setup to initialize periodic global snapshot notifications.
 */
internal object GlobalSnapshotManager {
    private val started = atomic(false)
    private var commitPending = false
    private var removeWriteObserver: (() -> Unit)? = null

    private val scheduleScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    @OptIn(ExperimentalComposeApi::class)
    fun ensureStarted() {
        if (started.compareAndSet(false, true)) {
            removeWriteObserver = Snapshot.registerGlobalWriteObserver(globalWriteObserver)
        }
    }

    @OptIn(ExperimentalComposeApi::class)
    private val globalWriteObserver: SnapshotWriteObserver = {
        // Race, but we don't care too much if we end up with multiple calls scheduled.
        if (!commitPending) {
            commitPending = true
            schedule {
                commitPending = false
                Snapshot.sendApplyNotifications()
            }
        }
    }

    /**
     * List of deferred callbacks to run serially. Guarded by its own monitor lock.
     */
    private val scheduledCallbacks = mutableListOf<() -> Unit>()

    /**
     * Guarded by [scheduledCallbacks]'s monitor lock.
     */
    private var isSynchronizeScheduled = false

    /**
     * Synchronously executes any outstanding callbacks and brings snapshots into a
     * consistent, updated state.
     */
    private fun synchronize() {
        synchronized(scheduledCallbacks) {
            scheduledCallbacks.forEach { it.invoke() }
            scheduledCallbacks.clear()
            isSynchronizeScheduled = false
        }
    }

    private fun schedule(block: () -> Unit) {
        synchronized(scheduledCallbacks) {
            scheduledCallbacks.add(block)
            if (!isSynchronizeScheduled) {
                isSynchronizeScheduled = true
                scheduleScope.launch { synchronize() }
            }
        }
    }
}
