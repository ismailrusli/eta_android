package com.example.ble_bluetoothlowenergyesp32.presentation.components

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ble_bluetoothlowenergyesp32.presentation.Screen
import com.example.ble_bluetoothlowenergyesp32.presentation.TextToSpeechViewModel

@Composable
fun TestCaseScreen(
    textToSpeechViewModel: TextToSpeechViewModel,
    navController:NavController
) {
    val context = LocalContext.current

    var inputDistance by remember { mutableStateOf(TextFieldValue()) }
    var displayedDistance by remember { mutableStateOf("") }
    var spokenMessages by remember { mutableStateOf(mutableListOf<String>()) } // Updated to mutableList

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .aspectRatio(1f)
                .border(BorderStroke(5.dp, Color.Blue), RoundedCornerShape(10.dp)),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                fontSize = 30.sp,
                text = "Jarak : $displayedDistance Centimeter"
            )

            // TextField untuk input
            OutlinedTextField(
                value = inputDistance,
                onValueChange = { newValue -> inputDistance = newValue },
                label = { Text("Masukkan Jarak (cm)") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val distanceFloat = inputDistance.text.toFloatOrNull()
                    if (distanceFloat != null) {
                        displayedDistance = inputDistance.text
                        VibratePhoneTest(distanceFloat, context)
                    } else {
                        Log.e("TestCaseScreen", "Input tidak valid: ${inputDistance.text}")
                    }
                }
            ) {
                Text("Vibrate")
            }

            Button(
                onClick = {
                    val distanceFloat = inputDistance.text.toFloatOrNull()
                    if (distanceFloat != null) {
                        displayedDistance = inputDistance.text
                        TTSOkTest(
                            context = context,
                            stringTTS = displayedDistance,
                            textToSpeechViewModel = textToSpeechViewModel,
                            spokenMessages = spokenMessages
                        )
                    } else {
                        Log.e("TestCaseScreen", "Input tidak valid: ${inputDistance.text}")
                    }
                }
            ) {
                Text("TTS")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    navController.navigate(Screen.SpatialAudioScreen.route)
                }
            ) {
                Text("Audio Spatial")
            }

        }
    }
}

fun VibratePhoneTest(
    distance: Float,
    context: Context
) {
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    val distanceMeter: Float = distance / 100f

    val duration: Long = 300L
    val amplitude: Int = when {
        distanceMeter <= 0.5f -> 255
        distanceMeter <= 1f -> 200
        distanceMeter <= 2f -> 150
        distanceMeter <= 3f -> 100
        distanceMeter <= 4f -> 55
        distanceMeter <= 5f -> 55
        distanceMeter >= 5 -> 55
        else -> 0
    }

    Log.d("VibratePhoneTest", "Vibrating for $duration milliseconds with amplitude $amplitude.")

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val vibrationEffect = VibrationEffect.createOneShot(duration, amplitude)
        vibrator.vibrate(vibrationEffect)
    } else {
        vibrator.vibrate(duration)
    }
}

fun TTSOkTest(
    context: Context,
    stringTTS: String,
    textToSpeechViewModel: TextToSpeechViewModel,
    spokenMessages: MutableList<String> // Pass in the mutable list
) {
    // If the new message is different from the most recent one, update the list
    if (spokenMessages.isEmpty() || spokenMessages.last() != stringTTS) {
        if (spokenMessages.size == 2) {
            spokenMessages.removeAt(0) // Keep the list size to 2
        }
        spokenMessages.add(stringTTS)
        Log.d("TTSOkTest", "Updated spokenMessages: $spokenMessages")
    } else {
        Log.d("TTSOkTest", "The new message is the same as the last one, but still calling TTS.")
    }

    // Trigger Text to Speech regardless of the message being repeated or not
    textToSpeechViewModel.onTextFieldValueChange(stringTTS)
    textToSpeechViewModel.textToSpeech(context)

    Log.d("TTSOkTest", "Speaking: $stringTTS")
}