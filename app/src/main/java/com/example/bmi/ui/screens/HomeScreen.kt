package com.example.bmi.ui.screens

import androidx.compose.foundation.clickable
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bmi.domain.model.Profile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


@Composable
fun HomeScreen(
    username: String,
    profiles: List<Profile>,
    onAddClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onProfileClick: (String, String) -> Unit,
    onDeleteClick: (String) -> Unit
) {

    var showDialog by remember { mutableStateOf(false) }
    var selectedProfileId by remember { mutableStateOf<String?>(null) }



    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Delete Profile") },
            text = { Text("Are you sure you want to delete this profile?") },
            confirmButton = {
                TextButton(onClick = {
                    selectedProfileId?.let { onDeleteClick(it) }
                    showDialog = false
                }) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(

        floatingActionButton = {
            FloatingActionButton(
                onClick = { onAddClick() },
                containerColor = Color(0xFF2196F3),
                contentColor = Color.White,
                modifier = Modifier.padding(bottom = 60.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        },
        floatingActionButtonPosition = FabPosition.End

    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {

            Text(
                text = "Welcome $username",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )


            Spacer(modifier = Modifier.height(20.dp))

            if (profiles.isEmpty()) {

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("No profile created yet 😔")
                    Text("Tap + to create one")
                }

            } else {
                val rainbowBrush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFEF8E99),
                        Color(0xFFE6AEEC),
                        Color(0xFFA7CEF5),
                        Color(0xFFA8F6AA),

                    )
                )

                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {

                    items(profiles) { profile ->
                        val isOther = profile.gender == "Other"

                        val bmiColor = when {
                            profile.currentBmi == null -> Color.Gray
                            profile.currentBmi < 18.5 -> Color(0xFF2196F3)
                            profile.currentBmi < 25 -> Color(0xFF4CAF50)
                            profile.currentBmi < 30 -> Color(0xFFFF9800)
                            else -> Color(0xFFF44336)
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .clickable {
                                    onProfileClick(profile.id, profile.name)
                                },
                            shape = RoundedCornerShape(18.dp),
                            elevation = CardDefaults.cardElevation(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {

                            Column(
                                modifier = Modifier.padding(18.dp)
                            ) {

                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {

                                    // Profile initial circle
                                    Box(
                                        modifier = Modifier
                                            .size(55.dp)
                                            .background(
                                                bmiColor.copy(alpha = 0.15f),
                                                CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = profile.name.first().uppercase(),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 20.sp,
                                            color = bmiColor
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(16.dp))

                                    Column(
                                        modifier = Modifier.weight(1f)
                                    ) {

                                        Text(
                                            text = profile.name,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            style = if (isOther) {
                                                TextStyle(
                                                    brush = rainbowBrush
                                                )
                                            } else {
                                                TextStyle(
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        )
                                        Text(
                                            text = profile.gender,
                                            color = Color.Gray
                                        )
                                    }

                                    // BMI badge
                                    profile.currentBmi?.let {
                                        Box(
                                            modifier = Modifier
                                                .background(
                                                    bmiColor.copy(alpha = 0.15f),
                                                    RoundedCornerShape(12.dp)
                                                )
                                                .padding(
                                                    horizontal = 12.dp,
                                                    vertical = 6.dp
                                                )
                                        ) {
                                            Text(
                                                text = "BMI %.1f".format(it),
                                                color = bmiColor,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(6.dp))

                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowRight,
                                        contentDescription = null,
                                        tint = Color.Gray
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = Color.Red.copy(alpha = 0.7f),
                                        modifier = Modifier.clickable {
                                            selectedProfileId = profile.id
                                            showDialog = true
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = onLogoutClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Logout")
            }
        }
    }
}


