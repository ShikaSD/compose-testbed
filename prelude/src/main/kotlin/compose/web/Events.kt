package compose.web

import org.w3c.dom.events.Event
import org.w3c.dom.events.EventListener

fun Modifier.click(action: (Event) -> Unit): Modifier =
    EventModifier("click", EventListener(action))
