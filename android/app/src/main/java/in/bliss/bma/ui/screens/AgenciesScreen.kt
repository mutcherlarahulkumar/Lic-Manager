package `in`.bliss.bma.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import `in`.bliss.bma.data.model.Agency
import `in`.bliss.bma.ui.components.*
import `in`.bliss.bma.ui.theme.Dp as D

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgenciesScreen(vm: AgenciesViewModel = viewModel()) {
    val state by vm.state.collectAsStateWithLifecycle()
    Scaffold(topBar = { TopAppBar(title = { Text("Agencies") }) }) { pad ->
        Box(Modifier.padding(pad)) {
            when {
                state.loading -> LoadingState()
                state.error != null -> ErrorState(state.error!!, vm::refresh)
                state.data.orEmpty().isEmpty() -> EmptyState("No agencies", "Agencies you manage will show here.")
                else -> ResponsiveContainer {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(D.md),
                        contentPadding = PaddingValues(vertical = D.lg),
                    ) {
                        items(state.data.orEmpty(), key = { it.id }) { AgencyCard(it) }
                    }
                }
            }
        }
    }
}

@Composable
private fun AgencyCard(a: Agency) {
    OutlinedCard(shape = MaterialTheme.shapes.medium) {
        Column(Modifier.fillMaxWidth().padding(D.lg), verticalArrangement = Arrangement.spacedBy(D.xs)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(a.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                a.status?.let { StatusChip(it) }
            }
            Text("Code ${a.agencyCode}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            DetailRow("Branch", a.branchCode)
            DetailRow("Club", a.clubTier)
            if (a.ranking > 0) DetailRow("Ranking", "#${a.ranking}")
        }
    }
}
