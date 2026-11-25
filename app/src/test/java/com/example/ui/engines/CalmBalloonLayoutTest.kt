package com.example.ui.engines

import org.junit.Assert.*
import org.junit.Test
import kotlin.math.abs

class CalmBalloonLayoutTest {
    @Test fun scaledBalloonsRemainSeparatedAndInsidePhoneAndTabletViewports() {
        val w = 104f * 1.08f
        val h = 142f * 1.08f
        for ((width, height, count) in listOf(Triple(296f, 400f, 2), Triple(620f, 165f, 3),
            Triple(900f, 500f, 3), Triple(240f, 500f, 2))) {
            val centers = CalmBalloonLayout.centers(width, height, count, w, h, 24f)
            centers.forEach { (x, y) ->
                assertTrue(x - w / 2 >= 0); assertTrue(x + w / 2 <= width + .01f)
                assertTrue(y - h / 2 >= 0); assertTrue(y + h / 2 <= height + .01f)
            }
            centers.forEachIndexed { i, a -> centers.drop(i + 1).forEach { b ->
                assertTrue(abs(a.first - b.first) >= w + 23.99f || abs(a.second - b.second) >= h + 23.99f)
            } }
        }
    }
}
