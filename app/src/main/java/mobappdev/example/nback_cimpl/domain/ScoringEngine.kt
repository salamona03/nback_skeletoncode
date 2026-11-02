package mobappdev.example.nback_cimpl.domain

/**
 * Domain class responsible for N-Back scoring logic and input validation.
 */
class ScoringEngine {

    /**
     * Checks if a match press is allowed for the given event index and N-back level.
     * Matches are ignored before round starts, after it ends, and during the first n events.
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
     * Calculates score from hits and false alarms.
     * Formula: (Hits * 10) - (False Alarms * 5), clamped at 0.
     */
    fun calculateScore(hits: Int, falseAlarms: Int): Int {
        val score = (hits * 10) - (falseAlarms * 5)
        return maxOf(0, score)
    }

    /**
     * Evaluates a visual match attempt.
     * Returns true if it was a true match (Hit), false if it was a False Alarm.
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
     * Evaluates an auditory match attempt.
     * Returns true if it was a true match (Hit), false if it was a False Alarm.
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
