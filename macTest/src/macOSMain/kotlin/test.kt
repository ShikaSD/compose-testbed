import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.yield
import mock.Linear
import mock.Text
import mock.View

fun main() {
    runBlocking {
        val rootView = View()
        val composition = renderComposable(rootView) {
            Linear {
                Text("edit")
            }
        }
        println(rootView.children)
        composition.setContent {
            Linear {
                Text("edit")
                Text("test")
                Text("test1")
            }
        }
        yield()
        println(rootView.children)
    }
}
