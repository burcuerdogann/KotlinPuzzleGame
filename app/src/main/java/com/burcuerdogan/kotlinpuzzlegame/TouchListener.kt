package com.burcuerdogan.kotlinpuzzlegame

import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.core.view.NestedScrollingChild
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop

class TouchListener(private val activity: PuzzleActivity) : View.OnTouchListener {

    private var xDelta = 0f
    private var yDelta = 0f


    override fun onTouch(view: View?, motionEvent: MotionEvent?): Boolean {
        val x = motionEvent!!.rawX
        val y = motionEvent.rawY
        val tolerance = Math.sqrt(
            Math.pow(view?.width.toDouble(), 2.0) +
                    Math.pow(view?.height.toDouble(), 2.0)
        ) / 10

        val piece = view as PuzzlePiece

        if (!piece.canMove) {
            return true
        }

        val lParams = view.layoutParams as RelativeLayout

        when (motionEvent.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                xDelta = x - lParams.marginLeft
                yDelta = y - lParams.marginTop
                piece.bringToFront()
            }
            MotionEvent.ACTION_MOVE -> {
                lParams.marginLeft = (x - xDelta).toInt()
                lParams.marginLeft = (y - yDelta).toInt()
                view.layoutParams = lParams
            }
            MotionEvent.ACTION_UP ->{
                val xDiff = StrictMath.abs(
                    piece.xCoord - lParams.marginLeft
                )
                val yDiff = StrictMath.abs(
                    piece.yCoord - lParams.marginLeft
                )
                if (xDiff <= tolerance && yDiff <= tolerance){
                    lParams.marginLeft = piece.xCoord
                    lParams.marginTop = piece.yCoord
                    piece.layoutParams = lParams
                    piece.canMove = false
                         sendViewToBack(piece)
                    activity.checkGameOver()
                }
            }
        }

        return true

    }

    private fun sendViewToBack(child:View) {

        val parent = child.parent as ViewGroup

        if (parent != null){
            parent.removeView(child)
            parent.addView(child,0)
        }

    }

}