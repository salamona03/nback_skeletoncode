package mobappdev.example.nback_cimpl

import mobappdev.example.nback_cimpl.domain.ScoringEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ScoringEngineTest {

    private lateinit var scoringEngine: ScoringEngine

    @Before
    fun setUp() {
        scoringEngine = ScoringEngine()
    }

    @Test
    fun testMatchPressGuards() {
        val totalEvents = 15
        val nBack = 2

        // Guard 1: Before game runs -> disallowed
        assertFalse(
            "Press when game is not running should be disallowed",
            scoringEngine.isPressAllowed(currentIndex = 3, totalEvents = totalEvents, nBack = nBack, isGameRunning = false)
        )

        // Guard 2: During first n events (indices 0 and 1 for 2-back) -> disallowed
        assertFalse(
            "Press during index 0 (first n events) should be disallowed",
            scoringEngine.isPressAllowed(currentIndex = 0, totalEvents = totalEvents, nBack = nBack, isGameRunning = true)
        )
        assertFalse(
            "Press during index 1 (first n events) should be disallowed",
            scoringEngine.isPressAllowed(currentIndex = 1, totalEvents = totalEvents, nBack = nBack, isGameRunning = true)
        )

        // Guard 3: At or after index nBack (index 2 for 2-back) -> allowed
        assertTrue(
            "Press at index 2 (after first n events) should be allowed",
            scoringEngine.isPressAllowed(currentIndex = 2, totalEvents = totalEvents, nBack = nBack, isGameRunning = true)
        )

        // Guard 4: Out of bounds index -> disallowed
        assertFalse(
            "Press at out-of-bounds index should be disallowed",
            scoringEngine.isPressAllowed(currentIndex = 15, totalEvents = totalEvents, nBack = nBack, isGameRunning = true)
        )
    }

    @Test
    fun testScoringFormulaAndNonNegativeFloor() {
        // Hits * 10 - FalseAlarms * 5
        assertEquals(20, scoringEngine.calculateScore(hits = 2, falseAlarms = 0))
        assertEquals(15, scoringEngine.calculateScore(hits = 2, falseAlarms = 1))

        // Non-negative floor test: 0 hits, 2 false alarms -> -10 -> clamped to 0
        assertEquals(0, scoringEngine.calculateScore(hits = 0, falseAlarms = 2))
    }

    @Test
    fun testVisualPressEvaluation() {
        val sequence = intArrayOf(1, 4, 1, 8, 5) // Matches at index 2 (sequence[2] == sequence[0] == 1)
        val nBack = 2

        // Index 2 is a match
        assertTrue(scoringEngine.evaluateVisualPress(sequence, currentIndex = 2, nBack = nBack))

        // Index 3 is NOT a match (sequence[3] = 8, sequence[1] = 4)
        assertFalse(scoringEngine.evaluateVisualPress(sequence, currentIndex = 3, nBack = nBack))
    }
}
