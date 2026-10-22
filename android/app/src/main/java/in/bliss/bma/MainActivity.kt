package `in`.bliss.bma

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import `in`.bliss.bma.ui.nav.AppNav
import `in`.bliss.bma.ui.theme.BmaTheme

/** Single-activity host; all UI is Compose. */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { BmaTheme { AppNav() } }
    }
}
