package com.codelog.rkimer.controller

import com.codelog.rkimer.cube.Move
import com.codelog.rkimer.cube.Scramble
import com.codelog.rkimer.cube.Solve
import com.codelog.rkimer.cube.timeToStr
import javafx.application.Platform
import javafx.beans.InvalidationListener
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableBooleanValue
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.chart.CategoryAxis
import javafx.scene.chart.LineChart
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.cell.CheckBoxTableCell
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.control.cell.TextFieldTableCell
import javafx.util.Callback
import javafx.util.StringConverter
import java.net.URL
import java.util.*

class SolveDataController: Initializable {
    companion object {
        var instance: SolveDataController? = null
    }

    lateinit var solves: List<Solve>

    @FXML
    lateinit var tblSolves: TableView<Solve>
    @FXML
    lateinit var chtSolves: LineChart<String, Number>

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        instance = this

        tblSolves.columns.clear()
        tblSolves.items.clear()
        tblSolves.columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
        val clmTime = TableColumn<Solve, Int>("Time")
        clmTime.cellValueFactory = PropertyValueFactory("time")
        clmTime.cellFactory = TextFieldTableCell.forTableColumn(object: StringConverter<Int>() {
            override fun toString(time: Int): String = timeToStr(time)
            override fun fromString(string: String): Int = 0
        })

        val clmDNF = TableColumn<Solve, Boolean>("DNF")
        clmDNF.cellValueFactory = PropertyValueFactory("dnf")
        clmDNF.cellFactory = TextFieldTableCell.forTableColumn(object: StringConverter<Boolean>() {
            override fun toString(value: Boolean): String = if (value) "Yes" else "No"
            override fun fromString(string: String): Boolean = string == "Yes"
        })

        val clmPlusTwo = TableColumn<Solve, Boolean>("+2")
        clmPlusTwo.cellValueFactory = PropertyValueFactory("plusTwo")
        clmPlusTwo.cellFactory = TextFieldTableCell.forTableColumn(object: StringConverter<Boolean>() {
            override fun toString(value: Boolean): String = if (value) "Yes" else "No"
            override fun fromString(string: String): Boolean = string == "Yes"
        })

        val clmScramble = TableColumn<Solve, Scramble>("Scramble")
        clmScramble.cellValueFactory = PropertyValueFactory("scramble")
        clmScramble.cellFactory = TextFieldTableCell.forTableColumn(object: StringConverter<Scramble>() {
            override fun toString(scramble: Scramble?): String = scramble?.toString() ?: "No scramble recorded"
            override fun fromString(string: String): Scramble = Scramble(Array(20) { _ -> Move.R })
        })

        tblSolves.columns.addAll(clmTime, clmDNF, clmPlusTwo, clmScramble)
    }

    fun addSolves() {
        tblSolves.items.addAll(solves)
        chtSolves.xAxis.label = "Solve"
        chtSolves.yAxis.label = "Time (seconds)"
        (chtSolves.yAxis as NumberAxis).isForceZeroInRange = false
        chtSolves.createSymbols = false
        chtSolves.verticalGridLinesVisible = false

        val data = ArrayList<Solve>(solves.size)
        for (s in solves) {
            if (!s.dnf)
                data.add(s)
        }

        val solveSeries = XYChart.Series<String, Number>()
        solveSeries.name = "Solves"
        for (i in data.indices) {
            solveSeries.data.add(XYChart.Data((i + 1).toString(), data[i].actualTime() / 100))
        }
        chtSolves.data.add(solveSeries)
        solveSeries.node.style = "-fx-stroke: dodgerblue"
        Platform.runLater {
            val nl = chtSolves.lookup(".default-color0.chart-line-symbol")
            nl.style = "-fx-background-color: dodgerblue"
        }

        var best = data[0].actualTime()
        val bestSeries = XYChart.Series<String, Number>()
        bestSeries.name = "PB"
        bestSeries.data.add(XYChart.Data("1", best / 100))
        for (i in data.indices) {
            if (data[i].actualTime() < best) {
                best = data[i].actualTime()
                bestSeries.data.add(XYChart.Data((i + 1).toString(), best / 100))
            }
        }
        chtSolves.data.add(bestSeries)
        bestSeries.node.style = "-fx-stroke-dash-array: 6;-fx-stroke: yellowgreen;"
        Platform.runLater {
            val nl = chtSolves.lookup(".default-color1.chart-line-symbol")
            nl.style = "-fx-background-color: yellowgreen"
        }

        if (data.size >= 3) {
            val ao3Series = XYChart.Series<String, Number>()
            ao3Series.name = "Average of 3"
            for (i in 2 until data.size) {
                var ave = 0.0
                for (j in i - 2..i)
                    ave += data[j].actualTime().toDouble() / 100
                ave /= 3
                ao3Series.data.add(XYChart.Data((i + 1).toString(), ave))
            }
            chtSolves.data.add(ao3Series)
            ao3Series.node.style = "-fx-stroke: orange;"
            Platform.runLater {
                val nl = chtSolves.lookup(".default-color2.chart-line-symbol")
                nl.style = "-fx-background-color: orange"
            }
        }

        chtSolves.title = "Line chart representing solve speed progression"


    }
}