package `in`.bliss.bma.ui.nav

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import `in`.bliss.bma.ui.dashboard.DashboardScreen
import `in`.bliss.bma.ui.policies.PoliciesScreen

/**
 * Central navigation graph. Two destinations are implemented (dashboard,
 * policies) as the reference; the remaining module routes are declared with
 * placeholders so you can fill them in one by one. See android/README.md for
 * the full screen plan mapped from the original app.
 */
@Composable
fun AppNav() {
    val nav = rememberNavController()
    NavHost(navController = nav, startDestination = "dashboard") {
        composable("dashboard") { DashboardScreen(onOpen = { nav.navigate(it) }) }
        composable("policies") { PoliciesScreen() }

        // TODO: implement these screens (placeholders keep navigation working).
        composable("clients") { Placeholder("Clients") }
        composable("prospects") { Placeholder("Leads") }
        composable("commission") { Placeholder("Commission") }
        composable("reports") { Placeholder("Reports") }
        composable("agencies") { Placeholder("Agencies") }
        composable("marketing") { Placeholder("Marketing") }
        composable("tools") { Placeholder("Tools") }
    }
}

@Composable
private fun Placeholder(name: String) = Text("$name — TODO")
