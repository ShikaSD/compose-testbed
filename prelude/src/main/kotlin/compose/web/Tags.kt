package compose.web

import androidx.compose.runtime.Composable

@Composable
fun h1(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    tag(modifier, "h1", content = content)
}

@Composable
fun h2(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    tag(modifier,"h2", content = content)
}

@Composable
fun button(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    tag(modifier,"button", content = content)
}

@Composable
fun input(modifier: Modifier = Modifier, content: @Composable() () -> Unit = {}) {
    tag(modifier, "input", content = content)
}

@Composable
fun canvas(modifier: Modifier = Modifier, width: Int, height: Int) {
    tag(modifier = modifier.width(width).height(height), tagName = "canvas", content = {})
}

@Composable
fun br() {
    tag(modifier = Modifier, tagName = "br", content = {})
}
