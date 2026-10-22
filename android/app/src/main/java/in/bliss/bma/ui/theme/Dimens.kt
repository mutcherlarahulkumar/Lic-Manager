package `in`.bliss.bma.ui.theme

import androidx.compose.ui.unit.dp

/**
 * One spacing scale for the whole app. Using named steps (not ad-hoc dp values)
 * is what keeps screens visually consistent. Always space with these.
 */
object Dp {
    val xs = 4.dp
    val sm = 8.dp
    val md = 12.dp
    val lg = 16.dp
    val xl = 24.dp
    val xxl = 32.dp

    /** Max width for the main content column on tablets/large screens, so text
     *  and cards don't stretch to uncomfortable line lengths. */
    val contentMax = 840.dp

    /** Standard corner radius for cards/containers. */
    val radius = 16.dp
}
