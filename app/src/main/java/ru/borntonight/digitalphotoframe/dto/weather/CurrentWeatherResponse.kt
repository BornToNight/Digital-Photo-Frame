package ru.borntonight.digitalphotoframe.dto.weather

data class CurrentWeatherResponse(
    val base: String,
    val clouds: Clouds?,
    val cod: Int?,
    val coord: Coord?,
    val dt: Int?,
    val id: Int,
    val main: Main,
    val name: String,
    val rain: Rain?,
    val snow: Snow?,
    val sys: Sys,
    val timezone: Int?,
    val visibility: Int,
    val weather: List<Weather>,
    val wind: Wind
)