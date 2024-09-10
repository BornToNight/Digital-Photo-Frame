package ru.borntonight.digitalphotoframe.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DateAndTimeUtils {

    public fun getDateAndTime(): String {
        return SimpleDateFormat(
            "dd.MM.yyyy",
            Locale.getDefault()
        ).format(System.currentTimeMillis()) + "\n" +
                SimpleDateFormat("HH:mm", Locale.getDefault()).format(System.currentTimeMillis())
    }

    public fun getCurrentDay(): String {
        return SimpleDateFormat("EEEE", Locale("ru", "RU")).format(System.currentTimeMillis())
            .replaceFirstChar { it.uppercaseChar() }
    }

    public fun getTime(): String {
        return SimpleDateFormat("HH:mm", Locale.getDefault()).format(System.currentTimeMillis())
    }
}