package com.example.bmi.presentation.bmi

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.bmi.domain.usecase.CalculateBmiUseCase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class BmiViewModel : ViewModel() {

    var heightState by mutableStateOf("")
    var weightState by mutableStateOf("")

    var bmi by mutableStateOf(0.0)
        private set

    var category by mutableStateOf("")
        private set
    var history by mutableStateOf<List<Double>>(emptyList())
        private set



    fun calculate() {
        val h = heightState.toDoubleOrNull() ?: return
        val w = weightState.toDoubleOrNull() ?: return

        bmi = w / ((h / 100) * (h / 100))
        category = getBmiCategory(bmi)
    }

    private fun getBmiCategory(bmi: Double): String {
        return when {
            bmi < 18.5 -> "Underweight"
            bmi < 25 -> "Normal"
            bmi < 30 -> "Overweight"
            else -> "Obese"
        }
    }

    fun saveBmiToFirestore(profileId: String) {

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val data = hashMapOf(
            "height" to heightState.toDoubleOrNull(),
            "weight" to weightState.toDoubleOrNull(),
            "bmi" to bmi,
            "category" to category,
            "timestamp" to System.currentTimeMillis()
        )

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .collection("profiles")
            .document(profileId)
            .collection("bmiHistory")
            .add(data)
    }

    fun loadHistory(profileId: String) {

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .collection("profiles")
            .document(profileId)
            .collection("bmiHistory")
            .orderBy("timestamp")
            .get()
            .addOnSuccessListener { snapshot ->
                history = snapshot.documents.mapNotNull {
                    it.getDouble("weight")
                }
            }
    }

}



