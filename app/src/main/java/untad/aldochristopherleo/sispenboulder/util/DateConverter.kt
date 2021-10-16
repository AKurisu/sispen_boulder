package untad.aldochristopherleo.sispenboulder.util

import java.text.SimpleDateFormat
import java.util.*

class DateConverter(epoch: Long) {

    private var date : Date = Date(epoch)

    private fun setDateFormat(pattern: String): SimpleDateFormat =
        SimpleDateFormat(pattern, Locale.getDefault())

    fun getHomeDate(): String = setDateFormat("d MMM yyyy").format(date)

    fun getHomeTime(): String = setDateFormat("HH:mm").format(date)

    fun getDetailDateTime(): String = setDateFormat("EEEE, d MMMM yyyy HH:mm").format(date)
}