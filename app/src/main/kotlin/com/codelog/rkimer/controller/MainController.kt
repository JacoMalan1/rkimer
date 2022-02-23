package com.codelog.rkimer.controller

import com.codelog.rkimer.App
import com.codelog.rkimer.cube.*
import com.codelog.rkimer.util.AlertFactory
import com.codelog.rkimer.util.ConfirmationDialogFactory
import javafx.application.Platform
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.fxml.Initializable
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.control.cell.TextFieldListCell
import javafx.scene.image.ImageView
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.stage.Stage
import javafx.util.StringConverter
import org.json.JSONArray
import java.io.File
import java.io.IOException
import java.net.URL
import java.util.*
import kotlin.math.round
import kotlin.system.exitProcess

class MainController: Initializable, EventHandler<KeyEvent> {
    private var timer: Timer = Timer()
    private var time: Int = 0

    companion object {
        var instance: MainController? = null
        var scene: Scene? = null
        var isTimerRunning = false
        var isSaved = false

        fun registerEvents() {
            scene?.onKeyPressed = instance

            App.currentStage?.setOnCloseRequest {
                if (!isSaved) {
                    val dialog = ConfirmationDialogFactory.yesNoDialog(
                        "Your times haven't been saved. Would you like to save them now?",
                        "Save times?"
                    )
                    val result = dialog.showAndWait()
                    if (result.isEmpty) {
                        it.consume()
                        return@setOnCloseRequest
                    }

                    if (result.get())
                        instance?.saveSolves()
                }
                exitProcess(0)
            }
        }
    }

    @FXML
    lateinit var lblTimer: Label
    @FXML
    lateinit var txtStats: TextArea
    @FXML
    lateinit var lstSolves: ListView<Solve>
    @FXML
    lateinit var lblScramble: Label
    @FXML
    lateinit var mnuDNF: CheckMenuItem
    @FXML
    lateinit var mnuPlusTwo: CheckMenuItem
    @FXML
    lateinit var imgScramble: ImageView

    lateinit var scramble: Scramble

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        mnuDNF.isDisable = true
        mnuPlusTwo.isDisable = true

        lstSolves.isEditable = false
        lstSolves.cellFactory = TextFieldListCell.forListView(object: StringConverter<Solve>() {
            override fun toString(solve: Solve): String = solve.toString()

            override fun fromString(string: String): Solve {
                return Solve(0, dnf = false, plusTwo = false)
            }
        })

        lstSolves.selectionModel.selectedItemProperty().addListener { _, _, newValue ->
            if (newValue != null) {
                mnuDNF.isDisable = false
                mnuPlusTwo.isDisable = false

                mnuDNF.isSelected = newValue.dnf
                mnuPlusTwo.isSelected = newValue.plusTwo

                mnuDNF.isDisable = mnuPlusTwo.isSelected
                mnuPlusTwo.isDisable = mnuDNF.isSelected
            } else {
                mnuDNF.isDisable = true
                mnuPlusTwo.isDisable = true
            }
        }

        loadSolves("solves.json")

