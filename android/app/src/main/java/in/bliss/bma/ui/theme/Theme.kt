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

// shadcn/ui "zinc" aesthetic ported onto Material 3:
//   neutral surfaces, a near-black primary, borders instead of heavy shadows,
//   muted-foreground secondary text, 0.5rem radius. A restrained amber is kept
//   only for "attention" states (dues / lapses).

// light (zinc)
private val Zinc50  = Color(0xFFFAFAFA)
private val Zinc100 = Color(0xFFF4F4F5)
private val Zinc200 = Color(0xFFE4E4E7)
private val Zinc500 = Color(0xFF71717A)
private val Zinc900 = Color(0xFF18181B)
private val Ink     = Color(0xFF09090B)
private val Amber100 = Color(0xFFFEF3C7)
private val Amber700 = Color(0xFFB45309)

private val Light = lightColorScheme(
    primary = Zinc900,
    onPrimary = Zinc50,
    primaryContainer = Zinc100,
    onPrimaryContainer = Zinc900,
    secondary = Zinc900,
    onSecondary = Zinc50,
    secondaryContainer = Zinc100,
    onSecondaryContainer = Zinc900,
    tertiary = Amber700,
    tertiaryContainer = Amber100,
    onTertiaryContainer = Color(0xFF7C2D12),
    background = Color.White,
    onBackground = Ink,
    surface = Color.White,
    onSurface = Ink,
    surfaceVariant = Zinc100,
    onSurfaceVariant = Zinc500,
    outline = Zinc200,
    outlineVariant = Zinc200,
)

// dark (zinc)
private val DZinc = Color(0xFF09090B)
private val DCard = Color(0xFF0F0F11)
private val DBorder = Color(0xFF27272A)
private val DMutedFg = Color(0xFFA1A1AA)
private val DFg = Color(0xFFFAFAFA)

private val Dark = darkColorScheme(
    primary = DFg,
    onPrimary = Zinc900,
    primaryContainer = DBorder,
    onPrimaryContainer = DFg,
    secondary = DFg,
    onSecondary = Zinc900,
    secondaryContainer = DBorder,
    onSecondaryContainer = DFg,
    tertiary = Color(0xFFFBBF24),
    tertiaryContainer = Color(0xFF7C2D12),
    onTertiaryContainer = Amber100,
    background = DZinc,
    onBackground = DFg,
    surface = DCard,
    onSurface = DFg,
    surfaceVariant = DBorder,
    onSurfaceVariant = DMutedFg,
    outline = DBorder,
    outlineVariant = DBorder,
)

// shadcn radius = 0.5rem (~8dp); larger containers a touch rounder.
private val BmaShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(6.dp),
    medium = RoundedCornerShape(8.dp),
    large = RoundedCornerShape(12.dp),
    extraLarge = RoundedCornerShape(16.dp),
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
