package com.example.bmi.presentation.auth

data class LoginFormState(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val isValid: Boolean = false
)