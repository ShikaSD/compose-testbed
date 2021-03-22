import GlobalSnapshotManager.ensureStarted
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.runtime.snapshots.ObserverHandle
import androidx.compose.runtime.snapshots.Snapshot
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
internal object GlobalSnapshotManager {
    private val started = atomic(false)
    private val commitPending = atomic(false)
    private val removeWriteObserver = atomic<ObserverHandle?>(null)

    private val scheduleScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    @OptIn(ExperimentalComposeApi::class)
    fun ensureStarted() {
        if (!started.compareAndSet(false, true)) {
            removeWriteObserver.value = Snapshot.registerGlobalWriteObserver(globalWriteObserver)
        }
    }

    @OptIn(ExperimentalComposeApi::class)
    private val globalWriteObserver: (Any) -> Unit = {
        // Race, but we don't care too much if we end up with multiple calls scheduled.
        if (!commitPending.compareAndSet(false, true)) {
            schedule {
                commitPending.value = false
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
    private val isSynchronizeScheduled = atomic(false)

    /**
     * Synchronously executes any outstanding callbacks and brings snapshots into a
     * consistent, updated state.
     */
    private fun synchronize() {
        scheduledCallbacks.forEach { it.invoke() }
        scheduledCallbacks.clear()
        isSynchronizeScheduled.value = false
    }

    private fun schedule(block: () -> Unit) {
        scheduledCallbacks.add(block)
        if (!isSynchronizeScheduled.compareAndSet(false, true)) {
            isSynchronizeScheduled.value = true
            scheduleScope.launch { synchronize() }
        }
    }
}
