package untad.aldochristopherleo.sispenboulder.data

import android.os.Parcelable
import com.google.firebase.database.Exclude
import kotlinx.parcelize.Parcelize

@Parcelize
class Result(val top: Double = 0.0, val at: Double = 0.0, val bonus: Double = 0.0, val ab: Double = 0.0) : Parcelable{
    @Exclude
    fun toArrayList(): ArrayList<Double>{
        return arrayListOf(top, at, bonus, ab)
    }
}