package com.mattech.barman.utils

import android.graphics.*
import com.squareup.picasso.Transformation
import kotlin.math.min

class CircleTransformation : Transformation {

    override fun key() = "circle"

    override fun transform(source: Bitmap?): Bitmap {
        val size = min(source!!.width, source.height)
        val x = (source.width - size) / 2
        val y = (source.height - size) / 2
        val squareBitmap = Bitmap.createBitmap(source, x, y, size, size)

        if (squareBitmap != source) {
            source.recycle()
        }

        val result = Bitmap.createBitmap(size, size, source.config)
        val canvas = Canvas(result)
        val paint = Paint()
        val bitmapShader = BitmapShader(squareBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)

        paint.shader = bitmapShader
        paint.isAntiAlias = true

        val r: Float = size / 2f
        canvas.drawCircle(r, r, r, paint)

        squareBitmap.recycle()
        return result
    }
}