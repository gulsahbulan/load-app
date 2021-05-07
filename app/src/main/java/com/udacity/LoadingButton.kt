package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.udacity.util.Constants.ANIMATION_DURATION
import com.udacity.util.Constants.BUTTON_TEXT_SIZE_SP
import com.udacity.util.Constants.CIRCLE_DIAMETER_PERCENTAGE
import com.udacity.util.Constants.CIRCLE_MARGIN_PERCENTAGE
import kotlin.properties.Delegates


class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var textHeight = 0.0f
    private var textOffset = 0.0f
    private val circleRectF = RectF()
    private var circleDiameter = 0.0f
    private var circleMargin = 0.0f
    private var circleProgress = 0.0f
    private val buttonRectF = RectF()
    private var buttonProgress = 0.0f
    private val buttonColor: Int
    private var buttonValueAnimator = ValueAnimator()
    private var circleValueAnimator = ValueAnimator()
    private val buttonAnimPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val circleAnimPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        textSize = BUTTON_TEXT_SIZE_SP * resources.displayMetrics.scaledDensity
    }

    private var buttonState: ButtonState by Delegates.observable(ButtonState.Completed) { _, _, new ->
        when (new) {
            ButtonState.Loading -> {

                buttonValueAnimator = ValueAnimator.ofFloat(0f, widthSize.toFloat()).apply {
                    duration = ANIMATION_DURATION
                    repeatCount = ValueAnimator.INFINITE
                    repeatMode = ValueAnimator.RESTART
                    addUpdateListener {
                        buttonProgress = animatedValue as Float
                        invalidate()
                    }
                }
                circleValueAnimator = ValueAnimator.ofFloat(0f, 360f).apply {
                    duration = ANIMATION_DURATION
                    repeatCount = ValueAnimator.INFINITE
                    repeatMode = ValueAnimator.RESTART
                    addUpdateListener {
                        circleProgress = animatedValue as Float
                        invalidate()
                    }
                }
                buttonValueAnimator.start()
                circleValueAnimator.start()
            }
            ButtonState.Completed -> {
                if (buttonValueAnimator.isStarted) {
                    buttonValueAnimator.end()
                }
                if (circleValueAnimator.isStarted) {
                    circleValueAnimator.end()
                }
                invalidate()
            }
            ButtonState.Clicked -> {
                // Do nothing here.
            }
        }
    }

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.LoadingButton,
            0,
            0
        ).apply {
            try {
                buttonColor = getColor(
                    R.styleable.LoadingButton_buttonColor,
                    context.getColor(R.color.colorPrimary)
                )
                buttonAnimPaint.color = getColor(
                    R.styleable.LoadingButton_buttonAnimColor,
                    context.getColor(R.color.colorPrimaryDark)
                )
                circleAnimPaint.color = getColor(
                    R.styleable.LoadingButton_circleAnimColor,
                    context.getColor(R.color.colorAccent)
                )
                textPaint.color = getColor(
                    R.styleable.LoadingButton_buttonTextColor,
                    context.getColor(R.color.white)
                )
            } finally {
                recycle()
            }
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val buttonLabel: String
        canvas?.drawColor(buttonColor)
        if (buttonState == ButtonState.Loading) {
            buttonLabel = context.getString(R.string.button_loading)
            canvas?.drawRect(
                0f,
                0f,
                buttonProgress,
                heightSize.toFloat(),
                buttonAnimPaint
            )
            canvas?.drawArc(
                circleRectF,
                0f,
                circleProgress,
                true,
                circleAnimPaint
            )
        } else {
            buttonLabel = context.getString(R.string.button_name)
        }
        canvas?.drawText(
            buttonLabel,
            buttonRectF.centerX(),
            buttonRectF.centerY() + textOffset,
            textPaint
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        buttonRectF.set(
            0f,
            0f,
            widthSize.toFloat(),
            heightSize.toFloat()
        )
        circleDiameter = heightSize * CIRCLE_DIAMETER_PERCENTAGE
        circleMargin = heightSize * CIRCLE_MARGIN_PERCENTAGE
        circleRectF.set(
            widthSize - circleDiameter - circleMargin,
            circleMargin,
            widthSize - circleMargin,
            circleMargin + circleDiameter
        )
        textHeight = textPaint.descent() - textPaint.ascent()
        textOffset = textHeight / 2 - textPaint.descent()
        setMeasuredDimension(w, h)
    }

    fun setLoadingState(state: ButtonState) {
        buttonState = state
    }
}