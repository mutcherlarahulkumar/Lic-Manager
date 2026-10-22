package `in`.bliss.bma.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import `in`.bliss.bma.data.model.Policy
import `in`.bliss.bma.ui.components.*
import `in`.bliss.bma.ui.theme.Dp as D

/** Reports — a live business summary computed from the policy data. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(vm: ReportsViewModel = viewModel()) {
    val state by vm.state.collectAsStateWithLifecycle()
    Scaffold(topBar = { TopAppBar(title = { Text("Reports") }) }) { pad ->
        Box(Modifier.padding(pad)) {
            when {
                state.loading -> LoadingState()
                state.error != null -> ErrorState(state.error!!, vm::refresh)
                else -> ResponsiveContainer { Summary(state.data.orEmpty()) }
            }
        }
    }
}

@Composable
private fun Summary(policies: List<Policy>) {
    val total = policies.size
    val inforce = policies.count { (it.status ?: "").contains("force", true) }
    val lapsed = policies.count { (it.status ?: "").contains("lapse", true) }
    val sumAssured = policies.sumOf { it.sumAssured ?: 0.0 }
    val premium = policies.sumOf { it.premium ?: 0.0 }
    val byStatus = policies.groupingBy { it.status ?: "unknown" }.eachCount().toList().sortedByDescending { it.second }

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(vertical = D.lg),
        verticalArrangement = Arrangement.spacedBy(D.md),
    ) {
        SectionHeader("Portfolio")
        Row(horizontalArrangement = Arrangement.spacedBy(D.md)) {
            StatTile("Policies", total.toString(), Modifier.weight(1f))
            StatTile("In-force", inforce.toString(), Modifier.weight(1f))
            StatTile("Lapsed", lapsed.toString(), Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(D.md)) {
            StatTile("Sum assured", "₹${"%,.0f".format(sumAssured)}", Modifier.weight(1f))
            StatTile("Premium", "₹${"%,.0f".format(premium)}", Modifier.weight(1f))
        }

        SectionHeader("By status")
        OutlinedCard(shape = MaterialTheme.shapes.medium, modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(D.md)) {
                if (byStatus.isEmpty()) {
                    Text("No policies to summarise.", style = MaterialTheme.typography.bodyMedium)
                } else byStatus.forEach { (status, count) ->
                    Row(Modifier.fillMaxWidth().padding(vertical = D.sm), horizontalArrangement = Arrangement.SpaceBetween) {
                        StatusChip(status)
                        Text(count.toString(), fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        Text(
            "More reports (commission, renewals, agency ranking) plug in here as their endpoints are wired.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
