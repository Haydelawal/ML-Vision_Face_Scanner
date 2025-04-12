package com.hayde117.mlkit_vision_face

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
class FaceBoundingBoxView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private var boundingBox: RectF? = null
    private val paint = Paint().apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 10f
    }

    fun setBoundingBox(rect: Rect, imageWidth: Int, imageHeight: Int) {
        val viewWidth = width
        val viewHeight = height
        val scaleFactorX = viewWidth.toFloat() / imageHeight.toFloat()
        val scaleFactorY = viewHeight.toFloat() / imageWidth.toFloat()
        boundingBox = RectF(
            rect.left * scaleFactorX,
            rect.top * scaleFactorY,
            rect.right * scaleFactorX,
            rect.bottom * scaleFactorY
        )
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        boundingBox?.let {
            canvas?.drawRect(it, paint)
        }
    }

}