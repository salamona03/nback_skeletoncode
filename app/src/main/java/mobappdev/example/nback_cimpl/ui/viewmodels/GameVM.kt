package mobappdev.example.nback_cimpl.ui.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import mobappdev.example.nback_cimpl.GameApplication
import mobappdev.example.nback_cimpl.data.UserPreferencesRepository
import mobappdev.example.nback_cimpl.data.db.ScoreEntity
import mobappdev.example.nback_cimpl.domain.NBackSequenceGenerator
import mobappdev.example.nback_cimpl.domain.ScoringEngine
import mobappdev.example.nback_cimpl.util.TextToSpeechHelper

enum class GameType {
    Visual,
    Audio,
    AudioVisual
}

enum class GameStatus {
    IDLE,
    RUNNING,
    FINISHED
}

enum class ScoreSortOrder {
    SCORE_DESC,
    NAME_ASC,
    DATE_DESC
}

data class GameState(
    val status: GameStatus = GameStatus.IDLE,
    val gameType: GameType = GameType.Visual,
    val nBack: Int = 2,
    val eventDurationMs: Long = 2000L,
    val eventsPerRound: Int = 15,
    val gridSize: Int = 3,
    val currentEventIndex: Int = -1,
    val totalEvents: Int = 15,
    val currentVisualValue: Int = -1,
    val currentAudioLetter: String = "",
    val score: Int = 0,
    val hits: Int = 0,
    val falseAlarms: Int = 0,
    val misses: Int = 0,
    val visualMatchPressed: Boolean = false,
    val audioMatchPressed: Boolean = false,
    val visualFeedbackError: Boolean = false,
    val audioFeedbackError: Boolean = false,
    val playerName: String = "Player",
    val darkTheme: String = "System",
    val displayScale: String = "Normal"
)

interface GameViewModel {
    val gameState: StateFlow<GameState>
    val score: StateFlow<Int>
    val highscore: StateFlow<Int>
    val nBack: Int
    val scoresList: StateFlow<List<ScoreEntity>>

    fun setGameType(gameType: GameType)
    fun startGame()
    fun stopGame()
    fun checkMatch()
    fun checkVisualMatch()
    fun checkAudioMatch()
    fun updateSettings(
        nBack: Int,
        eventDurationMs: Long,
        eventsPerRound: Int,
        gridSize: Int,
        gameType: GameType,
        playerName: String,
        darkTheme: String,
        displayScale: String
    )
    fun saveCurrentScore()
    fun setScoreSortOrder(order: ScoreSortOrder)
    fun clearFeedbackError()
}

