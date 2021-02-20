import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import compose.web.Modifier
import compose.web.button
import compose.web.onClick
import compose.web.text

class RouterImpl(
    private val routes: Map<String, @Composable (params: Router.Params) -> Unit> = hashMapOf()
) : Router {
    override var currentRoute by mutableStateOf("/")

    @Composable
    fun current() {
        println(routes)
        routes[currentRoute]!!.invoke(Router.Params())
    }

    fun configure(action: RouterDefinition.() -> Unit): RouterImpl =
        RouterImpl(
            (RouterDefinition().apply(action).routes + this.routes)
                .also {
                    println("routes $it")
                }
        )

    override fun navigateTo(url: String) {
        if (routes.containsKey(url)) {
            currentRoute = url
        } else {
            throw IllegalArgumentException("$url is not configured by the router")
        }
    }

}

interface Router {
    val currentRoute: String

    fun navigateTo(url: String)

    class Params : Map<String, String> by mapOf()

    companion object {
        val local = compositionLocalOf<Router> { error("Default router") }
    }
}

@DslMarker
annotation class RouterDsl

@RouterDsl
class RouterDefinition {
    val routes: HashMap<String, @Composable (params: Router.Params) -> Unit> = hashMapOf()

    fun route(route: String, content: @Composable (params: Router.Params) -> Unit) {
        println("add $route")
        routes += route to content
    }
}

@Composable
fun Router(definition: RouterDefinition.() -> Unit) {
    val router = currentRouter() as RouterImpl
    val newRouter = remember(router, definition) { router.configure(definition) }
    CompositionLocalProvider(Router.local provides newRouter) {
        newRouter.current()
    }
}

@Composable
fun currentRouter(): Router =
    Router.local.current

@Composable
fun test() {
    val emptyRouter = remember { RouterImpl() }
    CompositionLocalProvider(Router.local provides emptyRouter) {
        Router {
            route("/") {
                Root()
            }
            route("/profile") {
                Profile()
            }
            route("/messages") {
                Messages()
            }
        }
    }
}

@Composable
fun Root() {
    Button("Messages", navigateTo = "/messages")
    Button("Profile", navigateTo = "/profile")
}

@Composable
fun Profile() {
    Button("Root", navigateTo = "/")
}

@Composable
fun Messages() {
    Button("Root", navigateTo = "/")
}

@Composable
fun Button(text: String, navigateTo: String) {
    val router = currentRouter()
    button(Modifier.onClick { router.navigateTo(navigateTo) }) {
        text(text)
    }
}
