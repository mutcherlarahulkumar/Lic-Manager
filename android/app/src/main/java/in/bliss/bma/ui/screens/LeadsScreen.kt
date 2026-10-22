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
import `in`.bliss.bma.data.model.Prospect
import `in`.bliss.bma.ui.components.*
import `in`.bliss.bma.ui.theme.Dp as D

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeadsScreen(vm: LeadsViewModel = viewModel()) {
    val state by vm.state.collectAsStateWithLifecycle()
    Scaffold(topBar = { TopAppBar(title = { Text("Leads") }) }) { pad ->
        Box(Modifier.padding(pad)) {
            when {
                state.loading -> LoadingState()
                state.error != null -> ErrorState(state.error!!, vm::refresh)
                state.data.orEmpty().isEmpty() -> EmptyState("No leads yet", "Add prospects to build your pipeline.")
                else -> ResponsiveContainer {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(D.md),
                        contentPadding = PaddingValues(vertical = D.lg),
                    ) {
                        items(state.data.orEmpty(), key = { it.id }) { LeadCard(it) }
                    }
                }
            }
        }
    }
}

@Composable
private fun LeadCard(p: Prospect) {
    ElevatedCard(shape = MaterialTheme.shapes.medium) {
        Column(Modifier.fillMaxWidth().padding(D.lg), verticalArrangement = Arrangement.spacedBy(D.xs)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(p.fullName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                StageChip(p.stage)
            }
            p.interest?.let { Text(it, style = MaterialTheme.typography.bodyMedium) }
            DetailRow("Mobile", p.mobile)
            DetailRow("Follow-up", p.followUpDate)
        }
    }
}

@Composable
private fun StageChip(stage: String) {
    val color = when (stage.lowercase()) {
        "won" -> MaterialTheme.colorScheme.primaryContainer
        "lost" -> MaterialTheme.colorScheme.surfaceVariant
        "quoted", "contacted" -> MaterialTheme.colorScheme.tertiaryContainer
        else -> MaterialTheme.colorScheme.secondaryContainer
    }
    Surface(color = color, shape = MaterialTheme.shapes.extraLarge) {
        Text(
            stage.replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = D.md, vertical = D.xs),
        )
    }
}