class GameVM(
    private val userPreferencesRepository: UserPreferencesRepository,
    context: Context
) : GameViewModel, ViewModel() {

    private val _gameState = MutableStateFlow(GameState())
    override val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private val _score = MutableStateFlow(0)
    override val score: StateFlow<Int> = _score.asStateFlow()

    private val _highscore = MutableStateFlow(0)
    override val highscore: StateFlow<Int> = _highscore.asStateFlow()

    private val _scoresList = MutableStateFlow<List<ScoreEntity>>(emptyList())
    override val scoresList: StateFlow<List<ScoreEntity>> = _scoresList.asStateFlow()

    override val nBack: Int
        get() = gameState.value.nBack

    private var gameJob: Job? = null
    private var visualSequence = IntArray(0)
    private var audioSequence = emptyArray<String>()

    private val sequenceGenerator = NBackSequenceGenerator()
    private val scoringEngine = ScoringEngine()
    private var ttsHelper: TextToSpeechHelper? = TextToSpeechHelper(context)

    private var currentSortOrder = ScoreSortOrder.SCORE_DESC
    private var sortCollectJob: Job? = null

    init {
        viewModelScope.launch {
            userPreferencesRepository.highscore.collect { _highscore.value = it }
        }
        viewModelScope.launch {
            userPreferencesRepository.nBack.collect { n ->
                _gameState.value = _gameState.value.copy(nBack = n)
            }
        }
        viewModelScope.launch {
            userPreferencesRepository.eventDurationMs.collect { duration ->
                _gameState.value = _gameState.value.copy(eventDurationMs = duration)
            }
        }
        viewModelScope.launch {
            userPreferencesRepository.eventsPerRound.collect { events ->
                _gameState.value = _gameState.value.copy(eventsPerRound = events)
            }
        }
        viewModelScope.launch {
            userPreferencesRepository.gridSize.collect { size ->
                _gameState.value = _gameState.value.copy(gridSize = size)
            }
        }
        viewModelScope.launch {
            userPreferencesRepository.gameType.collect { typeStr ->
                val type = try { GameType.valueOf(typeStr) } catch (e: Exception) { GameType.Visual }
                _gameState.value = _gameState.value.copy(gameType = type)
            }
        }
        viewModelScope.launch {
            userPreferencesRepository.playerName.collect { name ->
                _gameState.value = _gameState.value.copy(playerName = name)
            }
        }
        viewModelScope.launch {
            userPreferencesRepository.darkTheme.collect { theme ->
                _gameState.value = _gameState.value.copy(darkTheme = theme)
            }
        }
        viewModelScope.launch {
            userPreferencesRepository.displayScale.collect { scale ->
                _gameState.value = _gameState.value.copy(displayScale = scale)
            }
        }

        loadScores(ScoreSortOrder.SCORE_DESC)
    }

    private fun loadScores(order: ScoreSortOrder) {
        currentSortOrder = order
        sortCollectJob?.cancel()
        sortCollectJob = viewModelScope.launch {
            userPreferencesRepository.scores.collectLatest { rawList ->
                val sorted = when (order) {
                    ScoreSortOrder.SCORE_DESC -> rawList.sortedByDescending { it.score }
                    ScoreSortOrder.NAME_ASC -> rawList.sortedBy { it.playerName.lowercase() }
                    ScoreSortOrder.DATE_DESC -> rawList.sortedByDescending { it.timestamp }
                }
                _scoresList.value = sorted
            }
        }
    }

    override fun setGameType(gameType: GameType) {
        _gameState.value = _gameState.value.copy(gameType = gameType)
        viewModelScope.launch {
            userPreferencesRepository.saveSettings(
                nBack = gameState.value.nBack,
                eventDurationMs = gameState.value.eventDurationMs,
                eventsPerRound = gameState.value.eventsPerRound,
                gridSize = gameState.value.gridSize,
                gameType = gameType.name,
                playerName = gameState.value.playerName,
                darkTheme = gameState.value.darkTheme,
                displayScale = gameState.value.displayScale
            )
        }
    }

    override fun startGame() {
        stopGame()

        val state = gameState.value
        val totalEvents = state.eventsPerRound
        val nBackVal = state.nBack
        val gridSizeVal = state.gridSize
        val numCells = gridSizeVal * gridSizeVal

        visualSequence = sequenceGenerator.generateVisualSequence(
            size = totalEvents,
            numCells = numCells,
            matchPercentage = 30,
            nBack = nBackVal
        )

        audioSequence = sequenceGenerator.generateAuditorySequence(
            size = totalEvents,
            matchPercentage = 30,
            nBack = nBackVal
        )

        _score.value = 0
        _gameState.value = state.copy(
            status = GameStatus.RUNNING,
            currentEventIndex = -1,
            totalEvents = totalEvents,
            score = 0,
            hits = 0,
            falseAlarms = 0,
            misses = 0,
            visualMatchPressed = false,
            audioMatchPressed = false,
            visualFeedbackError = false,
            audioFeedbackError = false
        )

        gameJob = viewModelScope.launch {
            for (i in 0 until totalEvents) {
                val currentVisual = if (state.gameType == GameType.Audio) -1 else visualSequence[i]
                val currentAudio = if (state.gameType == GameType.Visual) "" else audioSequence[i]

                _gameState.value = _gameState.value.copy(
                    currentEventIndex = i,
                    currentVisualValue = currentVisual,
                    currentAudioLetter = currentAudio,
                    visualMatchPressed = false,
                    audioMatchPressed = false,
                    visualFeedbackError = false,
                    audioFeedbackError = false
                )

                if (state.gameType == GameType.Audio || state.gameType == GameType.AudioVisual) {
                    if (currentAudio.isNotBlank()) {
                        ttsHelper?.speak(currentAudio)
                    }
                }

                delay(state.eventDurationMs)
            }

            val finalScore = _score.value
            _gameState.value = _gameState.value.copy(
                status = GameStatus.FINISHED,
                currentEventIndex = totalEvents,
                currentVisualValue = -1,
                currentAudioLetter = ""
            )

            userPreferencesRepository.saveHighScore(finalScore)
            saveCurrentScore()
        }
    }

    override fun stopGame() {
        gameJob?.cancel()
        gameJob = null
        ttsHelper?.stop()
        _gameState.value = _gameState.value.copy(
            status = GameStatus.IDLE,
            currentEventIndex = -1,
            currentVisualValue = -1,
            currentAudioLetter = ""
        )
    }

    override fun checkMatch() {
        if (gameState.value.gameType == GameType.Audio) {
            checkAudioMatch()
        } else {
            checkVisualMatch()
        }
    }

    override fun checkVisualMatch() {
        val state = gameState.value
        val currentIndex = state.currentEventIndex

        if (!scoringEngine.isPressAllowed(currentIndex, state.totalEvents, state.nBack, state.status == GameStatus.RUNNING)) {
            return
        }

        if (state.visualMatchPressed) return

        val isTrueMatch = scoringEngine.evaluateVisualPress(visualSequence, currentIndex, state.nBack)

        if (isTrueMatch) {
            val newHits = state.hits + 1
            val newScore = scoringEngine.calculateScore(newHits, state.falseAlarms)
            _score.value = newScore
            _gameState.value = state.copy(
                hits = newHits,
                score = newScore,
                visualMatchPressed = true,
                visualFeedbackError = false
            )
        } else {
            val newFalseAlarms = state.falseAlarms + 1
            val newScore = scoringEngine.calculateScore(state.hits, newFalseAlarms)
            _score.value = newScore
            _gameState.value = state.copy(
                falseAlarms = newFalseAlarms,
                score = newScore,
                visualMatchPressed = true,
                visualFeedbackError = true
            )
        }
    }

    override fun checkAudioMatch() {
        val state = gameState.value
        val currentIndex = state.currentEventIndex

        if (!scoringEngine.isPressAllowed(currentIndex, state.totalEvents, state.nBack, state.status == GameStatus.RUNNING)) {
            return
        }

        if (state.audioMatchPressed) return

        val isTrueMatch = scoringEngine.evaluateAudioPress(audioSequence, currentIndex, state.nBack)

        if (isTrueMatch) {
            val newHits = state.hits + 1
            val newScore = scoringEngine.calculateScore(newHits, state.falseAlarms)
            _score.value = newScore
            _gameState.value = state.copy(
                hits = newHits,
                score = newScore,
                audioMatchPressed = true,
                audioFeedbackError = false
            )
        } else {
            val newFalseAlarms = state.falseAlarms + 1
            val newScore = scoringEngine.calculateScore(state.hits, newFalseAlarms)
            _score.value = newScore
            _gameState.value = state.copy(
                falseAlarms = newFalseAlarms,
                score = newScore,
                audioMatchPressed = true,
                audioFeedbackError = true
            )
        }
    }

    override fun clearFeedbackError() {
        _gameState.value = _gameState.value.copy(
            visualFeedbackError = false,
            audioFeedbackError = false
        )
    }

    override fun updateSettings(
        nBack: Int,
        eventDurationMs: Long,
        eventsPerRound: Int,
        gridSize: Int,
        gameType: GameType,
        playerName: String,
        darkTheme: String,
        displayScale: String
    ) {
        _gameState.value = _gameState.value.copy(
            nBack = nBack,
            eventDurationMs = eventDurationMs,
            eventsPerRound = eventsPerRound,
            gridSize = gridSize,
            gameType = gameType,
            playerName = playerName,
            darkTheme = darkTheme,
            displayScale = displayScale
        )

        viewModelScope.launch {
            userPreferencesRepository.saveSettings(
                nBack = nBack,
                eventDurationMs = eventDurationMs,
                eventsPerRound = eventsPerRound,
                gridSize = gridSize,
                gameType = gameType.name,
                playerName = playerName,
                darkTheme = darkTheme,
                displayScale = displayScale
            )
        }
    }

    override fun saveCurrentScore() {
        val state = gameState.value
        viewModelScope.launch {
            userPreferencesRepository.insertScore(
                ScoreEntity(
                    playerName = state.playerName,
                    score = state.score,
                    nBack = state.nBack,
                    gameType = state.gameType.name,
                    hits = state.hits,
                    falseAlarms = state.falseAlarms,
                    totalEvents = state.totalEvents
                )
            )
        }
    }

    override fun setScoreSortOrder(order: ScoreSortOrder) {
        loadScores(order)
    }

    override fun onCleared() {
        super.onCleared()
        stopGame()
        ttsHelper?.shutdown()
        ttsHelper = null
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as GameApplication)
                GameVM(
                    userPreferencesRepository = application.userPreferencesRepository,
                    context = application.applicationContext
                )
            }
        }
    }
}

class FakeVM : GameViewModel {
    override val gameState: StateFlow<GameState> = MutableStateFlow(GameState()).asStateFlow()
    override val score: StateFlow<Int> = MutableStateFlow(20).asStateFlow()
    override val highscore: StateFlow<Int> = MutableStateFlow(42).asStateFlow()
    override val nBack: Int = 2
    override val scoresList: StateFlow<List<ScoreEntity>> = MutableStateFlow<List<ScoreEntity>>(emptyList()).asStateFlow()

    override fun setGameType(gameType: GameType) {}
    override fun startGame() {}
    override fun stopGame() {}
    override fun checkMatch() {}
    override fun checkVisualMatch() {}
    override fun checkAudioMatch() {}
    override fun updateSettings(
        nBack: Int,
        eventDurationMs: Long,
        eventsPerRound: Int,
        gridSize: Int,
        gameType: GameType,
        playerName: String,
        darkTheme: String,
        displayScale: String
    ) {}
    override fun saveCurrentScore() {}
    override fun setScoreSortOrder(order: ScoreSortOrder) {}
    override fun clearFeedbackError() {}
}
