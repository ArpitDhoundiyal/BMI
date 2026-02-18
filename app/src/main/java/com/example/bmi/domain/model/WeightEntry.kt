package com.example.bmi.domain.model

data class WeightEntry(
    val id: String,
    val userId: String = "",
    val weight: Double,
    val timestamp: Long,
    val accountId: String = "",
    val profileId: String = "",
)