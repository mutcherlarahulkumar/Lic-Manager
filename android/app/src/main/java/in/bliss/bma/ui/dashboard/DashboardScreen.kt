package `in`.bliss.bma.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Dashboard — the clean home screen.
 *
 * Instead of the original app's dense grid of 150 buttons, the rebuild groups
 * work into a small number of clear "modules". Each tile routes into a feature
 * area (see ui/nav/Destinations). Keep this screen calm: a few KPIs on top,
 * then the module grid.
 */
@Composable
fun DashboardScreen(onOpen: (String) -> Unit) {
    val modules = listOf(
        "clients" to "Clients",
        "policies" to "Policies",
        "prospects" to "Leads",
        "commission" to "Commission",
        "reports" to "Reports",
        "agencies" to "Agencies",
        "marketing" to "Marketing",
        "tools" to "Tools",
    )
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Good morning", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        KpiRow()
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(modules) { (route, label) ->
                ElevatedCard(onClick = { onOpen(route) }) {
                    Box(Modifier.fillMaxWidth().height(96.dp).padding(16.dp)) {
                        Text(label, style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    }
}

@Composable
private fun KpiRow() {
    // TODO: back these with a /api/v1/dashboard summary endpoint.
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Kpi("Due this month", "7")
        Kpi("Open leads", "12")
        Kpi("Commission", "₹48.6k")
    }
}

@Composable
private fun RowScope.Kpi(label: String, value: String) {
    ElevatedCard(Modifier.weight(1f)) {
        Column(Modifier.padding(12.dp)) {
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(label, style = MaterialTheme.typography.labelSmall)
        }
    }
}
