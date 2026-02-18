package com.example.bmi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.bmi.presentation.navigation.AppNavGraph
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val auth = FirebaseAuth.getInstance()

            val startDestination =
                if (auth.currentUser != null) "home"
                else "login"

            AppNavGraph(startDestination = startDestination)
        }
    }
}





