package com.example.ble_bluetoothlowenergyesp32.presentation.components

import SoundControllerViewModel
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.ble_bluetoothlowenergyesp32.presentation.ConfigViewModel
import kotlinx.coroutines.delay
import java.lang.reflect.Array
import kotlin.math.*
import kotlin.random.Random

@Composable
fun SpatialAudioScreen(
    soundManager: SoundControllerViewModel, // Pass in the SoundManager instance
    onPlay: () -> Unit,
    onStop: () -> Unit,
    onPositionChanged: (Float, Float, Float) -> Unit,
    configViewModel: ConfigViewModel
) {
    var zPosition by remember { mutableStateOf(0f) }
    var xPosition by remember { mutableStateOf(0f) }
    var yPosition by remember { mutableStateOf(0f) }

    var index by remember { mutableStateOf(0) }
    var isPlaying by remember { mutableStateOf(false) }  // Flag untuk kontrol loop

    var arrayJarakR = remember { FloatArray(20) { abs(Random.nextFloat() * 1000 - 500) } }
    var thetaTest = remember {
        FloatArray(20) { index -> (index + 1) * (Math.PI.toFloat() / 20f) }
    }

    var jarakRealTime by remember { mutableStateOf(0f) }
    jarakRealTime = sqrt(xPosition * xPosition + yPosition * yPosition) / 100f

    // Loop audio jika isPlaying true
    LaunchedEffect(isPlaying) {
        while (isPlaying && index < 20) {
            xPosition = arrayJarakR[index] * cos(thetaTest[index])
            yPosition = abs(arrayJarakR[index] * sin(thetaTest[index]))

            // Jalankan efek untuk memainkan suara dalam interval 1 detik
            soundManager.playBuzzingSound(xPosition, yPosition, zPosition, 510)

            Log.d("SpatialAudioScreen", "index : $index" + " thetaTest: ${thetaTest[index]}")
            delay(1000)  // Tunggu 1 detik sebelum memainkan suara berikutnya
            index++  // Increment index
        }
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Text(text = "Kontrol Posisi Audio 3D dengan Koordinat XY dan Z")
        Spacer(modifier = Modifier.height(16.dp))

        CircleSlider(
            xPosition,
            yPosition,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Jarak: $jarakRealTime Meter")

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Button(onClick = {
                // Memulai loop dengan setting isPlaying ke true
                isPlaying = true
            }) {
                Text(text = "Play")
            }

            Button(onClick = {
                // Memulai loop dengan setting isPlaying ke true
                isPlaying = true
                index = 0
            }) {
                Text(text = "Reset")
            }

            Button(onClick = {
                // Menghentikan loop dengan mengubah isPlaying ke false
                isPlaying = false
                soundManager.stopBuzzingSound()
            }) {
                Text(text = "Stop")
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            soundManager.stopBuzzingSound()
            soundManager.release()
        }
    }
}




@Composable
fun CircleSlider(
    xPos:Float,
    yPos:Float,
    modifier: Modifier = Modifier
) {
    // State untuk posisi handle (di lingkaran)
    var handlePosition by remember { mutableStateOf(Offset.Zero) }
    val radius = 500f  // Radius lingkaran

    // Koordinat Cartesian dinormalisasi ke rentang -1..1
    val normalizedX = handlePosition.x / radius
    val normalizedY = handlePosition.y / radius

    // Listen for changes to the handle position from outside
    LaunchedEffect(xPos, yPos) {
        handlePosition = Offset(xPos, yPos)
    }


    // Menggambar lingkaran dan handle yang bisa ditarik
    Canvas(modifier = modifier
        .size(300.dp)

    ) {
        // Gambar lingkaran utama
        drawCircle(
            color = Color.Gray,
            radius = radius,
            style = Stroke(width = 4.dp.toPx())
        )

        // Gambar handle yang bisa ditarik di dalam lingkaran
        drawCircle(
            color = Color.Red,
            radius = 15.dp.toPx(),
            center = Offset(
                x = center.x + handlePosition.x,
                y = center.y - handlePosition.y
            )
        )
    }
}

// Fungsi tambahan untuk operasi matematika dengan Offset
private operator fun Offset.plus(other: Offset): Offset {
    return Offset(this.x + other.x, this.y + other.y)
}

private operator fun Offset.times(scale: Float): Offset {
    return Offset(this.x * scale, this.y * scale)
}

private fun Offset.getDistance(): Float {
    return sqrt(x * x + y * y)
}

