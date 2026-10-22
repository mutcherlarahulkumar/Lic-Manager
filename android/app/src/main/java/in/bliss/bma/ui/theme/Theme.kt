package `in`.bliss.bma.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// Brand: a calm teal for a finance/insurance tool, warm amber for "attention"
// (dues, lapses). Deliberately not a copy of the original app's look.
private val Teal      = Color(0xFF0F766E)
private val TealDark  = Color(0xFF0B5A53)
private val TealTint  = Color(0xFFCCFBF1)
private val Amber     = Color(0xFFB45309)
private val AmberTint = Color(0xFFFEF3C7)
private val Ink       = Color(0xFF0F172A)
private val Mist      = Color(0xFFF1F5F9)

private val Light = lightColorScheme(
    primary = Teal,
    onPrimary = Color.White,
    primaryContainer = TealTint,
    onPrimaryContainer = TealDark,
    secondary = TealDark,
    tertiary = Amber,
    tertiaryContainer = AmberTint,
    onTertiaryContainer = Color(0xFF7C2D12),
    background = Mist,
    onBackground = Ink,
    surface = Color.White,
    onSurface = Ink,
    surfaceVariant = Color(0xFFE2E8F0),
    onSurfaceVariant = Color(0xFF475569),
    outlineVariant = Color(0xFFE2E8F0),
)

private val Dark = darkColorScheme(
    primary = Color(0xFF5EEAD4),
    onPrimary = Color(0xFF00201C),
    primaryContainer = Color(0xFF134E48),
    onPrimaryContainer = TealTint,
    secondary = Color(0xFF99F6E4),
    tertiary = Color(0xFFFBBF24),
    tertiaryContainer = Color(0xFF7C2D12),
    onTertiaryContainer = AmberTint,
    background = Color(0xFF0B1220),
    onBackground = Color(0xFFE2E8F0),
    surface = Color(0xFF111A2B),
    onSurface = Color(0xFFE2E8F0),
    surfaceVariant = Color(0xFF1E293B),
    onSurfaceVariant = Color(0xFF94A3B8),
    outlineVariant = Color(0xFF1E293B),
)

private val BmaShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(28.dp),
)

@Composable
fun BmaTheme(dark: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (dark) Dark else Light,
        typography = BmaTypography,
        shapes = BmaShapes,
        content = content,
    )
}
