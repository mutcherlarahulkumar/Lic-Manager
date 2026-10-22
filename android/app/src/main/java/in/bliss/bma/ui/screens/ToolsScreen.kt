package `in`.bliss.bma.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import `in`.bliss.bma.BuildConfig
import `in`.bliss.bma.ui.components.*
import `in`.bliss.bma.ui.theme.Dp as D

private data class Setting(val icon: ImageVector, val title: String, val value: String)

/** Tools — app settings and info. Backup/document actions plug in as their
 *  endpoints are wired; this shows the live config and build info today. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolsScreen() {
    var showAbout by remember { mutableStateOf(false) }
    val items = listOf(
        Setting(Icons.Filled.Cloud, "Backend", BuildConfig.API_BASE_URL),
        Setting(Icons.Filled.Info, "App version", BuildConfig.VERSION_NAME),
        Setting(Icons.Filled.Sync, "Data backup", "Configure in a later build"),
        Setting(Icons.Filled.Description, "Documents", "Configure in a later build"),
    )
    Scaffold(topBar = { TopAppBar(title = { Text("Tools") }) }) { pad ->
        ResponsiveContainer(Modifier.padding(pad)) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(D.sm),
                contentPadding = PaddingValues(vertical = D.md),
            ) {
                items(items, key = { it.title }) { s ->
                    OutlinedCard(shape = MaterialTheme.shapes.medium, modifier = Modifier.fillMaxWidth()) {
                        ListItem(
                            leadingContent = { Icon(s.icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                            headlineContent = { Text(s.title) },
                            supportingContent = { Text(s.value) },
                            colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surface),
                        )
                    }
                }
                item {
                    OutlinedCard(shape = MaterialTheme.shapes.medium, modifier = Modifier.fillMaxWidth()) {
                        ListItem(
                            leadingContent = { Icon(Icons.Filled.Favorite, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                            headlineContent = { Text("About Lifely") },
                            modifier = Modifier.clickable { showAbout = true },
                            colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surface),
                        )
                    }
                }
            }
        }
    }
    if (showAbout) {
        AlertDialog(
            onDismissRequest = { showAbout = false },
            confirmButton = { TextButton(onClick = { showAbout = false }) { Text("Close") } },
            title = { Text("Lifely") },
            text = { Text("LIC agent management, rebuilt. Kotlin app · Go backend · PostgreSQL.") },
        )
    }
}
