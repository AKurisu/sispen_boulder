package untad.aldochristopherleo.sispenboulder.util

import java.util.*

class Ahp(value: ArrayList<Double>){

    private var valueList = value
    private var list = ArrayList<Double>()
    private var normalizeList = ArrayList<Double>()
    private var consistencyList = ArrayList<Double>()

    private var sum1 = 0.0
    private var sum2 = 0.0
    private var sum3 = 0.0
    private var sum4 = 0.0

    private var sumNormalize1 = 0.0
    private var sumNormalize2 = 0.0
    private var sumNormalize3 = 0.0
    private var sumNormalize4 = 0.0

    private var priority1 = 0.0
    private var priority2 = 0.0
    private var priority3 = 0.0
    private var priority4 = 0.0

    private var consistencySum1 = 0.0
    private var consistencySum2 = 0.0
    private var consistencySum3 = 0.0
    private var consistencySum4 = 0.0

    private var eigen = 0.0
    private var consistencyIndex = 0.0
    private var consistencyRatio = 0.0

    init {
        countAlgorithm(valueList)
    }

    private fun countAlgorithm(valueList: ArrayList<Double>) {

        //Input Nilai Awal
        valueList.indices.forEach { index ->
            if (index % 4 != 0){
                list.add(valueList[index])
            } else {
                Collections.addAll(list, 1.00, valueList[index])
            }
            if ((index + 1) == valueList.size){
                list.add(1.00)
            }
        }

        //Cari Nilai Pembagi
        list.indices.forEach { index ->
            when {
                index % 4 == 0 -> {
                    sum1 += list[index]
                }
                index % 4 == 1 -> {
                    sum2 += list[index]
                }
                index % 4 == 2 -> {
                    sum3 += list[index]
                }
                index % 4 == 3 -> {
                    sum4 += list[index]
                }
            }
        }
        println(list)
        println(sum1.toString() + ' ' + sum2.toString() + ' ' + sum3.toString() + ' ' + sum4.toString())


        //Normalisasi Nilai
        list.indices.forEach { index ->
            when {
                index % 4 == 0 -> {
                    normalizeList.add(list[index] / sum1)
                }
                index % 4 == 1 -> {
                    normalizeList.add(list[index] / sum2)
                }
                index % 4 == 2 -> {
                    normalizeList.add(list[index] / sum3)
                }
                index % 4 == 3 -> {
                    normalizeList.add(list[index] / sum4)
                }
            }
        }

        normalizeList.indices.forEach { index ->
            when {
                index < 4 -> {
                    sumNormalize1 += normalizeList[index]
                }
                index < 8 -> {
                    sumNormalize2 += normalizeList[index]
                }
                index < 12 -> {
                    sumNormalize3 += normalizeList[index]
                }
                index < 16 -> {
                    sumNormalize4 += normalizeList[index]
                }
            }
        }

        priority1 = sumNormalize1 / 4
        priority2 = sumNormalize2 / 4
        priority3 = sumNormalize3 / 4
        priority4 = sumNormalize4 / 4

        list.indices.forEach { index ->
            when {
                index % 4 == 0 -> {
                    consistencyList.add(priority1 * list[index])
                }
                index % 4 == 1 -> {
                    consistencyList.add(priority2 * list[index])
                }
                index % 4 == 2 -> {
                    consistencyList.add(priority3 * list[index])
                }
                index % 4 == 3 -> {
                    consistencyList.add(priority4 * list[index])
                }
            }
        }

        consistencyList.indices.forEach { index ->
            when {
                index < 4 -> {
                    consistencySum1 += consistencyList[index]
                }
                index < 8 -> {
                    consistencySum2 += consistencyList[index]
                }
                index < 12 -> {
                    consistencySum3 += consistencyList[index]
                }
                index < 16 -> {
                    consistencySum4 += consistencyList[index]
                }
            }
        }


        eigen = ((consistencySum1 / priority1) + (consistencySum2 / priority2) + (consistencySum3 / priority3) + (consistencySum4 / priority4)).div(4)

        consistencyIndex = (eigen - 4).div(3)

        consistencyRatio = consistencyIndex.div(0.9)
        println(consistencyRatio)
    }

    fun getFirstMatrix(): ArrayList<Double> = list

    fun getFirstSum(): ArrayList<Double> {
        val list = ArrayList<Double>()
        Collections.addAll(list, sum1, sum2, sum3, sum4)
        return list
    }

    fun getNormalizeMatrix(): ArrayList<Double> = normalizeList

    fun getNormalizeSum(): ArrayList<Double> {
        val list = ArrayList<Double>()
        Collections.addAll(list, sumNormalize1, sumNormalize2, sumNormalize3, sumNormalize4,
            priority1, priority2, priority3, priority4)
        return list
    }

    fun getPrioritySum():ArrayList<Double>{
        val list = ArrayList<Double>()
        Collections.addAll(list, priority1, priority2, priority3, priority4)
        return list
    }

    fun getConsistencyMatrix(): ArrayList<Double> = consistencyList

    fun getConsistencySum(): ArrayList<Double> {
        val list = ArrayList<Double>()
        Collections.addAll(list, consistencySum1, consistencySum2, consistencySum3, consistencySum4)
        return list
    }

    fun getResults(index: Int) : Double {
        var result = 0.0
        if (index == 1) {
            result = eigen
        } else if (index == 2){
            result = consistencyIndex
        } else if (index == 3){
            result = consistencyRatio
        }
        return result
    }
}