        lblTimer.text = "00:00.00"
        lblTimer.style = "-fx-font-family:'DSEG7 Modern'"
        txtStats.isDisable = true
        resetTimer()
        calculateStatistics()
        instance = this
    }

    private fun loadSolves(filePath: String) {
        val solves = ArrayList<Solve>()
        val file = File(filePath)
        if (file.exists()) {
            val contents = file.readText()
            val jsonArray = JSONArray(contents)
            for (i in 0 until jsonArray.length()) {
                val solve = Solve.fromJSONObject(jsonArray.getJSONObject(i))
                solves.add(solve)
            }

            mnuPlusTwo.isSelected = solves.last().plusTwo
            mnuDNF.isSelected = solves.last().dnf

            lstSolves.items.addAll(solves)
            if (lstSolves.items.size > 0)
                lstSolves.selectionModel.select(0)
        }

        isSaved = true
    }

    fun tickTimer() {
        time++
        lblTimer.text = timeToStr(time)
    }

    private fun resetTimer() {
        scramble = ScrambleFactory.generateScramble(20, CubeType.c33)
        val imgProvider: ImageProvider = ScrambleImageProvider(scramble, 250.0, 250.0)
        imgScramble.image = imgProvider.provide()

        lblScramble.text = scramble.toString()
    }

    private fun registerSolve() {
        lstSolves.items.add(0, Solve(time, dnf = false, plusTwo = false, scramble = scramble))
        isSaved = false
        calculateStatistics()
    }

    private fun calculateStatistics() {
        if (lstSolves.items.size == 0)
            return

        val solves = ArrayList<Solve>()
        solves.addAll(lstSolves.items)
        solves.removeIf { it.dnf }

        if (solves.size == 0)
            return

        var best = solves[0]
        for (solve in solves) {
            if (solve.actualTime() < best.actualTime())
                best = solve
        }

        var mo3 = -1
        if (solves.size >= 3) {
            var mo3Double = 0.0
            for (i in 0 until 3) {
                mo3Double += solves[i].actualTime()
            }
            mo3Double /= 3.0
            mo3 = round(mo3Double).toInt()
        }

        var ao5 = -1
        if (solves.size >= 5) {
            var ao5Double = 0.0
            var bo5 = solves[0]
            var wo5 = solves[0]

            for (i in 0 until 5) {
                if (solves[i].actualTime() > wo5.actualTime())
                    wo5 = solves[i]
                if (solves[i].actualTime() < bo5.actualTime())
                    bo5 = solves[i]
            }

            for (i in 0 until 5) {
                if (solves[i] != bo5 && solves[i] != wo5)
                    ao5Double += solves[i].actualTime()
            }
            ao5 = round(ao5Double / 3.0).toInt()
        }

        val builder = StringBuilder()
        val bestStr = timeToStr(best.actualTime())
        var mo3Str = timeToStr(mo3)
        var ao5Str = timeToStr(ao5)

        if (mo3 == -1)
            mo3Str = "N/A"
        if (ao5 == -1)
            ao5Str = "N/A"

        builder.append("Best: $bestStr\n")
        builder.append("Mean of 3: $mo3Str\n")
        builder.append("Average of 5: $ao5Str")

        txtStats.text = builder.toString()
    }

    private fun toggleTimer() {
        if (!isTimerRunning) {
            lblTimer.style = "-fx-font-family:'DSEG7 Modern';-fx-text-fill: green;"
            instance?.timer = Timer()
            instance?.time = 0
            instance?.timer?.scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    isTimerRunning = true
                    Platform.runLater {
                        instance?.tickTimer()
                    }
                }
            }, 0, 10)
        } else {
            instance?.timer?.cancel()
            isTimerRunning = false
            lblTimer.style = "-fx-font-family:'DSEG7 Modern';-fx-text-fill: black;"
            resetTimer()
            registerSolve()
        }
    }

    override fun handle(event: KeyEvent) {
        if (event.code == KeyCode.SPACE) {
            toggleTimer()
        }
    }

    fun mnuDNFClick() {
        lstSolves.selectionModel.selectedItem.dnf = mnuDNF.isSelected
        lstSolves.refresh()
        mnuPlusTwo.isDisable = mnuDNF.isSelected
        isSaved = false
        calculateStatistics()
    }

    fun mnuPlusTwoClick() {
        lstSolves.selectionModel.selectedItem.plusTwo = mnuPlusTwo.isSelected
        lstSolves.refresh()
        mnuDNF.isDisable = mnuPlusTwo.isSelected
        isSaved = false
        calculateStatistics()
    }

    private fun saveSolves() {
        val solveArray = JSONArray()
        for (solve in lstSolves.items)
            solveArray.put(solve.serialize())
        val file = File("solves.json")
        if (file.exists())
            file.delete()
        file.writeText(solveArray.toString(4))
        isSaved = true
    }

    fun mnuSaveClick() {
        if (isSaved)
            return

        try {
            saveSolves()
            AlertFactory.showAndWait("Saved!")
        } catch (e: IOException) {
            AlertFactory.showAndWait("ERROR: IOException! (Couldn't save solves!)", Alert.AlertType.ERROR)
        }
    }

    fun mnuDeleteClick() {
        if (lstSolves.selectionModel.selectedItem != null) {
            val solveStr = lstSolves.selectionModel.selectedItem.toString()
            val dialog = ConfirmationDialogFactory.yesNoDialog(
                "Are you sure you would like to delete this solve? ($solveStr)",
                "Confirm delete",
                hasCancel = false
            )
            val result = dialog.showAndWait()
            if (result.isPresent) {
                if (result.get()) {
                    lstSolves.items.remove(lstSolves.selectionModel.selectedItem)
                    lstSolves.refresh()
                    calculateStatistics()
                }
            }
        }
    }

    fun mnuSolveDataClick() {
        val file: URL = javaClass.classLoader.getResource("fxml/solveData.fxml") ?: exitProcess(-1)
        val root: Parent = FXMLLoader.load(file)
        val scene = Scene(root)
        val stage = Stage()
        stage.title = "Solve Data Viewer"
        stage.scene = scene

        SolveDataController.instance?.solves = ArrayList(lstSolves.items)
        SolveDataController.instance?.addSolves()

        stage.showAndWait()
    }
}