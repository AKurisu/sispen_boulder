package untad.aldochristopherleo.sispenboulder.util

import java.util.*
import kotlin.math.pow

class Topsis (ahpPriority: ArrayList<Double>, climberScore: ArrayList<Double>) {

    private val normList = ArrayList<Double>()
    private val rootNorms = ArrayList<Double>()
    private val priorityNorms = ArrayList<Double>()
    private val positivesAlt = ArrayList<Double>()
    private val negativeAlt = ArrayList<Double>()
    private val totalPreference = ArrayList<Double>()
    private val sortedTotal = ArrayList<Double>()

    private val dPlus = ArrayList<Double>()
    private val dNega = ArrayList<Double>()

    private var rootNorm1 = 0.0
    private var rootNorm2 = 0.0
    private var rootNorm3 = 0.0
    private var rootNorm4 = 0.0

    init {
        countAlgorithm(ahpPriority, climberScore)
    }

    private fun countAlgorithm(ahpPriority: ArrayList<Double>, climberScore: ArrayList<Double>) {

        climberScore.indices.forEach { index -> normList.add(climberScore[index] * climberScore[index]) }
        normList.indices.forEach { index ->
            when {
                index % 4 == 0 -> {
                    rootNorm1 += normList[index]
                }
                index % 4 == 1 -> {
                    rootNorm2 += normList[index]
                }
                index % 4 == 2 -> {
                    rootNorm3 += normList[index]
                }
                index % 4 == 3 -> {
                    rootNorm4 += normList[index]
                }
            }
        }
        rootNorm1 = kotlin.math.sqrt(rootNorm1)
        rootNorm2 = kotlin.math.sqrt(rootNorm2)
        rootNorm3 = kotlin.math.sqrt(rootNorm3)
        rootNorm4 = kotlin.math.sqrt(rootNorm4)

        climberScore.indices.forEach { index ->
            when {
                index % 4 == 0 -> {
                    rootNorms.add(climberScore[index].div(rootNorm1))
                }
                index % 4 == 1 -> {
                    rootNorms.add(climberScore[index].div(rootNorm2))
                }
                index % 4 == 2 -> {
                    rootNorms.add(climberScore[index].div(rootNorm3))
                }
                index % 4 == 3 -> {
                    rootNorms.add(climberScore[index].div(rootNorm4))
                }
            }
        }
        Collections.addAll(positivesAlt, 0.0, 999.0, 0.0, 999.0)

        Collections.addAll(negativeAlt, 999.0, 0.0, 999.0, 0.0)

        rootNorms.indices.forEach { index ->
            when {
                index % 4 == 0 -> {
                    priorityNorms.add(rootNorms[index].times(ahpPriority[0]))
                    if (priorityNorms[index] > positivesAlt[0]) positivesAlt[0] = priorityNorms[index]
                    if (priorityNorms[index] < negativeAlt[0]) negativeAlt[0] = priorityNorms[index]
                }
                index % 4 == 1 -> {
                    priorityNorms.add(rootNorms[index].times(ahpPriority[1]))
                    if (priorityNorms[index] < positivesAlt[1]) positivesAlt[1] = priorityNorms[index]
                    if (priorityNorms[index] > negativeAlt[1]) negativeAlt[1] = priorityNorms[index]
                }
                index % 4 == 2 -> {
                    priorityNorms.add(rootNorms[index].times(ahpPriority[2]))
                    if (priorityNorms[index] > positivesAlt[2]) positivesAlt[2] = priorityNorms[index]
                    if (priorityNorms[index] < negativeAlt[2]) negativeAlt[2] = priorityNorms[index]
                }
                index % 4 == 3 -> {
                    priorityNorms.add(rootNorms[index].times(ahpPriority[3]))
                    if (priorityNorms[index] < positivesAlt[3]) positivesAlt[3] = priorityNorms[index]
                    if (priorityNorms[index] > negativeAlt[3]) negativeAlt[3] = priorityNorms[index]
                }
            }
        }

        var sumPlus = 0.0
        var sumNeg = 0.0
        var indexPosNeg = 0
        priorityNorms.indices.forEach { index ->
            sumPlus += (positivesAlt[indexPosNeg] - priorityNorms[index]).pow(2)
            sumNeg += (priorityNorms[index] - negativeAlt[indexPosNeg]).pow(2)
            if (index % 4 == 3){
                indexPosNeg = 0
                dPlus.add(kotlin.math.sqrt(sumPlus))
                dNega.add(kotlin.math.sqrt(sumNeg))
                sumPlus = 0.0
                sumNeg = 0.0
            } else indexPosNeg += 1
        }

        dPlus.indices.forEach { index ->
            totalPreference.add(dNega[index].div(dNega[index] + dPlus[index]))
        }
        Collections.sort(sortedTotal, Collections.reverseOrder())
    }

    fun getDivider(): ArrayList<Double>{
        val divider = ArrayList<Double>()
        Collections.addAll(divider, rootNorm1, rootNorm2, rootNorm3, rootNorm4)
        return divider
    }

    fun getNormalization(): ArrayList<Double> = rootNorms

    fun getPriorityNormalize(): ArrayList<Double> = priorityNorms

    fun getIdealPosNeg(status: String): ArrayList<Double>{
        return if (status == "+"){
            positivesAlt
        } else negativeAlt
    }

    fun getdPosNeg(status: String): ArrayList<Double>{
        return if (status == "+"){
            dPlus
        } else dNega
    }

    fun getPreference(): ArrayList<Double> = totalPreference
}