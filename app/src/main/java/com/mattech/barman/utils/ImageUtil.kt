package com.mattech.barman.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.ExifInterface
import java.io.File
import java.io.FileOutputStream
import kotlin.math.roundToInt

fun Drawable.toBitmap(): Bitmap {
    if (this is BitmapDrawable) {
        return this.bitmap
    }

    val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    setBounds(0, 0, canvas.width, canvas.height)
    draw(canvas)

    return bitmap
}

enum class Resolution(val size: Int) {
    LOW(320),
    MEDIUM(640),
    HIGH(1024)
}

class ImageUtil {
    companion object {
        private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
            val (height: Int, width: Int) = options.run { outHeight to outWidth }
            var inSampleSize = 1

            if (height > reqHeight || width > reqWidth) {
                val heightRatio = (height.toFloat() / reqHeight.toFloat()).roundToInt()
                val widthRatio = (width.toFloat() / reqWidth.toFloat()).roundToInt()

                inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
                val currentPixelCount = height * width
                val maxPixelCount = reqHeight * reqWidth * 2

                // Calculate the largest inSampleSize value that is a power of 2 and keeps both
                // height and width larger than the requested height and width.
                while (currentPixelCount / (inSampleSize * inSampleSize) > maxPixelCount) {
                    inSampleSize *= 2
                }
            }
            return inSampleSize
        }

        private fun rotateImage(image: Bitmap, degree: Int): Bitmap {
            val matrix = Matrix()
            matrix.postRotate(degree.toFloat())
            val rotatedImage = Bitmap.createBitmap(image, 0, 0, image.width, image.height, matrix, true)
            image.recycle()
            return rotatedImage
        }

        private fun rotateImageIfRequired(image: Bitmap, photoPath: String): Bitmap {
            val exif = ExifInterface(photoPath)
            val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
            return when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(image, 90)
                ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(image, 180)
                ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(image, 270)
                else -> image
            }
        }

        fun handleSamplingAndRotation(photoPath: String, resolution: Resolution) {
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeFile(File(photoPath).absolutePath, options)
            options.apply {
                inSampleSize = calculateInSampleSize(options, resolution.size, resolution.size)
                inJustDecodeBounds = false
            }

            getBitmap(photoPath, options)?.let {
                val result = rotateImageIfRequired(it, photoPath)
                val fileOutputStream = FileOutputStream(photoPath)
                result.compress(Bitmap.CompressFormat.WEBP, 80, fileOutputStream)
                fileOutputStream.flush()
                fileOutputStream.close()
            }
        }

        fun getBitmap(photoPath: String, options: BitmapFactory.Options? = BitmapFactory.Options()): Bitmap? {
            val imageFile = File(photoPath)
            return if (imageFile.exists()) {
                BitmapFactory.decodeFile(imageFile.absolutePath, options)
            } else {
                null
            }
        }
    }
}