package ru.borntonight.digitalphotoframe.dto.weather

data class Wind(
    val deg: Int,
    val gust: Double?,
    val speed: Double
)