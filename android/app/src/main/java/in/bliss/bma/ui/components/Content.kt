package `in`.bliss.bma.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import `in`.bliss.bma.ui.theme.Dp as D

/** Small coloured status pill. Colour conveys meaning (good / attention / neutral). */
@Composable
fun StatusChip(status: String?) {
    val s = (status ?: "—").lowercase()
    val (bg, fg) = when {
        s.contains("force") || s.contains("active") || s.contains("paid") ->
            MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
        s.contains("lapse") || s.contains("due") || s.contains("overdue") ->
            MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
        else ->
            MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
    }
    Surface(color = bg, shape = RoundedCornerShape(50)) {
        Text(
            status ?: "—",
            color = fg,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = D.md, vertical = D.xs),
        )
    }
}

/** Compact KPI tile for dashboards. */
@Composable
fun StatTile(label: String, value: String, modifier: Modifier = Modifier) {
    OutlinedCard(modifier, shape = RoundedCornerShape(D.radius)) {
        Column(Modifier.padding(D.lg)) {
            Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(D.xs))
            Text(
                label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

/** A labelled key/value row, used in detail cards. */
@Composable
fun DetailRow(label: String, value: String?) {
    if (value.isNullOrBlank()) return
    Row(Modifier.fillMaxWidth().padding(vertical = D.xs), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}

/** Section heading with optional trailing action. */
@Composable
fun SectionHeader(title: String, modifier: Modifier = Modifier) {
    Text(
        title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        modifier = modifier.padding(vertical = D.sm),
    )
}
