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
 * Main Activity host for the application.
 * Manages global dynamic theme selection (Light, Dark, System) and display scaling (Normal, Large, XL).
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Instantiate the ViewModel using custom factory to inject dependencies
            val gameViewModel: GameVM = viewModel(
                factory = GameVM.Factory
            )
            // Collect the game state reactively
            val gameState by gameViewModel.gameState.collectAsState()

            // Determine effective theme based on user settings
            val darkTheme = when (gameState.darkTheme) {
                "Light" -> false
                "Dark" -> true
                else -> isSystemInDarkTheme()
            }

            // Calculate font scale multiplier based on display scaling settings
            val fontScale = when (gameState.displayScale) {
                "XL" -> 1.4f
                "Large" -> 1.2f
                else -> 1.0f
            }

            NBack_CImplTheme(darkTheme = darkTheme) {
                // Apply global font scaling via CompositionLocalProvider
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
                        // Host screen navigation
                        AppNavigation(vm = gameViewModel)
                    }
                }
            }
        }
    }
}
