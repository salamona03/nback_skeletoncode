package mobappdev.example.nback_cimpl.domain

import kotlin.random.Random

/**
 * Domain class responsible for generating N-Back sequences.
 * Enforces:
 * 1. First n events can NEVER be matches.
 * 2. Match rate is ~20% - 40%.
 */
class NBackSequenceGenerator(
    private val random: Random = Random.Default
) {
    val defaultLetters = listOf("C", "D", "E", "H", "K", "L", "O", "Q", "R", "S", "T", "W", "Z")

    /**
     * Generates a visual N-Back sequence (0..numCells-1).
     * @param size total number of events in the round.
     * @param numCells total grid cells available (e.g. 9 for 3x3).
     * @param matchPercentage target match percentage (~20-40%).
     * @param nBack N-back parameter value.
     */
    fun generateVisualSequence(
        size: Int,
        numCells: Int,
        matchPercentage: Int = 30,
        nBack: Int
    ): IntArray {
        val result = IntArray(size) { -1 }

        // Calculate target number of matches
        val targetMatches = (((size - nBack) * matchPercentage) / 100).coerceAtLeast(1)

        // Select distinct indices >= nBack for matches
        val possibleMatchIndices = (nBack until size).shuffled(random).take(targetMatches).toSet()

        // 1. First n events are non-matching initial stimuli
        for (i in 0 until minOf(nBack, size)) {
            var valCandidate: Int
            do {
                valCandidate = random.nextInt(numCells)
            } while (i >= nBack && valCandidate == result[i - nBack])
            result[i] = valCandidate
        }

        // 2. Fill remaining events
        for (i in nBack until size) {
            if (possibleMatchIndices.contains(i)) {
                // Force a match with index i - nBack
                result[i] = result[i - nBack]
            } else {
                // Ensure NO match with index i - nBack
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

        for (i in 0 until minOf(nBack, size)) {
            var letterCandidate: String
            do {
                letterCandidate = letters[random.nextInt(numLetters)]
            } while (i >= nBack && letterCandidate == result[i - nBack])
            result[i] = letterCandidate
        }

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
