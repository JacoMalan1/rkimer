package com.codelog.rkimer.cube

import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.*

object SolveContext {
    private var solves: MutableMap<CubeType, List<Solve>> = EnumMap(CubeType.c33.javaClass)

    operator fun get(cubeType: CubeType): List<Solve> = solves[cubeType] ?: ArrayList()
    operator fun set(cubeType: CubeType, solveList: List<Solve>) {
        solves[cubeType] = solveList
    }

    fun loadSolves(fileName: String): Boolean {
        val file = File(fileName)
        if (!file.exists())
            return false

        val contents = file.readText()
        if (contents[0] == '[') {
            file.copyTo(File("solves_backup.json"))
            val jsonArray = JSONArray(contents)
            val solveList = ArrayList<Solve>()
            for (i in 0 until jsonArray.length())
                solveList.add(Solve.fromJSONObject(jsonArray.getJSONObject(i)))
            solves[CubeType.c33] = solveList
            return true
        } else {
            val json = JSONObject(contents)

            for (ct in CubeType.values()) {
                if (json.has(ct.toString())) {
                    val jsonArray = json.getJSONArray(ct.toString())
                    val solveList = ArrayList<Solve>()

                    for (i in 0 until jsonArray.length())
                        solveList.add(Solve.fromJSONObject(jsonArray.getJSONObject(i)))

                    solves[ct] = solveList
                }
            }

            return false
        }
    }

    fun writeSolves(fileName: String) {
        val file = File(fileName)
        if (file.exists())
            file.delete()

        val json = JSONObject()
        for (ct in solves.keys) {
            val jsonArray = JSONArray()
            val solveList = solves[ct] ?: ArrayList()
            for (solve in solveList) {
                jsonArray.put(solve.serialize())
            }
            json.put(ct.toString(), jsonArray)
        }

        file.writeText(json.toString(4))
    }
}