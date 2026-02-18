package com.example.bmi.presentation.profile

data class ProfileFormState(
    val weight: String = "",
    val height: String = "",
    val gender: String = "",
    val weightError: String? = null,
    val heightError: String? = null,
    val isValid: Boolean = false
)