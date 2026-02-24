package com.example.bmi.presentation.profile

import android.R.attr.height
import android.R.attr.name
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel(),
    onProfileSaved: () -> Unit
) {

    val auth = FirebaseAuth.getInstance()
    val uid = auth.currentUser?.uid

    var name by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Male") }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        MaterialTheme.colorScheme.background
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(10.dp)
        ) {

            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "Create Profile",
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(modifier = Modifier.height(24.dp))

                val rainbowBrush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFCE4552),
                        Color(0xFFCD83E5),
                        Color(0xFFAA8BE8),
                        Color(0xFF77B8EA),
                        Color(0xFF85F38B)

                    )
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Enter Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),   // rounded border
                    textStyle = if (gender == "Other") {
                        TextStyle(brush = rainbowBrush,
                            shadow = Shadow(
                                color = Color.White.copy(alpha = 0.4f),
                                blurRadius = 8f,
                                offset = Offset(0f, 2f)
                            )
                        )

                    } else {
                        TextStyle.Default
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Select Gender",
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {

                    FilterChip(
                        selected = gender == "Male",
                        onClick = { gender = "Male" },
                        label = { Text("Male") },
                        border = BorderStroke(
                            1.dp,
                            if (gender == "Male") Color(0xFF80BFEF) else Color.Gray
                        )

                    )

                    FilterChip(
                        selected = gender == "Female",
                        onClick = { gender = "Female" },
                        label = { Text("Female") },
                        border = BorderStroke(
                            1.dp,
                            if (gender == "Female") Color(0xFFEF48C3) else Color.Gray
                        )
                    )
                    FilterChip(
                        selected = gender == "Other",
                        onClick = { gender = "Other" },
                        label = { Text("Other") },
                        border = if (gender == "Other") {
                            BorderStroke(1.dp, rainbowBrush)
                        } else {
                            BorderStroke(1.dp, Color.Gray)
                        }
                    )
                }
                if (gender == "Other") {
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "We respect all identities 🌈",
                        fontSize = 13.sp,
                        color = Color.Gray,
                        fontStyle = FontStyle.Italic
                    )
                }

                Spacer(modifier = Modifier.height(30.dp))

                Button(
                    onClick = {
                        if (name.isBlank()) return@Button

                        val profileMap = mapOf(
                            "name" to name,
                            "gender" to gender
                        )

                        uid?.let {
                            FirebaseFirestore.getInstance()
                                .collection("users")
                                .document(it)
                                .collection("profiles")
                                .add(profileMap)
                                .addOnSuccessListener {
                                    onProfileSaved()
                                }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Save Profile")
                }
            }
        }
    }
}
