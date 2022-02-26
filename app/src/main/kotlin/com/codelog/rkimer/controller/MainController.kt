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
import javafx.stage.FileChooser
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.util.StringConverter
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.net.URL
import java.util.*
import kotlin.math.floor
import kotlin.math.round
import kotlin.system.exitProcess
import com.codelog.rkimer.util.KLoggerContext as Logger

class MainController: Initializable, EventHandler<KeyEvent> {

    private var timer: Timer = Timer()
    private var time: Int = 0
    private var cubeType = CubeType.C33

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
    @FXML
    lateinit var tglCubeType: ToggleGroup

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

        val converted = SolveContext.loadSolves("solves.json")

        loadSolves(CubeType.C33)

        if (converted) {
            AlertFactory.showAndWait("Your saved times have been converted from an old format (pre v0.1.0-BETA). " +
                    "Should anything have gone wrong, please contact the developer. " +
                    "Your old solves have been backed up as 'solves_backup.json'")
            saveSolves()
        }

        isSaved = true

        tglCubeType.selectedToggleProperty().addListener { _, _, newValue ->
            val oldCubeType = cubeType

            val idxNew = tglCubeType.toggles.indexOf(newValue)
            for (ct in CubeType.values())
                if (ct.cubeRank() == idxNew)
                    cubeType = ct

            val cubeName = cubeType.cubeName()
            App.currentStage?.title = "RKimer - $cubeName"

            loadSolves(oldCubeType)
            resetTimer()
        }

        lblTimer.text = "00:00.00"
        lblTimer.style = "-fx-font-family:'DSEG7 Modern'"
        txtStats.isDisable = true
        resetTimer()
        calculateStatistics(lstSolves.items)
        instance = this
    }

    private fun loadSolves(oldCubeType: CubeType) {
        if (lstSolves.items.size > 0)
            SolveContext[oldCubeType] = ArrayList(lstSolves.items)

        lstSolves.items.clear()

        lstSolves.items.addAll(SolveContext[cubeType])
        if (lstSolves.items.size > 0)
            lstSolves.selectionModel.select(0)
        calculateStatistics(SolveContext[cubeType])
    }

    fun tickTimer() {
        time++
        lblTimer.text = timeToStr(time)
    }

    private fun resetTimer() {
        val scrambleLength = when (cubeType) {
            CubeType.C22 -> 11
            CubeType.C33 -> 20
            CubeType.C44 -> 40
            CubeType.C55 -> 40
            // else -> 20
        }
        scramble = ScrambleFactory.generateScramble(scrambleLength, cubeType)
        val imgProvider: ImageProvider = ScrambleImageProvider(scramble, 250.0, 250.0)
        imgScramble.image = imgProvider.provide()

        lblScramble.text = scramble.toString()
    }

    private fun registerSolve() {
        lstSolves.items.add(0, Solve(time, dnf = false, plusTwo = false, scramble = scramble))
        isSaved = false
        calculateStatistics(lstSolves.items)
    }

    private fun calculateStatistics(solveList: List<Solve>) {
        if (solveList.isEmpty()) {
            val builder = StringBuilder()
            builder.append("Best: N/A\n")
            builder.append("Mean of 3: N/A\n")
            builder.append("Average of 5: N/A")
            txtStats.text = builder.toString()
            return
        }

        val solves = ArrayList<Solve>()
        solves.addAll(solveList)
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
        if (event.code == KeyCode.RIGHT) {
            val idx = tglCubeType.toggles.indexOf(tglCubeType.selectedToggle)
            tglCubeType.toggles[(idx + 1) % tglCubeType.toggles.size].isSelected = true
        }
    }

    fun mnuDNFClick() {
        lstSolves.selectionModel.selectedItem.dnf = mnuDNF.isSelected
        lstSolves.refresh()
        mnuPlusTwo.isDisable = mnuDNF.isSelected
        isSaved = false
        calculateStatistics(lstSolves.items)
    }

    fun mnuPlusTwoClick() {
        lstSolves.selectionModel.selectedItem.plusTwo = mnuPlusTwo.isSelected
        lstSolves.refresh()
        mnuDNF.isDisable = mnuPlusTwo.isSelected
        isSaved = false
        calculateStatistics(lstSolves.items)
    }

    private fun saveSolves() {
        SolveContext[cubeType] = ArrayList(lstSolves.items)
        SolveContext.writeSolves("solves.json")
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
                    calculateStatistics(lstSolves.items)
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

    fun mnuImportCsTimerClick() {
        val stage = Stage()
        stage.initOwner(App.currentStage)
        stage.initModality(Modality.APPLICATION_MODAL)

        val fileChooser = FileChooser()
        fileChooser.title = "Open csTimer data"
        val file = fileChooser.showOpenDialog(stage)

        val contents = file.readText()
        try {
            val json = JSONObject(contents)
            val solves = ArrayList<Solve>()
            for (key in json.keySet()) {
                if (key.contains("session")) {
                    val jsonArray = json.getJSONArray(key)
                    for (i in 0 until jsonArray.length()) {
                        val jsonSolve = jsonArray.getJSONArray(i)
                        val flag = jsonSolve.getJSONArray(0).getInt(0)
                        var dnf = false
                        var plusTwo = false
                        if (flag == 2000)
                            plusTwo = true
                        else if (flag == -1)
                            dnf = true

                        val time = floor(jsonSolve.getJSONArray(0).getInt(1) / 10.toDouble()).toInt()
                        val scrambleStr = jsonSolve.getString(1)
                        val scramble = Scramble.fromString(scrambleStr)

                        solves.add(Solve(time, dnf, plusTwo, scramble))
                    }
                }
            }
            lstSolves.items.addAll(0, solves)
            isSaved = false
        } catch (e: JSONException) {
            Logger.error("Couldn't load file (${file.absolutePath})")
            Logger.exception(e)
            AlertFactory.showAlert("The file you selected does not contain valid JSON!", Alert.AlertType.ERROR)
        }
    }
}