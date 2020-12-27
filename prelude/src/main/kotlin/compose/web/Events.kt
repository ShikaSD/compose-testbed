package compose.web

import org.w3c.dom.events.Event
import org.w3c.dom.events.EventListener
import org.w3c.dom.events.InputEvent
import org.w3c.dom.events.KeyboardEvent

fun Modifier.click(action: (Event) -> Unit): Modifier =
    event("click", EventListener(action))

fun Modifier.input(action: (InputEvent) -> Unit): Modifier =
    event("input", EventListener { action(it as InputEvent) })

fun Modifier.keyup(action: (KeyboardEvent) -> Unit): Modifier =
    event("keyup", EventListener { action(it as KeyboardEvent) })
