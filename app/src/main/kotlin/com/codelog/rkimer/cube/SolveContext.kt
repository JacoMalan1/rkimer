package com.codelog.rkimer.cube

import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.*

object SolveContext {
    private var solves: MutableMap<CubeType, List<Solve>> = EnumMap(CubeType.c33.javaClass)

    fun getSolves(cubeType: CubeType): List<Solve> = solves[cubeType] ?: ArrayList()

    fun loadSolves(fileName: String) {
        val file = File(fileName)
        if (!file.exists())
            return

        val contents = file.readText()
        if (contents[0] == '[') {
            val jsonArray = JSONArray(contents)
            val solveList = ArrayList<Solve>()
            for (i in 0 until jsonArray.length())
                solveList.add(Solve.fromJSONObject(jsonArray.getJSONObject(i)))
            solves[CubeType.c33] = solveList
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
        }
    }
}