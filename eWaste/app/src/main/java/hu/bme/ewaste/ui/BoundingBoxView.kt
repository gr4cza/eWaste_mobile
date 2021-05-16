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
    private val paintBackground: Paint = Paint()
    private val textPaint: Paint = Paint()
    private val STROKE = 10f

    private var updateScaleNeeded = true
    var imageWidth: Int = 0
    var imageHeight: Int = 0
    private var postScaleHeightOffset = 0f
    private var postScaleWidthOffset = 0f
    private var scaleFactor: Float = 0f

    var detectedObjects: List<DetectedObject> = listOf()
        set(value) {
            field = value
            invalidate()
        }

    init {
        paint.color = Color.RED
        paint.strokeWidth = STROKE
        paint.style = Paint.Style.STROKE
        paintBackground.color = Color.RED
        paintBackground.strokeWidth = STROKE
        paintBackground.style = Paint.Style.FILL_AND_STROKE
        textPaint.color = Color.WHITE
        textPaint.style = Paint.Style.FILL
        textPaint.textSize = 50f
    }

    private fun calculateScale() {
        if (updateScaleNeeded) {
            val viewAspectRatio = width.toFloat() / height
            val imageAspectRatio: Float = imageWidth.toFloat() / imageHeight
            if (viewAspectRatio > imageAspectRatio) {
                // The image needs to be vertically cropped to be displayed in this view.
                scaleFactor = width.toFloat() / imageWidth
                postScaleHeightOffset = (width.toFloat() / imageAspectRatio - height) / 2
            } else {
                // The image needs to be horizontally cropped to be displayed in this view.
                scaleFactor = height.toFloat() / imageHeight
                postScaleWidthOffset = (height.toFloat() * imageAspectRatio - width) / 2
            }
            updateScaleNeeded = false
        }
    }

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attributeSet: AttributeSet) : super(context, attributeSet)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        calculateScale()
        detectedObjects.forEach {
            drawBoundingBox(canvas, it)
        }

    }

    private fun drawBoundingBox(
        canvas: Canvas,
        it: DetectedObject
    ) {
        drawText(canvas, "${it.labels[0].text} id: ${it.trackingId}", it)
        canvas.drawRect(
            translateX(it.boundingBox.left.toFloat()),
            translateY(it.boundingBox.top.toFloat()),
            translateX(it.boundingBox.right.toFloat()),
            translateY(it.boundingBox.bottom.toFloat()),
            paint
        )
    }

    private fun drawText(
        canvas: Canvas,
        text: String,
        detectedObject: DetectedObject
    ) {
        canvas.drawRect(
            translateX(detectedObject.boundingBox.left.toFloat()),
            translateY(detectedObject.boundingBox.top.toFloat()) - textPaint.textSize,
            translateX(detectedObject.boundingBox.left.toFloat()) + textPaint.measureText(text),
            translateY(detectedObject.boundingBox.top.toFloat()),
            paintBackground
        )
        canvas.drawText(
            text,
            translateX(detectedObject.boundingBox.left.toFloat()),
            translateY(detectedObject.boundingBox.top.toFloat()) - STROKE
            ,
            textPaint
        )
    }

    private fun translateX(x: Float) = x * scaleFactor - postScaleWidthOffset
    private fun translateY(y: Float) = y * scaleFactor - postScaleHeightOffset
}