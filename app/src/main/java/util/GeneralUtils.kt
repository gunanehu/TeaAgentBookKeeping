package util

import java.text.SimpleDateFormat
import java.util.*

class GeneralUtils {
    companion object {
        fun convertDate(timestamp: Long): String {
            val timeD =
                Date(timestamp)
            val sdf = SimpleDateFormat("dd/M/yyyy")
            val time: String = sdf.format(timeD)
            return time
        }
    }
}