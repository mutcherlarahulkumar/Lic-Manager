package `in`.bliss.bma.ui.screens

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import `in`.bliss.bma.ui.components.*
import `in`.bliss.bma.ui.theme.Dp as D

private data class Template(val title: String, val desc: String, val message: String)

private val templates = listOf(
    Template("Birthday wish", "Send a branded birthday greeting", "Wishing you a very happy birthday from your Lifely advisor!"),
    Template("Policy reminder", "Premium due reminder", "A gentle reminder: your LIC premium is due soon. Reply to pay via Lifely."),
    Template("Festival greeting", "Seasonal greeting card", "Season's greetings and best wishes from Lifely!"),
    Template("New plan intro", "Introduce a new plan", "Ask me about LIC plans that protect your family and grow your savings."),
    Template("Referral invite", "Invite a friend", "I trust Lifely for my LIC needs — happy to introduce you to my advisor."),
)

/** Marketing — ready-to-share message templates. Share opens the system sheet
 *  (WhatsApp/SMS/etc.). Rich poster/video generation plugs in later. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketingScreen() {
    val ctx = LocalContext.current
    Scaffold(topBar = { TopAppBar(title = { Text("Marketing") }) }) { pad ->
        ResponsiveContainer(Modifier.padding(pad)) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(D.sm),
                contentPadding = PaddingValues(vertical = D.md),
            ) {
                items(templates, key = { it.title }) { t ->
                    OutlinedCard(shape = MaterialTheme.shapes.medium, modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(D.lg), verticalArrangement = Arrangement.spacedBy(D.xs)) {
                            Text(t.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                            Text(t.desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(Modifier.height(D.xs))
                            FilledTonalButton(onClick = {
                                val send = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, t.message)
                                }
                                ctx.startActivity(Intent.createChooser(send, "Share via"))
                            }) {
                                Icon(Icons.Filled.Share, contentDescription = null)
                                Spacer(Modifier.width(D.sm))
                                Text("Share")
                            }
                        }
                    }
                }
            }
        }
    }
}
