package mobappdev.example.nback_cimpl.domain

import kotlin.random.Random

/**
 * Domain class responsible for generating N-Back visual and auditory sequences.
 * Key Business Constraints:
 * 1. Guarantee that the first n events can NEVER be matches (since no stimulus exists n steps prior).
 * 2. Enforce a target match rate of approximately 20% - 40%.
 */
class NBackSequenceGenerator(
    private val random: Random = Random.Default
) {
    /** Phonetically distinct set of letters for auditory stimuli */
    val defaultLetters = listOf("C", "D", "E", "H", "K", "L", "O", "Q", "R", "S", "T", "W", "Z")

    /**
     * Generates a visual N-Back sequence of grid cell positions (0..numCells-1).
     *
     * @param size Total number of events in the round.
     * @param numCells Total available grid cells (e.g. 9 for 3x3).
     * @param matchPercentage Target match percentage (~20-40%).
     * @param nBack N-back parameter value.
     * @return Array of grid position indices for each event tick.
     */
    fun generateVisualSequence(
        size: Int,
        numCells: Int,
        matchPercentage: Int = 30,
        nBack: Int
    ): IntArray {
        val result = IntArray(size) { -1 }

        // Calculate target number of matches (only valid for events starting at index nBack)
        val targetMatches = (((size - nBack) * matchPercentage) / 100).coerceAtLeast(1)

        // Randomly select distinct event indices >= nBack that will contain forced matches
        val possibleMatchIndices = (nBack until size).shuffled(random).take(targetMatches).toSet()

        // Phase 1: Initialize the first n events as non-matching initial stimuli
        for (i in 0 until minOf(nBack, size)) {
            var valCandidate: Int
            do {
                valCandidate = random.nextInt(numCells)
            } while (i >= nBack && valCandidate == result[i - nBack])
            result[i] = valCandidate
        }

        // Phase 2: Fill remaining events (indices >= nBack)
        for (i in nBack until size) {
            if (possibleMatchIndices.contains(i)) {
                // Force a match: copy the value from n steps earlier
                result[i] = result[i - nBack]
            } else {
                // Force a non-match: choose a candidate different from index i - nBack
                var valCandidate: Int
                do {
                    valCandidate = random.nextInt(numCells)
                } while (numCells > 1 && valCandidate == result[i - nBack])
                result[i] = valCandidate
            }
        }

        return result
    }

    /**
     * Generates an auditory N-Back sequence of spoken letters.
     *
     * @param size Total number of events in the round.
     * @param letters List of available distinct spoken letters.
     * @param matchPercentage Target match percentage (~20-40%).
     * @param nBack N-back parameter value.
     * @return Array of letter strings for each event tick.
     */
    fun generateAuditorySequence(
        size: Int,
        letters: List<String> = defaultLetters,
        matchPercentage: Int = 30,
        nBack: Int
    ): Array<String> {
        val result = Array(size) { "" }
        val numLetters = letters.size
        val targetMatches = (((size - nBack) * matchPercentage) / 100).coerceAtLeast(1)
        val possibleMatchIndices = (nBack until size).shuffled(random).take(targetMatches).toSet()

        // Phase 1: Initialize first n events
        for (i in 0 until minOf(nBack, size)) {
            var letterCandidate: String
            do {
                letterCandidate = letters[random.nextInt(numLetters)]
            } while (i >= nBack && letterCandidate == result[i - nBack])
            result[i] = letterCandidate
        }

        // Phase 2: Fill remaining events with forced matches and non-matches
        for (i in nBack until size) {
            if (possibleMatchIndices.contains(i)) {
                result[i] = result[i - nBack]
            } else {
                var letterCandidate: String
                do {
                    letterCandidate = letters[random.nextInt(numLetters)]
                } while (numLetters > 1 && letterCandidate == result[i - nBack])
                result[i] = letterCandidate
            }
        }

        return result
    }
}
