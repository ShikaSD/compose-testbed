package compose.web

import org.w3c.dom.events.Event
import org.w3c.dom.events.EventListener
import org.w3c.dom.events.InputEvent
import org.w3c.dom.events.KeyboardEvent

fun Modifier.onClick(action: (Event) -> Unit): Modifier =
    event("click", EventListener(action))

fun Modifier.onInput(action: (InputEvent) -> Unit): Modifier =
    event("input", EventListener { action(it as InputEvent) })

fun Modifier.onKeyUp(action: (KeyboardEvent) -> Unit): Modifier =
    event("keyup", EventListener { action(it as KeyboardEvent) })
