package mobappdev.example.nback_cimpl

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.lifecycle.viewmodel.compose.viewModel
import mobappdev.example.nback_cimpl.ui.navigation.AppNavigation
import mobappdev.example.nback_cimpl.ui.theme.NBack_CImplTheme
import mobappdev.example.nback_cimpl.ui.viewmodels.GameVM

/**
 * MainActivity host for the N-Back application navigation with dynamic theme and multi-tier display scaling (Normal, Large, XL).
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val gameViewModel: GameVM = viewModel(
                factory = GameVM.Factory
            )
            val gameState by gameViewModel.gameState.collectAsState()

            val darkTheme = when (gameState.darkTheme) {
                "Light" -> false
                "Dark" -> true
                else -> isSystemInDarkTheme()
            }

            // Dynamic font scale for Normal, Large, and XL Display Modes
            val fontScale = when (gameState.displayScale) {
                "XL" -> 1.4f
                "Large" -> 1.2f
                else -> 1.0f
            }

            NBack_CImplTheme(darkTheme = darkTheme) {
                CompositionLocalProvider(
                    LocalDensity provides Density(
                        density = LocalDensity.current.density,
                        fontScale = fontScale
                    )
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        AppNavigation(vm = gameViewModel)
                    }
                }
            }
        }
    }
}
