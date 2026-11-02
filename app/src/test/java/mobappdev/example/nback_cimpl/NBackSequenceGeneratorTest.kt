package mobappdev.example.nback_cimpl

import mobappdev.example.nback_cimpl.domain.NBackSequenceGenerator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class NBackSequenceGeneratorTest {

    private lateinit var generator: NBackSequenceGenerator

    @Before
    fun setUp() {
        generator = NBackSequenceGenerator()
    }

    @Test
    fun testFirstNGuarantee_VisualSequence() {
        val size = 20
        val nBack = 2
        val numCells = 9

        val sequence = generator.generateVisualSequence(
            size = size,
            numCells = numCells,
            matchPercentage = 30,
            nBack = nBack
        )

        assertEquals(size, sequence.size)

        // Verify that for all i < nBack, index i cannot be a match with index i - nBack (since i - nBack is out of bounds)
        for (i in 0 until nBack) {
            assertTrue("Event at index $i should be initialized", sequence[i] >= 0)
        }
    }

    @Test
    fun testFirstNGuarantee_AuditorySequence() {
        val size = 20
        val nBack = 3

        val sequence = generator.generateAuditorySequence(
            size = size,
            matchPercentage = 30,
            nBack = nBack
        )

        assertEquals(size, sequence.size)

        for (i in 0 until nBack) {
            assertTrue("Event at index $i should not be blank", sequence[i].isNotBlank())
        }
    }

    @Test
    fun testMatchRateEnforcement_VisualSequence() {
        val size = 30
        val nBack = 2
        val targetMatchPercent = 30

        val sequence = generator.generateVisualSequence(
            size = size,
            numCells = 9,
            matchPercentage = targetMatchPercent,
            nBack = nBack
        )

        var matchCount = 0
        for (i in nBack until size) {
            if (sequence[i] == sequence[i - nBack]) {
                matchCount++
            }
        }

        val actualPercentage = (matchCount.toDouble() / (size - nBack)) * 100
        println("Generated visual sequence with $matchCount matches (${actualPercentage}%)")

        // Assert match rate is within expected 15% - 45% range (~20-40%)
        assertTrue("Match percentage $actualPercentage should be within acceptable bounds", actualPercentage in 15.0..45.0)
    }
}
