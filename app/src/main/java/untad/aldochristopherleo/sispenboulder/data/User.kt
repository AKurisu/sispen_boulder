package untad.aldochristopherleo.sispenboulder.data

import android.os.Parcelable
import com.google.firebase.database.Exclude
import kotlinx.parcelize.Parcelize
import kotlin.reflect.typeOf

@Parcelize
data class User(val name: String? = null, val email: String? = null, val type: String? = null):Parcelable{
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "name" to name,
            "email" to email,
            "type" to type
        )
    }
}
