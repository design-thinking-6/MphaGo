package io.suyong.mphago

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.BitmapDrawable
import android.os.CountDownTimer
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.time_view.view.*
import kotlin.math.ceil
import kotlin.math.roundToInt

class TimeView(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {
    var isLock: Boolean
        private set
    var isStarted = false
        private set
    var maxTime: Int = 60
        set(value) {
            if (!isStarted) {
                field = value
                time = value
            }
        }
    var time: Int = 60
        private set

    private val innerView = LayoutInflater.from(context).inflate(R.layout.time_view, this, true)
    private var timeListener: (Int, Int, TimeView) -> Unit = { _, _, _ -> }
    private var clickListener: (Int) -> Unit = { }

    private var timer: CountDownTimer? = null
    private var lastTime: Int

    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        backgroundPaint.color = Color.parseColor("#29B6F6")
        backgroundPaint.setShadowLayer(
            dp(4).toFloat(),
            0f,
            dp(2).toFloat(),
            Color.parseColor("#7d7d7d7d")
        )

        elevation = 2f
        setWillNotDraw(false)
        measure(0, 0)

        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.TimeView,
            0, 0
        ).apply {
            try {
                isLock = getBoolean(R.styleable.TimeView_lock, true)
                maxTime = getInteger(R.styleable.TimeView_max_time, 60)
                time = getInteger(R.styleable.TimeView_time, maxTime)
                lastTime = time
            } finally {
                recycle()
            }
        }

        innerView.time_view_lock.drawable.alpha = 255
        innerView.time_view_text.visibility = View.GONE
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun start() {
        isLock = false

        val timeView = this
        timer = object : CountDownTimer((maxTime * 1000).toLong(), 50) {
            override fun onTick(millisUntilFinished: Long) {
                time = ceil(millisUntilFinished / 1000.0).toInt()

                innerView.time_view_image.setImageBitmap(drawLeftTime((millisUntilFinished / 1000f) / maxTime.toFloat() * 100f))
                innerView.time_view_text.text = time.toString()

                if (time == 1) {
                    innerView.time_view_text.alpha = (millisUntilFinished / 1000f)
                    innerView.time_view_lock.drawable.alpha =
                        ((1 - millisUntilFinished / 1000f) * 127 + 128).toInt()
                }

                if (time != lastTime) {
                    timeListener(time, maxTime, timeView)

                    lastTime = time
                }
            }

            override fun onFinish() {
                innerView.time_view_image.setImageDrawable(null)
                innerView.time_view_text.visibility = View.GONE
                innerView.time_view_lock.drawable.alpha = 255

                time = 0
                lastTime = 0
                isStarted = false
                timeListener(0, maxTime, timeView)
            }
        }

        innerView.time_view_text.visibility = View.VISIBLE

        val drawable = timeView.resources.getDrawable(
            R.drawable.ic_lock_open_animated,
            context.theme
        ) as AnimatedVectorDrawable
        drawable.alpha = 128
        innerView.time_view_lock.setImageDrawable(drawable)

        isStarted = true
        timer?.start()
    }

    fun setOnTimeListener(onTimeListener: (Int, Int, TimeView) -> Unit) {
        this.timeListener = onTimeListener
    }

    fun setOnClickListener(onClickListener: (Int) -> Unit) {
        this.clickListener = onClickListener

        this.innerView.isClickable = true
        this.innerView.setOnClickListener {
            this.clickListener(this.time)
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun setLock(lock: Boolean) {
        val drawable = this.resources.getDrawable(
            if (lock) R.drawable.ic_lock_close_animated
            else R.drawable.ic_lock_open_animated,
            context.theme
        ) as AnimatedVectorDrawable
        drawable.start()
        innerView.time_view_lock.setImageDrawable(drawable)

        isLock = lock
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawCircle(
            (width / 2).toFloat(),
            (height / 2).toFloat(),
            (width / 2 - dp(4)).toFloat(),
            backgroundPaint
        )

        if (isLock && innerView.time_view_image.drawable == null) {
            innerView.time_view_image.setImageBitmap(drawLeftTime(100f))
        }
    }

    private fun drawLeftTime(
        percent: Float,
        initWidth: Int? = null,
        initHeight: Int? = null
    ): Bitmap {
        val width = initWidth ?: this.measuredWidth
        val height = initHeight ?: this.measuredHeight

        val bitmap =
            Bitmap.createBitmap(width - dp(8), height - dp(8), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val paint = Paint()
        paint.strokeWidth = dp(4).toFloat()
        paint.style = Paint.Style.STROKE
        paint.color = Color.parseColor("#FFFFFF")

        canvas.drawArc(
            RectF(
                dp(8).toFloat(),
                dp(8).toFloat(),
                width - dp(16).toFloat(),
                height - dp(16).toFloat()
            ),
            -90f,
            3.6f * percent,
            false,
            paint
        )

        return bitmap
    }

    private fun dp(dp: Int) = (dp * resources.displayMetrics.density).roundToInt()
}