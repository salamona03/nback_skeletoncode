package mobappdev.example.nback_cimpl.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import mobappdev.example.nback_cimpl.data.db.ScoreEntity
import java.io.IOException

/**
 * Repository interacting with DataStore Preferences for game settings, app theme/display settings, high score, and score history.
 */
class UserPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) {
    private companion object {
        val HIGHSCORE = intPreferencesKey("highscore")
        val N_BACK = intPreferencesKey("n_back")
        val EVENT_DURATION_MS = longPreferencesKey("event_duration_ms")
        val EVENTS_PER_ROUND = intPreferencesKey("events_per_round")
        val GRID_SIZE = intPreferencesKey("grid_size")
        val GAME_TYPE = stringPreferencesKey("game_type")
        val PLAYER_NAME = stringPreferencesKey("player_name")
        val DARK_THEME = stringPreferencesKey("dark_theme")
        val DISPLAY_SCALE = stringPreferencesKey("display_scale")
        val SCORES_JSON = stringPreferencesKey("scores_json")
        const val TAG = "UserPreferencesRepo"
    }

    private val gson = Gson()

    val highscore: Flow<Int> = dataStore.data
        .catch { handleException(it) }
        .map { preferences -> preferences[HIGHSCORE] ?: 0 }

    val nBack: Flow<Int> = dataStore.data
        .catch { handleException(it) }
        .map { preferences -> preferences[N_BACK] ?: 2 }

    val eventDurationMs: Flow<Long> = dataStore.data
        .catch { handleException(it) }
        .map { preferences -> preferences[EVENT_DURATION_MS] ?: 2000L }

    val eventsPerRound: Flow<Int> = dataStore.data
        .catch { handleException(it) }
        .map { preferences -> preferences[EVENTS_PER_ROUND] ?: 15 }

    val gridSize: Flow<Int> = dataStore.data
        .catch { handleException(it) }
        .map { preferences -> preferences[GRID_SIZE] ?: 3 }

    val gameType: Flow<String> = dataStore.data
        .catch { handleException(it) }
        .map { preferences -> preferences[GAME_TYPE] ?: "Visual" }

    val playerName: Flow<String> = dataStore.data
        .catch { handleException(it) }
        .map { preferences -> preferences[PLAYER_NAME] ?: "Player" }

    val darkTheme: Flow<String> = dataStore.data
        .catch { handleException(it) }
        .map { preferences -> preferences[DARK_THEME] ?: "System" }

    val displayScale: Flow<String> = dataStore.data
        .catch { handleException(it) }
        .map { preferences -> preferences[DISPLAY_SCALE] ?: "Normal" }

    val scores: Flow<List<ScoreEntity>> = dataStore.data
        .catch { handleException(it) }
        .map { preferences ->
            val json = preferences[SCORES_JSON]
            if (json.isNullOrBlank()) {
                emptyList()
            } else {
                try {
                    val type = object : TypeToken<List<ScoreEntity>>() {}.type
                    gson.fromJson<List<ScoreEntity>>(json, type) ?: emptyList()
                } catch (_: Exception) {
                    emptyList()
                }
            }
        }

    private fun handleException(throwable: Throwable) {
        if (throwable is IOException) {
            Log.e(TAG, "Error reading preferences", throwable)
        } else {
            throw throwable
        }
    }

    suspend fun saveHighScore(score: Int) {
        dataStore.edit { preferences ->
            val currentHigh = preferences[HIGHSCORE] ?: 0
            if (score > currentHigh) {
                preferences[HIGHSCORE] = score
            }
        }
    }

    suspend fun saveSettings(
        nBack: Int,
        eventDurationMs: Long,
        eventsPerRound: Int,
        gridSize: Int,
        gameType: String,
        playerName: String,
        darkTheme: String,
        displayScale: String
    ) {
        dataStore.edit { preferences ->
            preferences[N_BACK] = nBack
            preferences[EVENT_DURATION_MS] = eventDurationMs
            preferences[EVENTS_PER_ROUND] = eventsPerRound
            preferences[GRID_SIZE] = gridSize
            preferences[GAME_TYPE] = gameType
            preferences[PLAYER_NAME] = playerName
            preferences[DARK_THEME] = darkTheme
            preferences[DISPLAY_SCALE] = displayScale
        }
    }

    suspend fun insertScore(score: ScoreEntity) {
        dataStore.edit { preferences ->
            val json = preferences[SCORES_JSON]
            val type = object : TypeToken<List<ScoreEntity>>() {}.type
            val currentList: MutableList<ScoreEntity> = if (json.isNullOrBlank()) {
                mutableListOf()
            } else {
                try {
                    gson.fromJson<List<ScoreEntity>>(json, type)?.toMutableList() ?: mutableListOf()
                } catch (_: Exception) {
                    mutableListOf()
                }
            }
            currentList.add(score)
            preferences[SCORES_JSON] = gson.toJson(currentList)
        }
    }
}
