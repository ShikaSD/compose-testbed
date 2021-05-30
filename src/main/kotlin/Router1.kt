//import androidx.compose.runtime.*
//import compose.web.*
//
//class Router {
//    val backStack: BackStack = BackStack(mutableStateListOf(Root))
//
//    companion object {
//        val local: ProvidableCompositionLocal<Router> = staticCompositionLocalOf { Router() }
//        val currentIndex: ProvidableCompositionLocal<Int> = compositionLocalOf { 1 }
//    }
//}
//
//@Composable
//fun <T> Router(default: T, block: @Composable (T) -> Unit) {
//    val router = Router.local.current
//    val stackIndex = Router.currentIndex.current
//    val stack = router.backStack.entries.last().stack
//
//    val current = if (stackIndex < stack.size) stack[stackIndex] else default
//
//    CompositionLocalProvider(Router.currentIndex provides stackIndex + 1) {
//        block(current as T)
//    }
//}
//
//fun <Current, Next> Router.push(value: Route<Current, Next>) {
//    backStack.entries += value
//}
//
//fun Router.pop() {
//    backStack.entries.removeLast()
//}
//
//interface RoutePart<Next> {
//    operator fun <T, Rt : RoutePart<T>> div(value: Next): Route<Next, T> =
//        Route(value, this as Route<*, Next>)
//
//    operator fun div(value: Next): Route<Next, Nothing> =
//            Route(value, this as Route<*, Next>)
//}
//
//open class Route<Current, Next>(val value: Current, private val parent: Route<*, Current>) : RoutePart<Next> {
//    fun stack(): List<Route<*, *>> {
//        var top: Route<*, *> = this
//        val acc = mutableListOf<Route<*, *>>()
//        while (top != Root) {
//            acc += top
//            top = top.parent
//        }
//        acc += Root
//        return acc
//    }
//}
//
//object Root : Route<Root, Root>(Root, Root)
//fun <Next> rootFor(): RoutePart<Next> = Root as RoutePart<Next>
//
//class BackStack(val entries: MutableList<Route<*, *>>)
//
//sealed class Page {
//    object Initial : Page()
//    object Message : Page(), RoutePart<MessageId>
//    object Profile : Page()
//}
//
//@Composable
//fun testRouter1() {
//    Router(Page.Initial) { value: Page ->
//        val current = Router.local.current.backStack.entries.last()
//        console.log(current.stack)
//
//        when (value) {
//            Page.Initial -> InitialScreen()
//            Page.Message -> Messages()
//            Page.Profile -> Profile()
//        }
//    }
//}
//
//@Composable
//fun InitialScreen() {
//    h1 {
//        text("Root")
//    }
//
//    Button("Messages", navigateTo = MessagesRoute)
//    Button("Profile", navigateTo = ProfileRoute)
//}
//
//val ProfileRoute = rootFor<Page>() / Page.Profile
//@Composable
//fun Profile() {
//    h1 {
//        text("Profile")
//    }
//
//    Button("Root", navigateTo = Root)
//}
//
//val MessagesRoute = rootFor<Page>() / Page.Message
//@Composable
//fun Messages() {
//    h1 {
//        text("Messages")
//    }
//
//    Router<MessageId?>(null) {
//        if (it != null) {
//            CurrentMessage(it.messageId)
//        }
//
//        val nextId = it?.messageId?.plus(1) ?: 0
//        Button("Message #$nextId", navigateTo = messageRoute(nextId))
//    }
//
//    Button("Root", navigateTo = Root)
//}
//
//data class MessageId(val messageId: Int)
//fun messageRoute(id: Int) = MessagesRoute / MessageId(id)
//
//@Composable
//fun CurrentMessage(id: Int) {
//    text("Currently displaying message with id=$id")
//}
//
//@Composable
//fun Button(text: String, navigateTo: Route<*, *>) {
//    val router = Router.local.current
//    button(Modifier.onClick { router.push(navigateTo) }) {
//        text(text)
//    }
//}
