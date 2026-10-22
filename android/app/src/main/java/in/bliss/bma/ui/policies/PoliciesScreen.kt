package `in`.bliss.bma.ui.policies

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import `in`.bliss.bma.data.model.Policy
import `in`.bliss.bma.ui.components.*
import `in`.bliss.bma.ui.theme.Dp as D

/**
 * Policies — the reference feature screen. Shows the standard shape every screen
 * follows: ResponsiveContainer + adaptive grid + loading/error/empty states.
 * Cards reflow into 1/2/3 columns as the window grows.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PoliciesScreen(vm: PoliciesViewModel = viewModel()) {
    val state by vm.state.collectAsStateWithLifecycle()
    Scaffold(topBar = { TopAppBar(title = { Text("Policies") }) }) { pad ->
        Box(Modifier.padding(pad)) {
            when {
                state.loading -> LoadingState()
                state.error != null -> ErrorState(state.error!!, vm::refresh)
                state.policies.isEmpty() -> EmptyState("No policies yet", "Add a policy to see it here.")
                else -> ResponsiveContainer {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(adaptiveColumns(compact = 1, medium = 2, expanded = 3)),
                        horizontalArrangement = Arrangement.spacedBy(D.md),
                        verticalArrangement = Arrangement.spacedBy(D.md),
                        contentPadding = PaddingValues(vertical = D.lg),
                    ) {
                        items(state.policies, key = { it.id }) { PolicyCard(it) }
                    }
                }
            }
        }
    }
}

@Composable
private fun PolicyCard(p: Policy) {
    OutlinedCard(shape = RoundedCornerShape(D.radius)) {
        Column(Modifier.padding(D.lg), verticalArrangement = Arrangement.spacedBy(D.xs)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    p.planName ?: "Policy",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                StatusChip(p.status)
            }
            Text(
                "No. ${p.policyNo}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(D.xs))
            DetailRow("Sum assured", p.sumAssured?.let { "₹${"%,.0f".format(it)}" })
            DetailRow("Premium", p.premium?.let { "₹${"%,.0f".format(it)} / ${p.mode ?: ""}".trim() })
            DetailRow("Next due", p.nextDueDate)
        }
    }
}
