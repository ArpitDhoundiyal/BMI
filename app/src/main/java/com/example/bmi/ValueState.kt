package com.example.bmi

data class ValueState(
    val label: String, // lable
    val prefix: String,// units
    val value: String = "", // A string representing the user's input value.
    val error: String? = null
) {
    fun toNumber() = value.toDoubleOrNull()
}