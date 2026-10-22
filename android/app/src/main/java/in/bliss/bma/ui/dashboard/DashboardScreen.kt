package `in`.bliss.bma.ui.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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

private data class Action(val route: String, val label: String, val icon: ImageVector)

private val quickActions = listOf(
    Action("commission", "Commission", Icons.AutoMirrored.Filled.ReceiptLong),
    Action("reports", "Reports", Icons.Filled.BarChart),
    Action("agencies", "Agencies", Icons.Filled.Business),
    Action("marketing", "Marketing", Icons.Filled.Campaign),
    Action("tools", "Tools", Icons.Filled.Build),
)

/**
 * Home — a calm hero header, headline KPIs, then quick actions. The primary
 * areas (Clients, Policies, Leads) live in the bottom bar; this surfaces the
 * next layer without the original app's wall of 150 buttons.
 */
@Composable
fun DashboardScreen(onOpen: (String) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(adaptiveColumns(compact = 2, medium = 3, expanded = 4)),
        horizontalArrangement = Arrangement.spacedBy(D.md),
        verticalArrangement = Arrangement.spacedBy(D.md),
        contentPadding = PaddingValues(D.lg),
        modifier = Modifier.fillMaxSize(),
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) { Hero() }
        item(span = { GridItemSpan(maxLineSpan) }) { KpiRow() }
        item(span = { GridItemSpan(maxLineSpan) }) { SectionHeader("Quick actions") }
        items(quickActions, key = { it.route }) { a -> ActionCard(a, onOpen) }
    }
}

@Composable
private fun Hero() {
    Surface(
        color = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        shape = MaterialTheme.shapes.large,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(D.xl), verticalArrangement = Arrangement.spacedBy(D.xs)) {
            Text("Welcome back", style = MaterialTheme.typography.labelLarge)
            Text("Your agency at a glance", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(
                "Dues, leads and commission — all in one place.",
                style = MaterialTheme.typography.bodyMedium,
            )
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
private fun ActionCard(a: Action, onOpen: (String) -> Unit) {
    ElevatedCard(shape = MaterialTheme.shapes.medium, modifier = Modifier.clickable { onOpen(a.route) }) {
        Column(
            Modifier.fillMaxWidth().heightIn(min = 100.dp).padding(D.lg),
            verticalArrangement = Arrangement.spacedBy(D.sm),
        ) {
            Icon(a.icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Text(a.label, style = MaterialTheme.typography.titleMedium)
        }
    }
}
