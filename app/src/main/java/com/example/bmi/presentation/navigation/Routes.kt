package com.example.bmi.presentation.navigation

sealed class Routes(val route: String) {
    object Login : Routes("login")
    object Home : Routes("home")
    object Profile : Routes("profile")
}