package untad.aldochristopherleo.sispenboulder.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class SortedResult(val name: String? = null, val preferenceValue: Double? = null, val result: Result? = null) : Parcelable