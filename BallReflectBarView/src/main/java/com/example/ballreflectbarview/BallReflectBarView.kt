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
val squareFactor : Float = 3.9f
val parts : Int = 4
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
    save()
    translate(w / 2, h / 2)
    drawCircle(
        -w / 2 + r + (w - 2 * r) * sf2,
        -h / 2 + r + (h - 2 * r) * sf2.sinify(),
        r * (sf1 - sf3),
        paint
    )
    restore()
}

fun Canvas.drawBRBNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = colors[i]
    drawBallReflectBar(scale, w, h, paint)
}
