package com.example.bmi


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer



@Composable
fun TipsScreen(name: String, weight: Double, bmi: Double, gender: String, navController: NavController) {

    var aiText by remember { mutableStateOf("Loading tips...") }


    LaunchedEffect(Unit) {

        val prompt = """
Act as a highly empathetic, supportive, and motivating wellness coach.

You are given a user's health profile. 
First determine their BMI category using standard BMI ranges:
- Below 18.5 → Underweight
- 18.5 – 24.9 → Normal
- 25 – 29.9 → Overweight
- 30+ → Obese

User Profile:
- Name: $name
- Weight: $weight kg
- BMI: $bmi
- Gender: $gender

Instructions:

1. Greet the user warmly using their name.
2. Mention their BMI category in a positive and non-judgmental tone.
3. Provide:
   - 3 personalized physical health tips
   - 1 mental wellness tip
   - 1 small achievable goal for this week
4. Adjust tone slightly based on gender:
   - Male → focus slightly on strength, stamina, and muscle health
   - Female → focus slightly on balanced fitness and overall wellness
   - Other → use an inclusive and empowering tone
5. If gender is "Other", add one short line encouraging confidence, individuality, and self-respect.
6. Keep the message under 180 words.
7. End with a short uplifting motivational sentence.
8. Format the response clearly using emojis and bullet points. Separate sections clearly. 
9. dont use * 

Do not sound robotic. Keep it friendly and human.
""".trimIndent()

        GroqAi.getTips(prompt) { result ->
            aiText = result
        }

    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(3.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            18.dp,
            Alignment.CenterVertically
        )) {
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "✨ Your AI Wellness Tips",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            style = androidx.compose.ui.text.TextStyle(
                brush = Brush.horizontalGradient(
                    listOf(
                        Color(0xFF7B1FA2),
                        Color(0xFF2196F3)
                    )
                )
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (aiText == "Loading tips...") {

            CircularProgressIndicator()

        } else {

            AnimatedVisibility(
                visible = aiText != "Loading",
                enter = fadeIn() + slideInVertically(
                    initialOffsetY = { it / 2 }
                )
            ) {

                val infiniteTransition = rememberInfiniteTransition(label = "")

                val animatedShift by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 800f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(6000, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    ),
                    label = ""
                )

                val rainbowBrush = Brush.linearGradient(
                    colors = listOf(
                        Color.Red,
                        Color(0xFFFFA500),
                        Color.Yellow,
                        Color.Green,
                        Color.Blue,
                        Color(0xFF4B0082),
                        Color(0xFF8F00FF)
                    ),
                    start = Offset(animatedShift, 0f),
                    end = Offset(animatedShift + 600f, 600f)
                )

                Card(
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (gender == "Other")
                            Color.Transparent
                        else
                            Color.White
                    ),
                    elevation = CardDefaults.cardElevation(12.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .background(
                            if (gender == "Other") rainbowBrush
                            else Brush.horizontalGradient(
                                listOf(Color.White, Color.White)
                            ),
                            shape = RoundedCornerShape(28.dp)
                        )
                ) {

                    Column(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                            .background(

                                if (gender == "Other")
                                    Color.White.copy(alpha = 0.9f)
                                else
                                    Color.White
                            )
                            .padding(8.dp)
                    ) {
                        Text(
                            text = aiText,
                            fontSize = 16.sp,
                            lineHeight = 24.sp,
                            color = Color(0xFF333333)
                        )
                    }
                }

            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { navController.popBackStack() },
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent
            ),
            modifier = Modifier.background(
                Brush.horizontalGradient(
                    listOf(
                        Color(0xFF7B1FA2),
                        Color(0xFF2196F3)
                    )
                ),
                shape = RoundedCornerShape(50)
            )
        ) {
            Text("← Go Back", color = Color.White)
        }
    }
}



