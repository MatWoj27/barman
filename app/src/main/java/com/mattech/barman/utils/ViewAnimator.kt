package com.mattech.barman.utils

import android.R
import android.content.Context
import android.graphics.Bitmap
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import android.widget.ImageView


class ViewAnimator {
    companion object {
        fun animatedImageChange(context: Context, imageView: ImageView, image: Bitmap) {
            val outAnimation = AnimationUtils.loadAnimation(context, R.anim.fade_out)
            val inAnimation = AnimationUtils.loadAnimation(context, R.anim.fade_in)

            outAnimation.setAnimationListener(object : AnimationListener {
                override fun onAnimationStart(animation: Animation) {}
                override fun onAnimationRepeat(animation: Animation) {}
                override fun onAnimationEnd(animation: Animation) {
                    imageView.apply {
                        setImageBitmap(image)
                        startAnimation(inAnimation)
                    }
                }
            })

            imageView.startAnimation(outAnimation)
        }
    }
}