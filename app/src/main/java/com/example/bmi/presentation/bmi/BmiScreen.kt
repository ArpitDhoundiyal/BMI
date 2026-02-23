package com.example.bmi.presentation.bmi

import android.net.Uri
import com.example.bmi.BmiViewModel

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bmi.BmiViewModel.Mode
import androidx.core.graphics.toColorInt
import androidx.navigation.NavController


@Composable
fun BmiScreen(
    profileId: String,
    name: String,
    navController: NavController,
    vm: BmiViewModel = viewModel()

) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "BMI Calculator",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        // ---------------- MODE SELECTOR ----------------

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {

            RadioButton(
                selected = vm.selectedMode == Mode.Metric,
                onClick = { vm.updateMode(Mode.Metric) }
            )
            Text("Metric")

            Spacer(modifier = Modifier.width(20.dp))

            RadioButton(
                selected = vm.selectedMode == Mode.Imperial,
                onClick = { vm.updateMode(Mode.Imperial) }
            )
            Text("Imperial")
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ---------------- HEIGHT FIELD ----------------

        OutlinedTextField(
            value = vm.heightState.value,
            onValueChange = { vm.updateHeight(it) },
            label = { Text("Height (${vm.heightState.prefix})") },
            isError = vm.heightState.error != null,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal
            ),
            singleLine = true,
            trailingIcon = {
                if (vm.heightState.value.isNotEmpty()) {
                    IconButton(onClick = { vm.updateHeight("") }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear text"
                        )
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        )


        Spacer(modifier = Modifier.height(16.dp))

        // ---------------- WEIGHT FIELD ----------------

        OutlinedTextField(
            value = vm.weightState.value,
            onValueChange = { vm.updateWeight(it) },
            label = { Text("Weight (${vm.weightState.prefix})") },
            isError = vm.weightState.error != null,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal
            ),
            singleLine = true,
            trailingIcon = {
                if (vm.heightState.value.isNotEmpty()) {
                    IconButton(onClick = { vm.updateHeight("") }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear text"
                        )
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        )


        Spacer(modifier = Modifier.height(20.dp))

        // ---------------- BUTTON ----------------

        Button(
            onClick = {
                vm.calculate()
                vm.saveBmiToFirestore(profileId)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Calculate BMI")
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ---------------- RESULT ----------------

        if (vm.bmi != 0.0) {
            Text(
                text = "BMI: %.2f".format(vm.bmi),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            )
            {

                Text(
                    text = "You are ${vm.message}",
                    fontWeight = FontWeight.Medium
                )

                TextButton(
                    onClick = {
                        navController.navigate(
                            "tips/${Uri.encode(name)}/${vm.weightState.value.toString()}/${vm.bmi}"
                        )

                    }
                ) {
                    Text("See Tips ➜")
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))


        // ---------------- HISTORY ----------------

        LaunchedEffect(Unit) {
            vm.loadHistory(profileId)
        }

        Text("Weight History", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(10.dp))

        AndroidView(
            factory = { context ->
                com.github.mikephil.charting.charts.BarChart(context)
            },
            update = { chart ->
                chart.axisLeft.setDrawGridLines(false)
                chart.axisRight.setDrawGridLines(false)
                chart.xAxis.setDrawGridLines(false)



                chart.setDrawGridBackground(false)


                val entries = vm.history.mapIndexed { index, value ->
                    com.github.mikephil.charting.data.BarEntry(
                        (index + 1).toFloat(),
                        value.toFloat()
                    )
                }

                val dataSet = com.github.mikephil.charting.data.BarDataSet(
                    entries,
                    "Weight (kg)"
                ).apply {
                    color = "#4CAF50".toColorInt()
                    valueTextSize = 12f
                }

                val barData = com.github.mikephil.charting.data.BarData(dataSet)

                chart.data = barData
                chart.description.isEnabled = false
                chart.axisRight.isEnabled = false
                chart.xAxis.granularity = 1f
                chart.animateY(800)
                chart.invalidate()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
        )
    }
}







