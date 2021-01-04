package com.zejian.myapplication.taluo

import android.os.Build
import android.view.View
import androidx.viewpager2.widget.ViewPager2

class ScaleInTransformer : ViewPager2.PageTransformer {
    private val mMinScale = DEFAULT_MIN_SCALE
    override fun transformPage(view: View, position: Float) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.elevation = -kotlin.math.abs(position)
        }
        val pageWidth = view.width
        val pageHeight = view.height

        view.pivotY = (pageHeight / 2).toFloat()
        view.pivotX = (pageWidth / 2).toFloat()
        if (position < -1) {
            view.scaleX = DEFAULT_FONT_SCALE
            view.scaleY = DEFAULT_FONT_SCALE
            view.pivotX = pageHeight.toFloat()
        } else if (position <= 1) {
            if (position < 0) {
                val scaleFactor = (1 + position) * (1 - DEFAULT_FONT_SCALE) + DEFAULT_FONT_SCALE
                view.scaleX = scaleFactor
                view.scaleY = scaleFactor
                view.pivotX = pageHeight * (DEFAULT_CENTER + DEFAULT_CENTER * -position)
            } else {
                val scaleFactor = (1 - position) * (1 - mMinScale) + mMinScale
                view.scaleX = scaleFactor
                view.scaleY = scaleFactor
                view.pivotX = pageHeight * ((1 - position) * DEFAULT_CENTER)
            }
        } else {
            view.pivotX = 0f
            view.scaleX = mMinScale
            view.scaleY = mMinScale
        }
    }

    companion object {
        const val DEFAULT_FONT_SCALE = 0.48f
        const val DEFAULT_MIN_SCALE = 0.8f
        const val DEFAULT_CENTER = 0f
    }
}