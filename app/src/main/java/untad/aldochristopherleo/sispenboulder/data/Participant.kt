package untad.aldochristopherleo.sispenboulder.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class Participant(val name : String? = null, val group: String? = null) : Parcelable