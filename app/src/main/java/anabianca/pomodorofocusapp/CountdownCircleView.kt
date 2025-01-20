package anabianca.pomodorofocusapp

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat

class CountdownCircleView @JvmOverloads constructor(

    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.circle_background)
        style = Paint.Style.STROKE
        strokeWidth = 35f
        strokeCap = Paint.Cap.ROUND
    }

    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.laranja)
        style = Paint.Style.STROKE
        strokeWidth = 35f
        strokeCap = Paint.Cap.ROUND
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.timer_text)
        textSize = 80f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textAlign = Paint.Align.CENTER
    }

    private var progressFraction: Float = 1f
    private var timerText: String = "25:00"

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerX = width / 2f
        val centerY = height / 2f
        val radius = Math.min(centerX, centerY) - 40f

        // Círculo de fundo
        canvas.drawCircle(centerX, centerY, radius, circlePaint)

        // Arco de progresso
        val sweepAngle = 360 * progressFraction
        canvas.drawArc(
            centerX - radius,
            centerY - radius,
            centerX + radius,
            centerY + radius,
            -90f,
            sweepAngle,
            false,
            progressPaint
        )

        // Texto do timer
        canvas.drawText(timerText, centerX, centerY + (textPaint.textSize / 3), textPaint)
    }

    fun updateProgress(fraction: Float) {
        progressFraction = fraction
        invalidate()
    }

    fun updateTimerText(text: String) {
        timerText = text
        invalidate()
    }
}