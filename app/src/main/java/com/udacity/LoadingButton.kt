package com.udacity

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlinx.android.synthetic.main.content_main.view.*
import kotlin.properties.Delegates


class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var widthSize = 0
    private var heightSize = 0

    //Set default color for background and text
    private var bgColor: Int = context.getColor(R.color.colorPrimary)
    private var textColor: Int = Color.WHITE

    private var progress: Double = 0.0
    private var valueAnimator = ValueAnimator()
    private var valueAnimatorcircle = ValueAnimator()


    // observes the state of button
    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->


    }

    //update every time we move from 0 to 100 (loading progress)
    private fun animatorListener(it: ValueAnimator) {
        progress = (it.animatedValue as Float).toDouble()
        invalidate()
        requestLayout()
    }

    //when animator is done return the progress to zero
    private fun progressAnimatorEnd() = object : AnimatorListenerAdapter() {

        override fun onAnimationEnd(animation: Animator?) {
            super.onAnimationEnd(animation)

            valueAnimator.cancel()
            valueAnimatorcircle.cancel()
            progress = 0.0
        }
    }

    //call after downloading is completed
    fun hasCompletedDownload() {
        // cancel the animation when file is downloaded
        buttonState = ButtonState.Completed
        progressAnimatorEnd()
        custom_button.isEnabled = true

        invalidate()
        requestLayout()

    }

    init {
        //this enable the custom view to be clickable
        isClickable = true


        valueAnimator = AnimatorInflater.loadAnimator(context, R.animator.loading_animation)
                as ValueAnimator
        valueAnimator.addUpdateListener { animatorListener(it) }
        //when end of animator happened ,we remove the loading
//        valueAnimator.addListener(progressAnimatorEnd())
          valueAnimatorcircle = ValueAnimator.ofInt(0, 100).apply {

            addUpdateListener { valueAnimator ->
                progress = (valueAnimator.animatedValue as Int).toDouble()
                invalidate()
            }
            repeatMode = ValueAnimator.REVERSE
            repeatCount = ValueAnimator.INFINITE
            duration = 2000
            invalidate()

        }


    }

    override fun performClick(): Boolean {
        super.performClick()

        if (buttonState == ButtonState.Completed)
            buttonState = ButtonState.Loading
            custom_button.isEnabled = false
        valueAnimator.start()
        valueAnimatorcircle.start()

        return true

    }

    // set attributes of paint
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        //make the text Bold
        typeface = Typeface.create("", Typeface.BOLD)

    }

    private val paintCircle = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        strokeWidth = 10f
        color = Color.RED
    }

    private val oval = RectF()

    private fun setSpace() {
        val horizontalCenter = (width.div(1.36)).toFloat()
        val verticalCenter = (height.div(2)).toFloat()
        val ovalSize =40
        oval.set(
            horizontalCenter- ovalSize,
            verticalCenter - ovalSize,
            horizontalCenter + ovalSize,
            verticalCenter + ovalSize
        )
    }

    override fun onDraw(canvas: Canvas) {

        super.onDraw(canvas)

        paint.strokeWidth = 0f
        paint.color = bgColor
        // draw custom button
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)

        //to show rectangle progress on custom button
        //while file is downloading
        if (buttonState == ButtonState.Loading) {

            paint.color = context.getColor(R.color.colorPrimaryDark)
            canvas.drawRect(
                0f, 0f,
                (width * (progress / 100)).toFloat(), height.toFloat(), paint
            )

            setSpace()
            canvas.drawArc(oval, 0f, 360f * progress.toFloat() / 100f, true, paintCircle)

        }
        val buttonText = if (buttonState == ButtonState.Loading)
            resources.getString(R.string.button_loading)
        else resources.getString(R.string.button_download)

        //write the text on the button
        paint.color = textColor
        canvas.drawText(buttonText, (width / 2).toFloat(), ((height + 30) / 2).toFloat(), paint)

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = measuredWidth
//        ;resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        //for cashing
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

}