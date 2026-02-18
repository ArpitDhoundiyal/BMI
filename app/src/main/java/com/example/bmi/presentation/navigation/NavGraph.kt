package com.example.bmi.presentation.navigation

import android.app.Activity
import android.net.Uri
import com.example.bmi.ui.screens.RegisterScreen


import android.widget.Toast
import com.example.bmi.domain.model.Profile
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.bmi.TipsScreen
import com.example.bmi.presentation.profile.ProfileScreen
import com.example.bmi.presentation.auth.LoginScreen
import com.example.bmi.presentation.bmi.BmiScreen
import com.example.bmi.ui.screens.HomeScreen
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.firestore.FirebaseFirestore


@Composable
fun AppNavGraph(startDestination: String) {

    val navController = rememberNavController()
    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        // ---------------- REGISTER ----------------

        composable("register") {

            RegisterScreen(

                onRegisterClick = { username: String, email: String, password: String ->

                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnSuccessListener { result ->

                            val uid = result.user?.uid

                            uid?.let {

                                val userMap = mapOf(
                                    "name" to username,
                                    "email" to email
                                )

                                FirebaseFirestore.getInstance()
                                    .collection("users")
                                    .document(it)
                                    .set(userMap)

                                navController.navigate("home") {
                                    popUpTo("register") { inclusive = true }
                                }
                            }
                        }

                        .addOnFailureListener {
                            Toast.makeText(context, "Register Failed", Toast.LENGTH_SHORT).show()
                        }
                },

                onNavigateToLogin = {
                    navController.navigate("login")
                }
            )
        }

        // ---------------- LOGIN ----------------

        composable("login") {

            LoginScreen(

                onLoginClick = { email, password ->

                    auth.signInWithEmailAndPassword(email, password)
                        .addOnSuccessListener {
                            navController.navigate("home") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                        .addOnFailureListener { exception ->

                            val errorMessage = when (exception) {
                                is FirebaseAuthInvalidUserException -> "User not registered"
                                is FirebaseAuthInvalidCredentialsException -> "Wrong password"
                                else -> "Login Failed"
                            }

                            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                        }
                },

                onGoogleLoginClick = {

                    val googleSignInClient = GoogleSignIn.getClient(
                        context,
                        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken("188754450709-rtghdrqpcnkhd4j684jjbqb4baqc4fs6.apps.googleusercontent.com")
                            .requestEmail()
                            .build()
                    )

                    val signInIntent = googleSignInClient.signInIntent
                    (context as Activity).startActivityForResult(signInIntent, 1001)
                },

                onNavigateToRegister = {
                    navController.navigate("register")
                }
            )
        }


        // ---------------- PROFILE CREATE ----------------

        composable("profile") {

            ProfileScreen(
                onProfileSaved = {
                    navController.navigate("home") {
                        popUpTo("profile") { inclusive = true }
                    }
                }
            )
        }

        composable("bmi/{profileId}/{name}") { backStackEntry ->

            val profileId = backStackEntry.arguments?.getString("profileId")?:""
            val name = backStackEntry.arguments?.getString("name") ?: ""

            BmiScreen(
                profileId = profileId,
                name = name,
                navController = navController
            )
        }
        composable("tips/{name}/{weight}/{bmi}") { backStackEntry ->

            val name = backStackEntry.arguments?.getString("name") ?: ""
            val weight = backStackEntry.arguments?.getString("weight")?.toDoubleOrNull() ?: 0.0
            val bmi = backStackEntry.arguments?.getString("bmi")?.toDoubleOrNull() ?: 0.0

            TipsScreen(name, weight, bmi, navController)
        }



        // ---------------- HOME ----------------

        composable("home") {

            val uid = auth.currentUser?.uid
            var username by remember { mutableStateOf("") }
            var profiles by remember { mutableStateOf<List<Profile>>(emptyList()) }

            LaunchedEffect(Unit) {

                // Fetch username
                FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(uid!!)
                    .get()
                    .addOnSuccessListener { document ->
                        username = document.getString("name") ?: ""
                    }

                // Fetch profiles
                FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(uid)
                    .collection("profiles")
                    .addSnapshotListener { snapshot, _ ->

                        snapshot?.let {

                            profiles = it.documents.map { document ->
                                Profile(
                                    id = document.id,
                                    name = document.getString("name") ?: "",
                                    gender = document.getString("gender") ?: "",
                                    currentBmi = document.getDouble("currentBmi")
                                )
                            }
                        }
                    }
                }


                HomeScreen(
                username = username,
                profiles = profiles,
                onAddClick = {
                    navController.navigate("profile")
                },
                    onProfileClick = { profileId, name ->
                        navController.navigate(
                            "bmi/$profileId/${Uri.encode(name)}"
                        )
                    },
                onLogoutClick = {
                    auth.signOut()

                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                onDeleteClick = { profileId ->

                    val uid = auth.currentUser?.uid ?: return@HomeScreen

                    FirebaseFirestore.getInstance()
                        .collection("users")
                        .document(uid)
                        .collection("profiles")
                        .document(profileId)
                        .delete()
                }


            )


        }
        composable("profile") {
            ProfileScreen(
                onProfileSaved = {
                    navController.popBackStack()
                }
            )
        }

    }
}

