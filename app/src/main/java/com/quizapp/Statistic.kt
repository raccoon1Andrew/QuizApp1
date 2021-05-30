package com.quizapp

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

import kotlinx.android.synthetic.main.activity_statistic.*
import java.security.KeyStore
import java.util.Map



class Statistic : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistic)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        iv_star.setOnClickListener {
            iv_star.animate().apply {
                duration = 1000
                rotationYBy(360f)
            }.withEndAction {
                    iv_star.animate().apply {
                        duration = 1000
                        rotationXBy(3600f)
                    }.start()
                }

        }

        setPieChart()

    }

    fun setPieChart() {

        var correctAnswers = intent.getIntExtra(Constants.CORRECT_ANSWERS, 0)
        var totalQuestions = intent.getIntExtra(Constants.TOTAL_QUESTIONS, 0)

        val incorrectAnswers = totalQuestions - correctAnswers

        val piechartentry = ArrayList<PieEntry>()
        piechartentry.add(PieEntry(correctAnswers.toFloat(), "Correct"))
        piechartentry.add(PieEntry(incorrectAnswers.toFloat(), "Mistake \n"))

        val colors = ArrayList<Int>()
        colors.add(Color.RED)
        colors.add(Color.BLUE)

        val piedataset = PieDataSet(piechartentry, "\n Statistic")
        piedataset.colors = colors

        piedataset.sliceSpace = 5f
        val data = PieData(piedataset)
        pieChart.data = data

        pieChart.animateY(3000)

        val legend: Legend = pieChart.legend
        legend.textSize = 20f
    }

}
