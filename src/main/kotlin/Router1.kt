import androidx.compose.runtime.*
import compose.web.*

class Router {
    val backStack: BackStack = BackStack(mutableStateListOf(Root))

    companion object {
        val local: ProvidableCompositionLocal<Router> = staticCompositionLocalOf { Router() }
        val currentIndex: ProvidableCompositionLocal<Int> = compositionLocalOf { 1 }
    }
}

@Composable
fun <T> Router(default: T, block: @Composable (T) -> Unit) {
    val router = Router.local.current
    val stackIndex = Router.currentIndex.current
    val stack = router.backStack.entries.last().stack

    val current = if (stackIndex < stack.size) stack[stackIndex] else default

    CompositionLocalProvider(Router.currentIndex provides stackIndex + 1) {
        block(current as T)
    }
}

fun <T> Router.push(value: Route<T>) {
    backStack.entries += value
}

fun Router.pop() {
    backStack.entries.removeLast()
}

interface Route<T> {
    fun <R> then(value: R): Route<R> =
        Entry(value, parent = stack)

    val stack: List<*>

    val value: T

    class Entry<T>(
        override val value: T,
        parent: List<*>
    ) : Route<T> {
        override val stack: List<*> =
            parent + value
    }
}

operator fun <T> Route<*>.div(value: T): Route<T> =
    then(value)

object Root : Route<Root> {
    override val stack: List<Route<*>> = listOf(Root)
    override val value: Root = Root
}

class BackStack(val entries: MutableList<Route<*>>)

sealed class Page {
    object Initial : Page()
    object Message : Page()
    object Profile : Page()
}

@Composable
fun testRouter1() {
    Router(Page.Initial) { value: Page ->
        val current = Router.local.current.backStack.entries.last()
        console.log(current.stack)

        when (value) {
            Page.Initial -> InitialScreen()
            Page.Message -> Messages()
            Page.Profile -> Profile()
        }
    }
}

@Composable
fun InitialScreen() {
    h1 {
        text("Root")
    }

    Button("Messages", navigateTo = MessagesRoute)
    Button("Profile", navigateTo = ProfileRoute)
}

val ProfileRoute = Root / Page.Profile
@Composable
fun Profile() {
    h1 {
        text("Profile")
    }

    Button("Root", navigateTo = Root)
}

val MessagesRoute = Root / Page.Message
@Composable
fun Messages() {
    h1 {
        text("Messages")
    }

    Router<Message?>(null) {
        if (it != null) {
            CurrentMessage(it.messageId)
        }

        val nextId = it?.messageId?.plus(1) ?: 0
        Button("Message #$nextId", navigateTo = messageRoute(nextId))
    }

    Button("Root", navigateTo = Root)
}

data class Message(val messageId: Int)
fun messageRoute(id: Int): Route<*> = MessagesRoute / Message(id)

@Composable
fun CurrentMessage(id: Int) {
    text("Currently displaying message with id=$id")
}

@Composable
fun Button(text: String, navigateTo: Route<*>) {
    val router = Router.local.current
    button(Modifier.onClick { router.push(navigateTo) }) {
        text(text)
    }
}
