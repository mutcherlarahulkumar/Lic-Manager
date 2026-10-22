package `in`.bliss.bma.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// A calm, professional palette for a finance/insurance tool — deliberately
// different from the original app. One teal brand accent, generous neutrals.
private val BrandTeal = Color(0xFF0F766E)
private val BrandTealDark = Color(0xFF115E59)
private val Amber = Color(0xFFB45309) // used for "attention" (due/lapsed)

private val Light = lightColorScheme(
    primary = BrandTeal,
    secondary = BrandTealDark,
    tertiary = Amber,
)
private val Dark = darkColorScheme(
    primary = Color(0xFF5EEAD4),
    secondary = Color(0xFF99F6E4),
    tertiary = Color(0xFFFBBF24),
)

@Composable
fun BmaTheme(dark: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if (dark) Dark else Light, typography = BmaTypography, content = content)
}
