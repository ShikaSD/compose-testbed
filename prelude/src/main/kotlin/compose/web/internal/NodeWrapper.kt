package compose.web.internal

import compose.web.CssModifier
import compose.web.EventModifier
import compose.web.Modifier
import compose.web.PropertyModifier
import compose.web.RefModifier
import kotlinx.browser.document
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement
import org.w3c.dom.Node
import org.w3c.dom.get

internal class NodeWrapper internal constructor(internal val realNode: Node) {
    internal constructor(tagName: String) : this(document.createElement(tagName))

    internal var modifier: Modifier = Modifier
        set(value) {
            updateModifier(previous = field, next = value)
            field = value
        }

    private fun updateModifier(previous: Modifier, next: Modifier) {
        val element = realNode as HTMLElement

        previous.foldOut(Unit) { mod, _ ->
            if (mod is EventModifier) {
                element.removeEventListener(mod.eventName, mod.listener)
            }
        }
        if (element.style.length > 0) {
            element.style.cssText = ""
        }

        next.foldOut(Unit) { mod, _ ->
            when (mod) {
                is CssModifier -> element.style.apply(mod.configure)
                is EventModifier -> element.addEventListener(mod.eventName, mod.listener)
                is PropertyModifier -> element.apply(mod.configure)
                is RefModifier -> mod.configure(element)
            }
        }
    }

    fun insert(index: Int, instance: NodeWrapper) {
        if (realNode !is Element) throw IllegalStateException("Cannot add elements to $realNode")

        realNode.insertBefore(instance.realNode, realNode.childNodes[index])
    }

    fun remove(index: Int, count: Int) {
        if (realNode !is Element) throw IllegalStateException("Cannot remove elements from $realNode")

        repeat(count) {
            realNode.removeChild(realNode.childNodes[index]!!)
        }
    }

    fun move(from: Int, to: Int, count: Int) {
        if (realNode !is Element) throw IllegalStateException("Cannot move elements in $realNode")

        if (from > to) {
            repeat(count) {
                realNode.insertBefore(realNode.childNodes[from + it]!!, realNode.childNodes[to + it])
            }
        } else {
            repeat(count) {
                realNode.insertBefore(realNode.childNodes[from]!!, realNode.childNodes[to])
            }
        }
    }
}
