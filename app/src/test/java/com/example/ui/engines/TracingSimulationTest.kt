package com.example.ui.engines

import androidx.compose.ui.geometry.Offset
import org.junit.Assert.*
import org.junit.Test

class TracingSimulationTest {
    private val guide = (0..10).map { Offset(it * 20f, 0f) }
    private fun evaluate(points: List<Offset>) =
        TracingStudioDeckRegistry.evaluateStrokeAttempt(
            TracingStrokeSpec(guide), guide, listOf(points), 12f, .55f, true
        )

    @Test fun correctTracePasses() {
        assertTrue(evaluate(guide).strokePassed)
    }
    @Test fun parallelOutsideTraceFails() {
        val result = evaluate(guide.map { it + Offset(0f, 25f) })
        assertFalse(result.strokePassed)
        assertTrue(result.accuracyPercent < 100)
    }
    @Test fun halfTraceFails() {
        assertFalse(evaluate(guide.take(6)).strokePassed)
    }
    @Test fun reverseTraceFails() {
        assertFalse(evaluate(guide.reversed()).strokePassed)
    }
    @Test fun stationaryTapFails() {
        assertFalse(evaluate(List(30) { Offset.Zero }).strokePassed)
    }
    @Test fun scribbleFails() {
        assertFalse(evaluate(guide.flatMap { listOf(it, it + Offset(0f, 100f), it) }).strokePassed)
    }

    @Test fun isolatedCheckpointTapsDoNotPass() {
        val result = TracingStudioDeckRegistry.evaluateStrokeAttempt(
            TracingStrokeSpec(guide), guide, guide.map { listOf(it) }, 12f, .55f, true)
        assertFalse(result.strokePassed)
        assertEquals(0, result.accuracyPercent)
    }
    @Test fun exactTracesPassAcrossAllDecksAndCanvasSizes() {
        for (height in listOf(180f, 300f, 480f)) {
            for (deck in 1..12) {
                TracingStudioDeckRegistry.getDeckForActivity("w04_tracing_$deck").forEach { glyph ->
                    glyph.strokes.forEachIndexed { index, stroke ->
                        val points = stroke.points.map {
                            TracingStudioDeckRegistry.mapNormalizedToCanvas(it, height * 1.6f, height)
                        }
                        val result = TracingStudioDeckRegistry.evaluateStrokeAttempt(
                            stroke, points, listOf(points), 12f, .55f, true
                        )
                        assertTrue("Deck $deck, ${glyph.glyph}, stroke $index, height $height", result.strokePassed)
                    }
                }
            }
        }
    }

    @Test fun distanceUsesContinuousGuide() {
        assertEquals(3f, TracingStudioDeckRegistry.distanceToGuide(
            Offset(10f, 3f), listOf(Offset.Zero, Offset(20f, 0f))), .001f)
    }
}
