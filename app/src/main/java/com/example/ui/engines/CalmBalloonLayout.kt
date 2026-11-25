package com.example.ui.engines

/** Geometry shared by rendering and regression tests. Inputs include maximum hint scale. */
object CalmBalloonLayout {
    fun centers(width: Float, height: Float, count: Int, itemWidth: Float,
        itemHeight: Float, gap: Float): List<Pair<Float, Float>> {
        if (count <= 0) return emptyList()
        val columns = ((width + gap) / (itemWidth + gap)).toInt().coerceIn(1, count)
        val rows = (count + columns - 1) / columns
        return (0 until count).map { i ->
            val row = i / columns
            val inRow = minOf(columns, count - row * columns)
            val start = (width - inRow * itemWidth - (inRow - 1) * gap) / 2f
            val top = (height - rows * itemHeight - (rows - 1) * gap) / 2f
            Pair(start + (i % columns) * (itemWidth + gap) + itemWidth / 2f,
                top + row * (itemHeight + gap) + itemHeight / 2f)
        }
    }
}
