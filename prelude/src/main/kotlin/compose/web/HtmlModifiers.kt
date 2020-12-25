package compose.web

import org.w3c.dom.css.CSSStyleDeclaration
import org.w3c.dom.events.EventListener

internal class EventModifier(val eventName: String, val listener: EventListener) : Modifier.Element

internal class CssModifier(val configure: CSSStyleDeclaration.() -> Unit) : Modifier.Element

fun Modifier.css(configure: CSSStyleDeclaration.() -> Unit): Modifier =
    this.then(CssModifier(configure))

fun Modifier.event(eventName: String, listener: EventListener) : Modifier =
    this.then(EventModifier(eventName, listener))
