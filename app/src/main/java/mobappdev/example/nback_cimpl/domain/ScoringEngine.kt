package mobappdev.example.nback_cimpl.domain

/**
 * Domain class responsible for N-Back scoring calculations, validation guards, and match evaluation.
 */
class ScoringEngine {

    /**
     * Checks if a match press attempt is valid based on game state.
     * Enforces guards:
     * - Disallowed if the game is not running (before start or after finish).
     * - Disallowed if out of event bounds.
     * - Disallowed during the first n events (indices < nBack).
     */
    fun isPressAllowed(
        currentIndex: Int,
        totalEvents: Int,
        nBack: Int,
        isGameRunning: Boolean
    ): Boolean {
        if (!isGameRunning) return false
        if (currentIndex < 0 || currentIndex >= totalEvents) return false
        if (currentIndex < nBack) return false
        return true
    }

    /**
     * Calculates the game score based on correct hits and false alarm errors.
     * Formula: (Hits * 10) - (False Alarms * 5), clamped at a minimum floor of 0.
     */
    fun calculateScore(hits: Int, falseAlarms: Int): Int {
        val score = (hits * 10) - (falseAlarms * 5)
        return maxOf(0, score)
    }

    /**
     * Evaluates whether a visual match tap was a true match (Hit) or error (False Alarm).
     * Returns true if sequence[currentIndex] == sequence[currentIndex - nBack].
     */
    fun evaluateVisualPress(
        sequence: IntArray,
        currentIndex: Int,
        nBack: Int
    ): Boolean {
        if (currentIndex < nBack || currentIndex >= sequence.size) return false
        return sequence[currentIndex] == sequence[currentIndex - nBack]
    }

    /**
     * Evaluates whether an auditory match tap was a true match (Hit) or error (False Alarm).
     * Returns true if sequence[currentIndex] == sequence[currentIndex - nBack].
     */
    fun evaluateAudioPress(
        sequence: Array<String>,
        currentIndex: Int,
        nBack: Int
    ): Boolean {
        if (currentIndex < nBack || currentIndex >= sequence.size) return false
        return sequence[currentIndex] == sequence[currentIndex - nBack]
    }
}
