package `in`.bliss.bma.ui.policies

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import `in`.bliss.bma.data.model.Policy

/**
 * Policies screen — a clean list of the agent's policies.
 *
 * This is the reference screen: Dashboard and the other feature screens follow
 * the same shape (collect ViewModel state → render loading/error/content).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PoliciesScreen(vm: PoliciesViewModel = viewModel()) {
    val state by vm.state.collectAsStateWithLifecycle()
    Scaffold(topBar = { TopAppBar(title = { Text("Policies") }) }) { pad ->
        Box(Modifier.padding(pad).fillMaxSize()) {
            when {
                state.loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                state.error != null -> ErrorView(state.error!!, vm::refresh)
                else -> LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(state.policies) { PolicyCard(it) }
                }
            }
        }
    }
}

@Composable
private fun PolicyCard(p: Policy) {
    ElevatedCard {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(p.planName ?: "Policy", fontWeight = FontWeight.SemiBold)
                StatusChip(p.status)
            }
            Text("No. ${p.policyNo}", style = MaterialTheme.typography.bodySmall)
            p.sumAssured?.let { Text("Sum assured ₹${"%,.0f".format(it)}") }
            p.nextDueDate?.let { Text("Next due $it", style = MaterialTheme.typography.bodySmall) }
        }
    }
}

@Composable
private fun StatusChip(status: String?) {
    val label = status ?: "—"
    AssistChip(onClick = {}, label = { Text(label) })
}

@Composable
private fun ErrorView(msg: String, onRetry: () -> Unit) {
    Column(
        Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Couldn't load policies", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(4.dp))
        Text(msg, style = MaterialTheme.typography.bodySmall)
        Spacer(Modifier.height(16.dp))
        Button(onClick = onRetry) { Text("Retry") }
    }
}
