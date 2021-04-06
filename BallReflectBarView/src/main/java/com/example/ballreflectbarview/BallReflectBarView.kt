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