package mobappdev.example.nback_cimpl

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import mobappdev.example.nback_cimpl.data.UserPreferencesRepository

// Name of the DataStore preferences file
private const val APP_PREFERENCES_NAME = "game_preferences"

// Delegate property providing global access to the DataStore preferences instance
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = APP_PREFERENCES_NAME
)

/**
 * Custom Application class serving as the dependency injection entry point.
 * Initializes the UserPreferencesRepository for data persistence.
 */
class GameApplication : Application() {
    /**
     * Singleton instance of UserPreferencesRepository available application-wide.
     */
    lateinit var userPreferencesRepository: UserPreferencesRepository

    override fun onCreate() {
        super.onCreate()
        // Instantiate repository with the DataStore delegate property
        userPreferencesRepository = UserPreferencesRepository(dataStore)
    }
}
