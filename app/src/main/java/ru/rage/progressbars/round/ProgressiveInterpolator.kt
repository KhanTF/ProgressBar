package ru.rage.progressbars.round

import android.support.annotation.Keep

@Keep
class ProgressiveInterpolator : RoundProgressBar.RoundInterpolator() {

    override fun getAngle(index: Int, count: Int, value: Float): Float {
        val angle = 360f * value
        return angle * (count - index) * 2
    }

}