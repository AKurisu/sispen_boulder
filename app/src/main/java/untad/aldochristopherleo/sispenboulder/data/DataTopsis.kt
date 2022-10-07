package untad.aldochristopherleo.sispenboulder.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DataTopsis(val norms: ArrayList<Double>? = null, val priorityNorms: ArrayList<Double>? = null,
val dPos: ArrayList<Double>? = null, val dNeg: ArrayList<Double>? = null): Parcelable