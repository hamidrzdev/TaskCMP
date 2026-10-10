package ir.taskcmp.image

class ManualMean3x3Smoother {
    fun smooth(input: PixelImage): PixelImage {
        val output = IntArray(input.pixels.size)

        for (y in 0 until input.height) {
            for (x in 0 until input.width) {
                var total = 0

                for (dy in -1..1) {
                    for (dx in -1..1) {
                        if (dx == 0 && dy == 0) continue

                        val neighbourX = (x + dx).coerceIn(0, input.width - 1)
                        val neighbourY = (y + dy).coerceIn(0, input.height - 1)
                        val neighbourIndex = neighbourY * input.width + neighbourX
                        total += input.pixels[neighbourIndex] and 0xff
                    }
                }

                val grayAverage = total / 8
                val index = y * input.width + x
                val alpha = input.pixels[index] ushr 24
                output[index] = alpha shl 24 or
                    (grayAverage shl 16) or
                    (grayAverage shl 8) or
                    grayAverage
            }
        }

        return PixelImage(input.width, input.height, output)
    }
}
