package compose.web

import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.HTMLInputElement

fun Modifier.value(value: String): Modifier =
    property<HTMLInputElement> { this.value = value }

fun Modifier.width(value: Int): Modifier =
    property<HTMLCanvasElement> { this.width = value }

fun Modifier.height(value: Int): Modifier =
    property<HTMLCanvasElement> { this.height = value }
