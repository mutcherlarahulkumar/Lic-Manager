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
import `in`.bliss.bma.data.model.CommissionSummary
import `in`.bliss.bma.ui.components.*
import `in`.bliss.bma.ui.theme.Dp as D

private val MONTHS = listOf(
    "", "January", "February", "March", "April", "May", "June",
    "July", "August", "September", "October", "November", "December",
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommissionScreen(vm: CommissionViewModel = viewModel()) {
    val state by vm.state.collectAsStateWithLifecycle()
    Scaffold(topBar = { TopAppBar(title = { Text("Commission") }) }) { pad ->
        Box(Modifier.padding(pad)) {
            when {
                state.loading -> LoadingState()
                state.error != null -> ErrorState(state.error!!, vm::refresh)
                state.data == null -> EmptyState("No commission data", "Import a statement to see it here.")
                else -> ResponsiveContainer {
                    CommissionBody(state.data!!)
                }
            }
        }
    }
}

@Composable
private fun CommissionBody(c: CommissionSummary) {
    val total = c.premiumCommission.firstOrNull { it.isTotal }?.commission
        ?: c.premiumCommission.sumOf { it.commission }
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(vertical = D.lg),
        verticalArrangement = Arrangement.spacedBy(D.md),
    ) {
        val period = "${MONTHS.getOrElse(c.month) { c.month.toString() }} ${c.year}"
        ElevatedCard(shape = MaterialTheme.shapes.large, modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(D.lg)) {
                Text(period, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(D.xs))
                Text("₹${"%,.2f".format(total)}", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text("Total commission", style = MaterialTheme.typography.bodySmall)
                c.voucherProcessedDate?.let {
                    Spacer(Modifier.height(D.sm)); Text("Voucher processed $it", style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        SectionHeader("Premium & commission")
        LineTable(
            header = Triple("Description", "Premium", "Commission"),
            rows = c.premiumCommission.map { Triple(it.description, it.premium, it.commission) to it.isTotal },
        )

        if (c.paymentRecovery.isNotEmpty()) {
            SectionHeader("Payment & recovery")
            LineTable(
                header = Triple("Description", "Payment", "Recovery"),
                rows = c.paymentRecovery.map { Triple(it.description, it.payment, it.recovery) to it.isTotal },
            )
        }
    }
}

@Composable
private fun LineTable(header: Triple<String, String, String>, rows: List<Pair<Triple<String, Double, Double>, Boolean>>) {
    ElevatedCard(shape = MaterialTheme.shapes.medium, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(D.md)) {
            Row(Modifier.fillMaxWidth().padding(vertical = D.xs)) {
                Text(header.first, Modifier.weight(2f), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(header.second, Modifier.weight(1f), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(header.third, Modifier.weight(1f), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            HorizontalDivider()
            rows.forEach { (r, isTotal) ->
                val w = if (isTotal) FontWeight.Bold else FontWeight.Normal
                Row(Modifier.fillMaxWidth().padding(vertical = D.sm)) {
                    Text(r.first, Modifier.weight(2f), fontWeight = w, style = MaterialTheme.typography.bodyMedium)
                    Text("%,.0f".format(r.second), Modifier.weight(1f), fontWeight = w, style = MaterialTheme.typography.bodyMedium)
                    Text("%,.0f".format(r.third), Modifier.weight(1f), fontWeight = w, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}
