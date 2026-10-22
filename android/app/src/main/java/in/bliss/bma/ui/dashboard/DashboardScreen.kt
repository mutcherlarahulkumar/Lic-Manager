package `in`.bliss.bma.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import `in`.bliss.bma.ui.components.*
import `in`.bliss.bma.ui.theme.Dp as D

private data class Module(val route: String, val label: String, val icon: ImageVector)

private val modules = listOf(
    Module("clients", "Clients", Icons.Filled.People),
    Module("policies", "Policies", Icons.Filled.Description),
    Module("prospects", "Leads", Icons.Filled.TrendingUp),
    Module("commission", "Commission", Icons.AutoMirrored.Filled.ReceiptLong),
    Module("reports", "Reports", Icons.Filled.BarChart),
    Module("agencies", "Agencies", Icons.Filled.Business),
    Module("marketing", "Marketing", Icons.Filled.Campaign),
    Module("tools", "Tools", Icons.Filled.Build),
)

/**
 * Dashboard — the clean home. A calm KPI row, then an adaptive module grid
 * (2 columns on phones, 3–4 on larger screens). Replaces the original app's
 * dense flat grid of 150 buttons.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(onOpen: (String) -> Unit) {
    Scaffold(topBar = { TopAppBar(title = { Text("BMA") }) }) { pad ->
        ResponsiveContainer(Modifier.padding(pad)) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(adaptiveColumns()),
                horizontalArrangement = Arrangement.spacedBy(D.md),
                verticalArrangement = Arrangement.spacedBy(D.md),
                contentPadding = PaddingValues(vertical = D.lg),
            ) {
                // Greeting spans the full row.
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text(
                        "Good morning",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                    )
                }
                // KPIs: one per column, three tiles.
                item(span = { GridItemSpan(maxLineSpan) }) { KpiRow() }

                item(span = { GridItemSpan(maxLineSpan) }) { SectionHeader("Modules") }

                items(modules) { m -> ModuleCard(m, onOpen) }
            }
        }
    }
}

@Composable
private fun KpiRow() {
    // TODO: back with a /api/v1/dashboard summary endpoint.
    Row(horizontalArrangement = Arrangement.spacedBy(D.md)) {
        StatTile("Due this month", "7", Modifier.weight(1f))
        StatTile("Open leads", "12", Modifier.weight(1f))
        StatTile("Commission", "₹48.6k", Modifier.weight(1f))
    }
}

@Composable
private fun ModuleCard(m: Module, onOpen: (String) -> Unit) {
    ElevatedCard(onClick = { onOpen(m.route) }, shape = RoundedCornerShape(D.radius)) {
        Column(
            Modifier.fillMaxWidth().heightIn(min = 104.dp).padding(D.lg),
            verticalArrangement = Arrangement.spacedBy(D.sm),
        ) {
            Icon(m.icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Text(m.label, style = MaterialTheme.typography.titleMedium)
        }
    }
}
