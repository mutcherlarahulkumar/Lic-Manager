package `in`.bliss.bma.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import `in`.bliss.bma.ui.theme.Dp as D

/** Coarse width buckets for adaptive layout, derived without extra deps. */
enum class WidthClass { Compact, Medium, Expanded }

@Composable
@ReadOnlyComposable
fun widthClass(): WidthClass {
    val w = LocalConfiguration.current.screenWidthDp
    return when {
        w < 600 -> WidthClass.Compact
        w < 840 -> WidthClass.Medium
        else -> WidthClass.Expanded
    }
}

/** Grid column count that scales with screen width. */
@Composable
fun adaptiveColumns(compact: Int = 2, medium: Int = 3, expanded: Int = 4): Int =
    when (widthClass()) {
        WidthClass.Compact -> compact
        WidthClass.Medium -> medium
        WidthClass.Expanded -> expanded
    }

/** Side padding that grows a little on wider screens. */
@Composable
fun gutter(): Dp = if (widthClass() == WidthClass.Compact) D.lg else D.xl

/**
 * Centers page content and caps its width on large screens so line length and
 * card size stay comfortable. Wrap every screen body in this.
 */
@Composable
fun ResponsiveContainer(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        Box(
            Modifier
                .widthIn(max = D.contentMax)
                .fillMaxWidth()
                .padding(horizontal = gutter()),
            content = content,
        )
    }
}
