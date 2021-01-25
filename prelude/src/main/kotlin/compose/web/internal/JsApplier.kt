package compose.web.internal

import androidx.compose.runtime.AbstractApplier
import org.w3c.dom.Node

internal class JsApplier(root: Node) : AbstractApplier<NodeWrapper>(NodeWrapper(root)) {
    override fun onClear() {
        // no-op
    }

    override fun insertBottomUp(index: Int, instance: NodeWrapper) {
        current.insert(index, instance)
    }

    override fun insertTopDown(index: Int, instance: NodeWrapper) {}

    override fun move(from: Int, to: Int, count: Int) {
        current.move(from, to, count)
    }

    override fun remove(index: Int, count: Int) {
        current.remove(index, count)
    }
}
