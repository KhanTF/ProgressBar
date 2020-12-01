package ru.rage.progressbars.round

import android.support.annotation.Keep

@Keep
class LinearInterpolator : RoundProgressBar.RoundInterpolator() {

    override fun getAngle(index: Int, count: Int, value: Float): Float {
        val k = if (index % 2 == 0) 1 else -1
        return index * 30f + k * (360f * value)
    }

}