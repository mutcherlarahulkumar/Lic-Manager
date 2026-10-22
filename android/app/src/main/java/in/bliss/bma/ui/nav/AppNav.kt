package `in`.bliss.bma.ui.nav

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import `in`.bliss.bma.ui.dashboard.DashboardScreen
import `in`.bliss.bma.ui.policies.PoliciesScreen
import `in`.bliss.bma.ui.screens.*

private data class Tab(val route: String, val label: String, val on: ImageVector, val off: ImageVector)

private val tabs = listOf(
    Tab("home", "Home", Icons.Filled.Home, Icons.Outlined.Home),
    Tab("clients", "Clients", Icons.Filled.People, Icons.Outlined.People),
    Tab("policies", "Policies", Icons.Filled.Description, Icons.Outlined.Description),
    Tab("leads", "Leads", Icons.Filled.TrendingUp, Icons.Outlined.TrendingUp),
    Tab("more", "More", Icons.Filled.Menu, Icons.Outlined.Menu),
)

@Composable
fun AppNav() {
    val nav = rememberNavController()
    Scaffold(
        bottomBar = {
            val entry by nav.currentBackStackEntryAsState()
            val current = entry?.destination
            NavigationBar {
                tabs.forEach { tab ->
                    val selected = current?.hierarchy?.any { it.route == tab.route } == true
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            nav.navigate(tab.route) {
                                popUpTo(nav.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(if (selected) tab.on else tab.off, contentDescription = tab.label) },
                        label = { Text(tab.label) },
                    )
                }
            }
        },
    ) { pad ->
        NavHost(nav, startDestination = "home", modifier = Modifier.padding(pad)) {
            composable("home") { DashboardScreen(onOpen = { nav.navigate(it) }) }
            composable("clients") { ClientsScreen() }
            composable("policies") { PoliciesScreen() }
            composable("leads") { LeadsScreen() }
            composable("more") { MoreScreen(onOpen = { nav.navigate(it) }) }

            // Secondary destinations reached from Home / More.
            composable("commission") { CommissionScreen() }
            composable("reports") { ComingSoonScreen("Reports") }
            composable("agencies") { ComingSoonScreen("Agencies") }
            composable("marketing") { ComingSoonScreen("Marketing") }
            composable("tools") { ComingSoonScreen("Tools") }
        }
    }
}
