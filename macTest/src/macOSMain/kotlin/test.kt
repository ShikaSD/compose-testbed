import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.yield
import mock.Linear
import mock.Text
import mock.View

fun main() {
    runBlocking {
        val rootView = View()
        val s = staticCompositionLocalOf { "" }

        val composition = renderComposable(rootView) {
            CompositionLocalProvider(s provides "t") {
                Linear(0) {
                    Text("edit yo ${s.current}")
                }
            }
        }
        println(rootView.children)
        composition.setContent {
            Linear(1) {
                Text("edit")
                Text("test")
                Text("test1")
            }
        }
        yield()
        println(rootView.children)
    }
}

