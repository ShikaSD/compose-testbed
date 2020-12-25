package compose.web

import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.runtime.snapshots.SnapshotWriteObserver
import compose.web.GlobalSnapshotManager.ensureStarted
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Platform-specific mechanism for starting a monitor of global snapshot state writes
 * in order to schedule the periodic dispatch of snapshot apply notifications.
 * This process should remain platform-specific; it is tied to the threading and update model of
 * a particular platform and framework target.
 *
 * Composition bootstrapping mechanisms for a particular platform/framework should call
 * [ensureStarted] during setup to initialize periodic global snapshot notifications.
 */
object GlobalSnapshotManager {
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
