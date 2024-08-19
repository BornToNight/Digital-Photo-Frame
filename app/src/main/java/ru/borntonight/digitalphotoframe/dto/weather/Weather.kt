package ru.borntonight.digitalphotoframe.dto.weather

data class Weather(
    val description: String,
    val icon: String,
    val id: Int,
    val main: String
)