package untad.aldochristopherleo.sispenboulder.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Alternative(val name: String? = null, val phone: Int? = null) : Parcelable
