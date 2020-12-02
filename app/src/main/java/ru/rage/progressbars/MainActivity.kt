package ru.rage.progressbars

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import ru.rage.progressbars.round.LinearInterpolator
import ru.rage.progressbars.round.ProgressiveInterpolator
import ru.rage.progressbars.round.ReverseProgressiveInterpolator

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        progressive.setOnClickListener {
            progress.setInterpolator(ProgressiveInterpolator())
        }
        reverse_progressive.setOnClickListener {
            progress.setInterpolator(ReverseProgressiveInterpolator())
        }
        linear.setOnClickListener {
            progress.setInterpolator(LinearInterpolator())
        }
    }
}
