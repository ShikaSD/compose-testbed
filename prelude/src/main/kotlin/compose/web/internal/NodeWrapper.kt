package compose.web.internal

import compose.web.CssModifier
import compose.web.EventModifier
import compose.web.Modifier
import compose.web.PropertyModifier
import kotlinx.browser.document
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement
import org.w3c.dom.Node
import org.w3c.dom.get

class NodeWrapper internal constructor(internal val realNode: Node) {
    internal constructor(tagName: String) : this(document.createElement(tagName))

    private val eventModifiers = mutableListOf<EventModifier>()
    private val cssModifiers = mutableListOf<CssModifier>()
    private val propertyModifiers = mutableListOf<PropertyModifier<*>>()

    internal var modifier: Modifier = Modifier
        set(value) {
            updateModifier(value)
            field = value
        }

    private fun updateModifier(modifier: Modifier) {
        val element = realNode as HTMLElement

        eventModifiers.forEach {
            element.removeEventListener(it.eventName, it.listener)
        }
        eventModifiers.clear()

        element.style.cssText = ""
        cssModifiers.clear()
        // todo: unapply?
        propertyModifiers.clear()

        modifier.foldOut(Unit) { mod, _ ->
            if (mod is CssModifier) {
                cssModifiers.add(mod)
            }
            if (mod is EventModifier) {
                eventModifiers.add(mod)
            }
            if (mod is PropertyModifier<*>) {
                propertyModifiers.add(mod)
            }
        }

        eventModifiers.forEach {
            element.addEventListener(it.eventName, it.listener)
        }
        propertyModifiers.forEach {
            // not type safe :(
            it as PropertyModifier<HTMLElement>
            element.apply(it.configure)
        }
        cssModifiers.forEach {
            element.style.apply(it.configure)
        }
    }

    fun insert(index: Int, instance: NodeWrapper) {
        if (realNode !is Element) throw IllegalStateException("Cannot add elements to $realNode")

        if (index == realNode.children.length) {
            realNode.appendChild(instance.realNode)
        } else {
            realNode.insertBefore(
                instance.realNode,
                realNode.childNodes[index]
            )
        }
    }

    fun remove(index: Int, count: Int) {
        repeat(count) {
            val removed = realNode.childNodes[index]!!
            realNode.removeChild(removed)
        }
    }

    fun move(from: Int, to: Int, count: Int) {
        // todo: simplify (copied from server-side project for now)
        if (from > to) {
            var next = to
            repeat(count) {
                val node = realNode.childNodes[from]!!
                realNode.removeChild(node)
                realNode.insertBefore(realNode.childNodes[next]!!, node)
                next++
            }
        } else {
            repeat(count) {
                val node = realNode.childNodes[from]!!
                realNode.removeChild(node)
                realNode.insertBefore(realNode.childNodes[to - 1]!!, node)
            }
        }
    }
}
