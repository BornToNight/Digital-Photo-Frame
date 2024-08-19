package ru.borntonight.digitalphotoframe.util

object AppConstants {
    const val SHARED_PREF_VALUE = "Фоторамка"
    const val SELECTED_PHOTO_FOLDER_KEY = "selectedPhotoFolderKey"
    const val GEOLOCATION_KEY = "geolocation"
    const val CITY_GEOLOCATION_KEY = "cityGeolocation"
    const val SLIDE_SHOW_DELAY_KEY = "slideShowDelay"
    const val UPDATE_WEATHER_DELAY_KEY = "updateWeatherDelay"
    const val SHUFFLE_PHOTO_KEY = "shufflePhoto"
    const val DEFAULT_SLIDE_SHOW_DELAY = 20_000L // 20 секунд
    const val DEFAULT_UPDATE_DATE_AND_TIME_DELAY = 60_000L // 1 минута
    const val DEFAULT_UPDATE_WEATHER_DELAY = 600_000L // 10 минут
    const val API_WEATHER_TOKEN = ""
}