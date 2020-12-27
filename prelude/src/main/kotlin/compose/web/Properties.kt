package compose.web

import org.w3c.dom.HTMLInputElement

fun Modifier.value(value: String): Modifier =
    property<HTMLInputElement> { this.value = value }
