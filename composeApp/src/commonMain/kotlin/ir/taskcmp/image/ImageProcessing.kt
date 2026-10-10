package ir.taskcmp.image

/** Pixels are stored row by row as ARGB integers, independently of the UI toolkit. */
data class PixelImage(val width: Int, val height: Int, val pixels: IntArray) {
    init {
        require(width > 0 && height > 0)
        require(pixels.size.toLong() == width.toLong() * height)
    }
}

enum class SmoothMethod(val title: String, val description: String) {
    MEAN_3X3("میانگین‌گیر ۳×۳", "میانگین هر پیکسل و ۸ همسایهٔ آن")
}

object ImageProcessing {
    fun grayscale(input: PixelImage): PixelImage {
        val output = IntArray(input.pixels.size)
        for (index in input.pixels.indices) {
            val color = input.pixels[index]
            val alpha = color ushr 24
            val red = color ushr 16 and 0xff
            val green = color ushr 8 and 0xff
            val blue = color and 0xff
            val gray = (red + green + blue) / 3
            output[index] = alpha shl 24 or (gray shl 16) or (gray shl 8) or gray
        }
        return PixelImage(input.width, input.height, output)
    }

    fun smooth(input: PixelImage, method: SmoothMethod): PixelImage = when (method) {
        SmoothMethod.MEAN_3X3 -> mean3x3(input)
    }

    private fun mean3x3(input: PixelImage): PixelImage {
        val output = IntArray(input.pixels.size)
        for (y in 0 until input.height) {
            for (x in 0 until input.width) {
                var total = 0
                for (dy in -1..1) {
                    val sampleY = (y + dy).coerceIn(0, input.height - 1)
                    for (dx in -1..1) {
                        val sampleX = (x + dx).coerceIn(0, input.width - 1)
                        total += input.pixels[sampleY * input.width + sampleX] and 0xff
                    }
                }
                val gray = total / 9
                val index = y * input.width + x
                val alpha = input.pixels[index] ushr 24
                output[index] = alpha shl 24 or (gray shl 16) or (gray shl 8) or gray
            }
        }
        return PixelImage(input.width, input.height, output)
    }
}
