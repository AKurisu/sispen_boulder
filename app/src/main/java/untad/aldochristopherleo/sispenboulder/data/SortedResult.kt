package untad.aldochristopherleo.sispenboulder.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class SortedResult(val name: String? = null, val preferenceValue: Double? = null, val result: Result? = null,
                   val wall1: Result = Result(),
                   val wall2: Result = Result(),
                   val wall3: Result = Result(),
                   val wall4: Result = Result(),
                   val wall5: Result? = null,
                   var position: Int? = null,
                   val key: String = "",
                   var listOfWall: ArrayList<Int> = ArrayList(),
                   val order: Int= -1
)  : Parcelable