package ru.borntonight.digitalphotoframe.util

import ru.borntonight.digitalphotoframe.dto.Geolocation

enum class CityGeolocationEnum(val id: Int, val cityName: String, val geolocation: Geolocation) {
    PERM(0, "Пермь", Geolocation("57.98", "56.20")),
    SHERYA(1, "Шерья", Geolocation("58.01", "55.20"));

    companion object {
        fun getCityGeoEnumByCityName(cityName: String): CityGeolocationEnum =
            entries.find { it.cityName == cityName } ?: PERM
    }
}