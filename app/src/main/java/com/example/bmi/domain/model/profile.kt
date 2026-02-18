package com.example.bmi.domain.model

data class Profile(
    val id: String = "",
    val accountId: String = "",
    val name: String = "",
    val weight: Double = 0.0,
    val height: Double = 0.0,
    val gender: String = "",
    val currentBmi: Double? = null

)