package ru.rage.progressbars.round

import android.support.annotation.Keep

@Keep
class ReverseProgressiveInterpolator : RoundProgressBar.RoundInterpolator() {

    override fun getAngle(index: Int, count: Int, value: Float): Float {
        val direction = if (index < count / 2) 1 else -1
        val startAngle = if (index < count / 2) 0 else 90
        val angle = direction * 360f * value + startAngle
        return angle * (count - index) * 2
    }

}