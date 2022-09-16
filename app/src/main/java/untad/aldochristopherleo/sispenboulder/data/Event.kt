package untad.aldochristopherleo.sispenboulder.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*
import kotlin.collections.HashMap

@Parcelize
data class Event(val name: String = "",
                 val date: Long = 0,
                 val location: String = "",
                 val finished: Boolean = false,
                 val totalParticipant: Int = 0,
                 val president: String = "",
                 val status: String? = null,
                 val judges: HashMap<String, Judge>? = null,
                 val participant:HashMap<String, Participant>? = null): Parcelable
