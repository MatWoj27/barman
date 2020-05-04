package com.mattech.barman.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.util.Log
import java.io.File
import java.io.FileOutputStream

class ImageUtil {
    companion object {
        private fun rotateImage(image: Bitmap, degree: Int): Bitmap {
            val matrix = Matrix()
            matrix.postRotate(degree.toFloat())
            val rotatedImage = Bitmap.createBitmap(image, 0, 0, image.width, image.height, matrix, true)
            image.recycle()
            return rotatedImage
        }

        fun rotateImageIfRequired(photoPath: String) {
            val image = getBitmap(photoPath)
            if (image != null) {
                val exif = ExifInterface(photoPath)
                val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
                val result = when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(image, 90)
                    ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(image, 180)
                    ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(image, 270)
                    else -> return
                }
                val fileOutputStream = FileOutputStream(photoPath)
                result.compress(Bitmap.CompressFormat.WEBP, 80, fileOutputStream)
                fileOutputStream.flush()
                fileOutputStream.close()
            } else {
                Log.e("", "File with following path does not exist: $photoPath")
            }
        }

        fun getBitmap(photoPath: String): Bitmap? = if (photoPath.isNotEmpty()) {
            val imageFile = File(photoPath)
            if (imageFile.exists()) {
                BitmapFactory.decodeFile(imageFile.absolutePath)
            } else {
                null
            }
        } else {
            null
        }
    }
}