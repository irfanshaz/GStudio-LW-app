package com.example

import androidx.compose.ui.geometry.Offset
import com.example.ui.engines.TracingStudioDeckRegistry
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ToddlerTrailTest {
    private val template = TracingStudioDeckRegistry.getDeckForActivity("w04_tracing_9")
        .first().strokes.first()
    private val guide = (0..10).map { Offset(it * 30f, 0f) }

    @Test fun anOffCenterDragPassesTheWideTrailButNotTheDottedGuide() {
        val drag = guide.map { Offset(it.x, 20f) }
        assertTrue(evaluate(listOf(drag), wide = true).strokePassed)
        assertFalse(evaluate(listOf(drag), wide = false).strokePassed)
    }

    @Test fun aWideTrailStillRequiresActualTravel() {
        assertFalse(evaluate(guide.map(::listOf), wide = true).strokePassed)
        assertFalse(evaluate(listOf(guide.map { Offset(it.x, 80f) }), wide = true).strokePassed)
        assertFalse(evaluate(listOf(guide.reversed()), wide = true).strokePassed)
    }

    private fun evaluate(segments: List<List<Offset>>, wide: Boolean) =
        TracingStudioDeckRegistry.evaluateStrokeAttempt(
            strokeSpec = template,
            checkpointPixels = guide,
            rawSegments = segments,
            tolerancePx = if (wide) 28f else 12f,
            requiredCoverageRatio = 0.66f,
            isToddlerBand = wide
        )
}
