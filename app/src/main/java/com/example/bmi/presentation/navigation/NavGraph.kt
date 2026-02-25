package com.example.bmi.presentation.navigation

import android.app.Activity
import android.net.Uri
import com.example.bmi.ui.screens.RegisterScreen

import com.google.android.gms.common.api.ApiException
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.bmi.R
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException


@Composable
fun AppNavGraph(startDestination: String) {

    val navController = rememberNavController()
    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current
    val clientId = stringResource(R.string.default_web_client_id)

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        // ---------------- REGISTER ----------------

        composable("register") {

            RegisterScreen(
                onRegisterClick = { username, email, password ->

                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnSuccessListener { result ->

                            val user = result.user

                            // Send verification email
                            user?.sendEmailVerification()

                            val uid = user?.uid

                            uid?.let {
                                val firstName = username
                                    .trim()
                                    .split(" ")
                                    .firstOrNull()
                                    .orEmpty()

                                val userMap = mapOf(
                                    "name" to firstName,
                                    "email" to email
                                )

                                FirebaseFirestore.getInstance()
                                    .collection("users")
                                    .document(it)
                                    .set(userMap)
                            }

                            Toast.makeText(
                                context,
                                "Verification email sent. Please verify before login.",
                                Toast.LENGTH_LONG
                            ).show()

                            auth.signOut()

                            navController.navigate("login") {
                                popUpTo("register") { inclusive = true }
                            }
                        }
                        .addOnFailureListener { exception ->

                            val errorMessage = when (exception) {

                                is FirebaseAuthUserCollisionException ->
                                    "Email already registered"

                                is FirebaseAuthWeakPasswordException ->
                                    "Password should be at least 6 characters"

                                is FirebaseAuthInvalidCredentialsException ->
                                    "Invalid email format"

                                else ->
                                    "Register Failed"
                            }

                            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                        }
                },
                onNavigateToLogin = {
                    navController.navigate("login")
                }
            )
        }

        // ---------------- LOGIN ----------------

        composable("login") {

            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartActivityForResult()
            ) { result ->

                if (result.resultCode == Activity.RESULT_OK) {

                    val task = GoogleSignIn
                        .getSignedInAccountFromIntent(result.data)

                    try {
                        val account = task.getResult(ApiException::class.java)

                        val credential = GoogleAuthProvider
                            .getCredential(account.idToken, null)

                        auth.signInWithCredential(credential)
                            .addOnCompleteListener { loginTask ->

                                if (loginTask.isSuccessful) {

                                    val user = auth.currentUser
                                    val uid = user?.uid

                                    val fullName = user?.displayName ?: ""
                                    val firstName = fullName
                                        .trim()
                                        .split(" ")
                                        .firstOrNull()
                                        .orEmpty()

                                    val userMap = mapOf(
                                        "name" to firstName,
                                        "email" to (user?.email ?: "")
                                    )

                                    uid?.let {
                                        FirebaseFirestore.getInstance()
                                            .collection("users")
                                            .document(it)
                                            .set(userMap)
                                    }

                                    navController.navigate("home") {
                                        popUpTo("login") { inclusive = true }
                                    }

                                } else {
                                    Toast.makeText(
                                        context,
                                        "Google Login Failed",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                    } catch (e: Exception) {
                        Toast.makeText(
                            context,
                            "Google Sign-In Error",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            LoginScreen(

                onLoginClick = { email, password ->

                    // Empty validation
                    if (email.isBlank()) {
                        Toast.makeText(context, "Email required", Toast.LENGTH_SHORT).show()
                        return@LoginScreen
                    }

                    if (password.isBlank()) {
                        Toast.makeText(context, "Password required", Toast.LENGTH_SHORT).show()
                        return@LoginScreen
                    }

                    // Email format validation
                    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        Toast.makeText(context, "Enter valid email", Toast.LENGTH_SHORT).show()
                        return@LoginScreen
                    }

                    // Firebase login
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnSuccessListener {

                            val user = auth.currentUser

                            if (user?.isEmailVerified == true) {

                                navController.navigate("home") {
                                    popUpTo("login") { inclusive = true }
                                }

                            } else {

                                Toast.makeText(
                                    context,
                                    "Please verify your email first",
                                    Toast.LENGTH_SHORT
                                ).show()

                                auth.signOut()
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
                            .requestIdToken(clientId)
                            .requestEmail()
                            .build()
                    )

                    launcher.launch(googleSignInClient.signInIntent)
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

        composable(
            route = "bmi/{profileId}/{name}/{gender}"
        ) { backStackEntry ->

            val profileId = backStackEntry.arguments?.getString("profileId") ?: ""
            val name = backStackEntry.arguments?.getString("name") ?: ""
            val gender = backStackEntry.arguments?.getString("gender") ?: "Other"

            BmiScreen(
                profileId = profileId,
                name = name,
                gender = gender,
                navController = navController
            )
        }
        composable(
            route = "tips/{name}/{weight}/{bmi}/{gender}",
            arguments = listOf(
                navArgument("name") { type = NavType.StringType },
                navArgument("weight") { type = NavType.StringType },
                navArgument("bmi") { type = NavType.StringType },
                navArgument("gender") { type = NavType.StringType }
            )
        ) { backStackEntry ->

            val name = backStackEntry.arguments?.getString("name") ?: ""

            val weight = backStackEntry.arguments
                ?.getString("weight")
                ?.toDoubleOrNull() ?: 0.0

            val bmi = backStackEntry.arguments
                ?.getString("bmi")
                ?.toDoubleOrNull() ?: 0.0

            val gender = backStackEntry.arguments
                ?.getString("gender") ?: "Other"

            TipsScreen(name, weight, bmi, gender, navController)
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
                    onProfileClick = { id, name, gender ->
                        navController.navigate(
                            "bmi/$id/${Uri.encode(name)}/$gender"
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

