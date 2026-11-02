package mobappdev.example.nback_cimpl.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import mobappdev.example.nback_cimpl.ui.screens.GameScreen
import mobappdev.example.nback_cimpl.ui.screens.GameSettingsScreen
import mobappdev.example.nback_cimpl.ui.screens.HomeScreen
import mobappdev.example.nback_cimpl.ui.screens.ScoresScreen
import mobappdev.example.nback_cimpl.ui.screens.ThemeSettingsScreen
import mobappdev.example.nback_cimpl.ui.screens.TutorialScreen
import mobappdev.example.nback_cimpl.ui.viewmodels.GameViewModel

enum class Screen {
    Home,
    Game,
    GameSettings,
    ThemeSettings,
    Scores,
    Tutorial
}

@Composable
fun AppNavigation(
    vm: GameViewModel,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.name
    ) {
        composable(Screen.Home.name) {
            HomeScreen(
                vm = vm,
                onNavigateToGame = { navController.navigate(Screen.Game.name) },
                onNavigateToGameSettings = { navController.navigate(Screen.GameSettings.name) },
                onNavigateToThemeSettings = { navController.navigate(Screen.ThemeSettings.name) },
                onNavigateToScores = { navController.navigate(Screen.Scores.name) },
                onNavigateToTutorial = { navController.navigate(Screen.Tutorial.name) }
            )
        }

        composable(Screen.Game.name) {
            GameScreen(
                vm = vm,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.GameSettings.name) {
            GameSettingsScreen(
                vm = vm,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.ThemeSettings.name) {
            ThemeSettingsScreen(
                vm = vm,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Scores.name) {
            ScoresScreen(
                vm = vm,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Tutorial.name) {
            TutorialScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
