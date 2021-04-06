package com.example.ballreflectbarview

import android.view.View
import android.view.MotionEvent
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.RectF

val colors : Array<Int> = arrayOf(
    "#f44336",
    "#3F51B5",
    "#00695C",
    "#FFAB00",
    "#BF360C"
).map {
    Color.parseColor(it)
}.toTypedArray()
val backColor : Int = Color.parseColor("#BDBDBD")
val delay : Long = 20
val ballRFactor : Float = 9.9f
val squareFactor : Float = 1.1f
val parts : Int = 3
val scGap : Float = 0.02f / parts


fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n
fun Float.sinify() : Float = Math.sin(this * Math.PI).toFloat()

fun Canvas.drawBallReflectBar(scale : Float, w : Float, h : Float, paint : Paint) {
    val r : Float = Math.min(w, h) / ballRFactor
    val sf1 : Float = scale.divideScale(0, parts)
    val sf2 : Float = scale.divideScale(1, parts)
    val sf3 : Float = scale.divideScale(2, parts)
    val size : Float = Math.min(w, h) / squareFactor
    val sff2 : Float = sf2.sinify()
    save()
    translate(w / 2, h / 2)
    drawCircle(
        -w / 2 + r + (w - 2 * r) * sf2,
        -h / 2 + r + (h - 2 * r - size) * sff2,
        r * (sf1 - sf3),
        paint
    )
    drawRect(RectF(-size / 2, h / 2 -size * sff2, size / 2, h / 2), paint)
    restore()
}

fun Canvas.drawBRBNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = colors[i]
    drawBallReflectBar(scale, w, h, paint)
}

class BallReflectBarView(ctx : Context) : View(ctx) {

    private val renderer  : Renderer = Renderer(this)

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var dir : Float = 0f, var prevScale : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            scale += scGap * dir
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1f - 2 * prevScale
                cb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(delay)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class BRBNode(var i : Int, val state : State = State()) {

        private var next : BRBNode? = null
        private var prev : BRBNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < colors.size - 1) {
                next = BRBNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawBRBNode(i, state.scale, paint)
        }

        fun update(cb : (Float) -> Unit) {
            state.update(cb)
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : BRBNode {
            var curr : BRBNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class BallReflectBar(var i : Int) {

        private var curr : BRBNode = BRBNode(0)
        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            curr.draw(canvas, paint)
        }

        fun update(cb : (Float) -> Unit) {
            curr.update {
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                cb(it)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            curr.startUpdating(cb)
        }
    }

    data class Renderer(var view : BallReflectBarView) {

        private var animator : Animator = Animator(view)
        private var brb : BallReflectBar = BallReflectBar(0)
        private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

        fun render(canvas : Canvas) {
            canvas.drawColor(backColor)
            brb.draw(canvas, paint)
            animator.animate {
                brb.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            brb.startUpdating {
                animator.start()
            }
        }
    }

    companion object {
        fun create(activity : Activity) : BallReflectBarView {
            val view : BallReflectBarView = BallReflectBarView(activity)
            activity.setContentView(view)
            return view
        }
    }
}