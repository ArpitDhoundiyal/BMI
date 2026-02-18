package com.example.bmi

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlin.math.roundToInt




class BmiViewModel : ViewModel() {

    enum class Mode {
        Imperial, Metric
    }

    var selectedMode by mutableStateOf(Mode.Metric)
        private set
    var bmi by mutableStateOf(0.0)
        private set
    var message by mutableStateOf("")
        private set
    var heightState by mutableStateOf(
        ValueState(
            "Height", "cm"
        )
    )
        private set
    var weightState by mutableStateOf(
        ValueState(
            "Weight", "kg"
        )
    )
        private set

    var history by mutableStateOf<List<Double>>(emptyList())
        private set


    fun updateHeight(it: String) {
        heightState = heightState.copy(value = it, error = null)
    }

    fun updateWeight(it: String) {
        weightState = weightState.copy(value = it, error = null)
    }

    fun calculate() {
        val height = heightState.toNumber()
        val weight = weightState.toNumber()
        if (height == null)
            heightState = heightState.copy(error = "Invalid number")
        else if (weight == null)
            weightState = weightState.copy(error = "Invalid number")
        else calculateBMI(height, weight, selectedMode == Mode.Metric)
    }

    private fun calculateBMI(height: Double, weight: Double, isMetric: Boolean = true) {

        if (height <= 0 || weight <= 0) {
            message = "Invalid input"
            return
        }

        bmi = if (isMetric) {
            val heightInMeter = height / 100.0
            weight / (heightInMeter * heightInMeter)
        } else {
            (703.0 * weight) / (height * height)
        }

        // Round to 2 decimal places
        bmi = String.format("%.1f", bmi).toDouble()
        bmi = (bmi * 10).roundToInt() / 10.0


        message = when {
            bmi < 18.5 -> "Underweight"
            bmi < 25.0 -> "Normal"
            bmi < 30.0 -> "Overweight"
            else -> "Obese"
        }
    }


    fun updateMode(mode: Mode) {
        selectedMode = mode
        when (mode) {
            Mode.Imperial -> {
                heightState = heightState.copy(prefix = "inch")
                weightState = weightState.copy(prefix = "lbs")
            }

            Mode.Metric -> {
                heightState = heightState.copy(prefix = "cm")
                weightState = weightState.copy(prefix = "kg")
            }
        }
    }

    fun saveBmiToFirestore(profileId: String) {

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val data = hashMapOf(
            "height" to heightState.toNumber(),
            "weight" to weightState.toNumber(),
            "bmi" to bmi,
            "category" to message,
            "timestamp" to System.currentTimeMillis()
        )

        val profileRef = FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .collection("profiles")
            .document(profileId)

        // Save in bmiHistory
        profileRef
            .collection("bmiHistory")
            .add(data)

        profileRef.set(
            mapOf("currentBmi" to bmi),
            SetOptions.merge()
        )

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
            .addSnapshotListener { snapshot, _ ->

                snapshot?.let {
                    history = it.documents.mapNotNull { doc ->
                        doc.getDouble("weight")
                    }
                }
            }




    }
}