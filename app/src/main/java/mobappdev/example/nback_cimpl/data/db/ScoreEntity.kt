package mobappdev.example.nback_cimpl.data.db

/**
 * Data class representing a saved game score entry stored in DataStore JSON.
 */
data class ScoreEntity(
    val id: Long = System.currentTimeMillis(),
    val playerName: String,
    val score: Int,
    val nBack: Int,
    val gameType: String,
    val hits: Int,
    val falseAlarms: Int,
    val totalEvents: Int,
    val timestamp: Long = System.currentTimeMillis()
)
