package hu.bme.ewaste.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.google.mlkit.vision.objects.DetectedObject

class BoundingBoxView : View {

    private val paint: Paint = Paint()
    private val textPaint: Paint = Paint()

    var detectedObjects: List<DetectedObject> = listOf()
        set(value) {
            field = value
            invalidate()
        }

    init {
        paint.color = Color.RED
        paint.strokeWidth = 10f
        paint.style = Paint.Style.STROKE
        textPaint.color = Color.RED
        textPaint.style = Paint.Style.FILL
        textPaint.textSize = 80f
    }

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attributeSet: AttributeSet) : super(context, attributeSet)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        detectedObjects.forEach {
            canvas.drawText(
                it.labels.firstOrNull()?.text ?: "Undefined",
                it.boundingBox.centerX().toFloat(),
                it.boundingBox.centerY().toFloat(),
                textPaint
            )
            canvas.drawRect(
                it.boundingBox.left.toFloat(),
                it.boundingBox.top.toFloat(),
                it.boundingBox.right.toFloat(),
                it.boundingBox.bottom.toFloat(),
                paint
            )
        }

    }
}