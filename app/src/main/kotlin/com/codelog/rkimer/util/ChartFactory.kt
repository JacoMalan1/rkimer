package com.codelog.rkimer.util

import javafx.scene.chart.CategoryAxis
import javafx.scene.chart.LineChart
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart

class ChartFactory {
    companion object {
        fun lineChart(data: Array<Number>, title: String, xlab: String, ylab: String, lineName: String): LineChart<String, Number> {
            val xAxis = CategoryAxis()
            val yAxis = NumberAxis()
            yAxis.isForceZeroInRange = false
            xAxis.label = xlab
            yAxis.label = ylab

            val chart = LineChart(xAxis, yAxis)
            chart.title = title
            val series = XYChart.Series<String, Number>()
            series.name = lineName
            for (i in data.indices)
                series.data.add(XYChart.Data((i + 1).toString(), data[i]))

            chart.data.add(series)
            return chart
        }
    }
}