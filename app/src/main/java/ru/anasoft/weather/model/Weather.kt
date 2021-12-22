package ru.anasoft.weather.model

data class Weather(val city:City = getDefaultCity(), val temperature:Int=-18, val feels:Int=-19)

data class City(val name:String, val ln:Double, val lt:Double)

fun getDefaultCity() = City("Москва",37.5,55.5)


