package compose.web

import androidx.compose.runtime.AbstractApplier
import org.w3c.dom.HTMLElement
import org.w3c.dom.Node
import org.w3c.dom.get

class JsApplier(root: HTMLElement) : AbstractApplier<Node>(root) {
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
