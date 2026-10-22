package `in`.bliss.bma.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import `in`.bliss.bma.ui.components.*
import `in`.bliss.bma.ui.theme.Dp as D

data class MoreEntry(val route: String, val label: String, val subtitle: String, val icon: ImageVector)

private val entries = listOf(
    MoreEntry("commission", "Commission", "Monthly statements & recovery", Icons.AutoMirrored.Filled.ReceiptLong),
    MoreEntry("reports", "Reports", "Business, ranking & summaries", Icons.Filled.BarChart),
    MoreEntry("agencies", "Agencies", "Agencies you manage", Icons.Filled.Business),
    MoreEntry("marketing", "Marketing", "Posters, videos & invitations", Icons.Filled.Campaign),
    MoreEntry("tools", "Tools", "Settings, backup & documents", Icons.Filled.Build),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreScreen(onOpen: (String) -> Unit) {
    Scaffold(topBar = { TopAppBar(title = { Text("More") }) }) { pad ->
        ResponsiveContainer(Modifier.padding(pad)) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(D.sm),
                contentPadding = PaddingValues(vertical = D.md),
            ) {
                items(entries, key = { it.route }) { e ->
                    ElevatedCard(shape = MaterialTheme.shapes.medium, modifier = Modifier.fillMaxWidth()) {
                        ListItem(
                            leadingContent = { Icon(e.icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                            headlineContent = { Text(e.label) },
                            supportingContent = { Text(e.subtitle) },
                            trailingContent = { Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null) },
                            colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.clickable { onOpen(e.route) },
                        )
                    }
                }
            }
        }
    }
}

/** Styled "coming soon" screen for modules not yet built, kept consistent. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComingSoonScreen(title: String) {
    Scaffold(topBar = { TopAppBar(title = { Text(title) }) }) { pad ->
        Box(Modifier.padding(pad)) {
            EmptyState("$title — coming soon", "This module is scaffolded next. See docs/REBUILD_TODO.md.")
        }
    }
}
