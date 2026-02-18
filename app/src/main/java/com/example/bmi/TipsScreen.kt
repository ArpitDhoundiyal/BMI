package com.example.bmi


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun TipsScreen(name: String, weight: Double, bmi: Double,navController: NavController) {

    var aiText by remember { mutableStateOf("Loading tips...") }


    LaunchedEffect(Unit) {

        val prompt = """
    Act as a highly empathetic, upbeat, and friendly wellness coach. >
I am going to provide you with a user's health profile. Please write a personalized wellness message based strictly on their details.
    
    User Profile:
    - Name: $name
    - Weight: $weight kg
    - BMI: $bmi
    - Gender: ${/* Pass gender here if you have it, otherwise remove this line */ "Not specified"}
    
    Task:
    1. Greet the user warmly by name.
    2. explain their BMI category (Underweight, Healthy, Overweight, or Obese) in a non-judgmental way.
    3. Provide 3 simple, actionable diet tips specific to their BMI.
    4. Provide 3 easy exercise ideas suitable for their weight class.
    5. End with a short, punchy motivational quote.
    
    Formatting Rules:
    - Use a few emojis to make it visually appealing, but don't overdo it.

Use bullet points for the diet and exercise lists.

Keep sentences short, punchy, and easy to read on a mobile phone screen.

Tone: Casual, positive, friendly, highly personalized, and encouraging. Never sound clinical or preachy
""".trimIndent()

        GroqAi.getTips(prompt) { result ->
            aiText = result
        }

    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "💡 Your Personal Tips",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (aiText == "Loading") {

            CircularProgressIndicator()

        } else {


            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(15.dp)
            ) {
                Text(
                    text = aiText,
                    modifier = Modifier.padding(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = { navController.popBackStack() }
            ) {
                Text("Go Back")
            }
        }

    }
}
