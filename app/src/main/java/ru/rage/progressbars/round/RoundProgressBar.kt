package ru.rage.progressbars.round

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import ru.rage.progressbars.R
import kotlin.math.min

class RoundProgressBar @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    companion object {
        private const val DEFAULT_THICKNESS = 2f
        private const val DEFAULT_GAP = 10f
        private const val DEFAULT_COUNT = 5
        private const val DEFAULT_RADIUS = 10f
        private const val DEFAULT_DURATION = 5000
        private const val DEFAULT_SWEEP_ANGLE = 180f
        private val DEFAULT_COLORS = intArrayOf(Color.RED, Color.GREEN, Color.BLUE)
    }

    private var thickness = DEFAULT_THICKNESS
    private var radius = DEFAULT_RADIUS
    private var gap = DEFAULT_GAP
    private var count = DEFAULT_COUNT
    private var animatorValue = 0f
    private var colors = DEFAULT_COLORS
    private var sweepAngle = DEFAULT_SWEEP_ANGLE
    private var interpolator: RoundInterpolator

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val animator = ValueAnimator.ofFloat(0f, 1f)
    private val animatorUpdateListener = ValueAnimator.AnimatorUpdateListener {
        animatorValue = it.animatedValue as Float
        postInvalidateOnAnimation()
    }

    init {
        val typedArray = when {
            attrs != null -> context.obtainStyledAttributes(attrs, R.styleable.RoundProgressBar)
            else -> null
        }
        val interpolatorCanonicalPath = typedArray?.getString(R.styleable.RoundProgressBar_rpb_interpolator)
        val colorsRef = typedArray?.getResourceId(R.styleable.RoundProgressBar_rpb_colors, 0) ?: 0
        if (colorsRef != 0) {
            colors = resources.getIntArray(colorsRef)
        }
        gap = typedArray?.getDimension(R.styleable.RoundProgressBar_rpb_gap, DEFAULT_GAP) ?: DEFAULT_GAP
        thickness = typedArray?.getDimension(R.styleable.RoundProgressBar_rpb_thickness, DEFAULT_THICKNESS) ?: DEFAULT_THICKNESS
        sweepAngle = typedArray?.getFloat(R.styleable.RoundProgressBar_rpb_sweep_angle, DEFAULT_SWEEP_ANGLE) ?: DEFAULT_SWEEP_ANGLE
        radius = typedArray?.getDimension(R.styleable.RoundProgressBar_rpb_radius, DEFAULT_RADIUS) ?: DEFAULT_RADIUS
        count = typedArray?.getInt(R.styleable.RoundProgressBar_rpb_count, DEFAULT_COUNT) ?: DEFAULT_COUNT
        interpolator = if (interpolatorCanonicalPath.isNullOrBlank()) {
            ProgressiveInterpolator()
        } else {
            val interpolatorClass = Class.forName(interpolatorCanonicalPath)
            interpolatorClass.newInstance() as RoundInterpolator
        }
        animator.duration = (typedArray?.getInt(R.styleable.RoundProgressBar_rpb_duration, DEFAULT_DURATION) ?: DEFAULT_DURATION).toLong()
        animator.repeatCount = ValueAnimator.INFINITE
        animator.interpolator = LinearInterpolator()
        animator.addUpdateListener(animatorUpdateListener)
        typedArray?.recycle()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        animator.start()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val centerX = width / 2
        val centerY = height / 2
        for (i in 0 until count) {
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = thickness
            paint.color = interpolator.getColor(i, animatorValue, colors)
            val arcRadius = radius - thickness + thickness * i + gap * i
            val angle = interpolator.getAngle(i, count, animatorValue)
            val sweepAngle = interpolator.getSweepAngle(i, count, animatorValue, sweepAngle)
            val left = centerX - arcRadius
            val right = centerX + arcRadius
            val top = centerY - arcRadius
            val bottom = centerY + arcRadius
            canvas.drawArc(left, top, right, bottom, angle, sweepAngle, false, paint)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        if (count <= 0) {
            setMeasuredDimension(0, 0)
        } else {
            val desiredRoundSize = (radius + thickness * count + gap * (count - 1)) * 2
            val desiredWidth = desiredRoundSize.toInt() + paddingLeft + paddingRight
            val desiredHeight = desiredRoundSize.toInt() + paddingTop + paddingBottom
            val measuredWidth = when (widthMode) {
                MeasureSpec.EXACTLY -> widthSize
                MeasureSpec.AT_MOST -> min(desiredWidth, widthSize)
                else -> desiredWidth
            }
            val measuredHeight = when (heightMode) {
                MeasureSpec.EXACTLY -> heightSize
                MeasureSpec.AT_MOST -> min(desiredHeight, heightSize)
                else -> desiredHeight
            }
            setMeasuredDimension(measuredWidth, measuredHeight)
        }
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        if (visibility == VISIBLE) {
            animator.resume()
        } else {
            animator.pause()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animator.cancel()
    }

    fun setInterpolator(interpolator: RoundInterpolator) {
        this.interpolator = interpolator
    }

    abstract class RoundInterpolator {
        abstract fun getAngle(index: Int, count: Int, value: Float): Float
        open fun getSweepAngle(index: Int, count: Int, value: Float, originSweepAngle: Float) = originSweepAngle
        open fun getColor(index: Int, value: Float, originColors: IntArray) = originColors[originColors.size - index % originColors.size - 1]
    }

}
