package `in`.bliss.bma.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import `in`.bliss.bma.data.model.Client
import `in`.bliss.bma.ui.components.*
import `in`.bliss.bma.ui.theme.Dp as D

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientsScreen(vm: ClientsViewModel = viewModel()) {
    val state by vm.state.collectAsStateWithLifecycle()
    var query by rememberSaveable { mutableStateOf("") }

    Scaffold(topBar = { TopAppBar(title = { Text("Clients") }) }) { pad ->
        Box(Modifier.padding(pad)) {
            when {
                state.loading -> LoadingState()
                state.error != null -> ErrorState(state.error!!, vm::refresh)
                else -> {
                    val all = state.data.orEmpty()
                    val list = all.filter {
                        query.isBlank() ||
                            it.fullName.contains(query, true) ||
                            (it.mobile?.contains(query, true) == true)
                    }
                    ResponsiveContainer {
                        Column {
                            SearchField(query, { query = it }, "Search name or mobile")
                            if (list.isEmpty()) {
                                EmptyState(
                                    if (all.isEmpty()) "No clients yet" else "No matches",
                                    if (all.isEmpty()) "Clients you add will appear here." else "Try a different search.",
                                )
                            } else {
                                LazyColumn(
                                    verticalArrangement = Arrangement.spacedBy(D.sm),
                                    contentPadding = PaddingValues(vertical = D.md),
                                ) {
                                    items(list, key = { it.id }) { ClientRow(it) }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ClientRow(c: Client) {
    ListItem(
        leadingContent = { Avatar(c.fullName) },
        headlineContent = { Text(c.fullName, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        supportingContent = {
            val sub = listOfNotNull(c.mobile, c.email).joinToString(" · ")
            if (sub.isNotBlank()) Text(sub, maxLines = 1, overflow = TextOverflow.Ellipsis)
        },
        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.clip(MaterialTheme.shapes.medium),
    )
}

@Composable
private fun Avatar(name: String) {
    val initials = name.trim().split(" ").mapNotNull { it.firstOrNull()?.uppercase() }.take(2).joinToString("")
    Surface(color = MaterialTheme.colorScheme.primaryContainer, shape = CircleShape) {
        Box(Modifier.size(40.dp), contentAlignment = Alignment.Center) {
            Text(initials.ifBlank { "?" }, color = MaterialTheme.colorScheme.onPrimaryContainer, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchField(value: String, onChange: (String) -> Unit, placeholder: String) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        placeholder = { Text(placeholder) },
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
        singleLine = true,
        shape = MaterialTheme.shapes.large,
        modifier = Modifier.fillMaxWidth().padding(top = D.md),
    )
}
