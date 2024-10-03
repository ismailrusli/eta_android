package com.example.ble_bluetoothlowenergyesp32.presentation.components

import SoundController
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
import kotlin.math.*

@Composable
fun SpatialAudioScreen(
    soundManager: SoundController, // Pass in the SoundManager instance
    onPlay: () -> Unit,
    onStop: () -> Unit,
    onPositionChanged: (Float, Float, Float) -> Unit,
    configViewModel: ConfigViewModel
) {
    var zPosition by remember { mutableStateOf(0f) }
    var xPosition by remember { mutableStateOf(0f) }
    var yPosition by remember { mutableStateOf(0f) }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Text(text = "Kontrol Posisi Audio 3D dengan Koordinat XY dan Z")

        Spacer(modifier = Modifier.height(16.dp))

        /*
        CircleSlider(
            onPositionChanged = { x, y ->
                // Update the buzzing sound's position
                soundManager.playBuzzingSound(x, y, zPosition, 75)
                onPositionChanged(x, y, zPosition)
            },
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
        ) */

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Posisi X: ${xPosition.toString()}")
        Slider(
            value = xPosition,
            onValueChange = {
                xPosition = it
                soundManager.playBuzzingSound(xPosition, yPosition, zPosition, 500) // Update with Z position as well
                onPositionChanged(xPosition, yPosition, zPosition)
            },
            valueRange = -500f..500f,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Posisi Y: ${yPosition.toString()}")
        Slider(
            value = yPosition,
            onValueChange = {
                yPosition = it
                soundManager.playBuzzingSound(xPosition, yPosition, zPosition, 500) // Update with Z position as well
                onPositionChanged(xPosition, yPosition, zPosition)
            },
            valueRange = 0f..500f,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Posisi Z: ${zPosition.toString()}")
        Slider(
            value = zPosition,
            onValueChange = {
                zPosition = it
                soundManager.playBuzzingSound(xPosition, yPosition, zPosition, 500) // Update with Z position as well
                onPositionChanged(xPosition, yPosition, zPosition)
            },
            valueRange = -500f..500f,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Button(onClick = { onPlay() }) {
                Text(text = "Play")
            }
            Button(onClick = { onStop() }) {
                Text(text = "Stop")
            }
        }
    }
}



@Composable
fun CircleSlider(
    onPositionChanged: (Float, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    // State untuk posisi handle (di lingkaran)
    var handlePosition by remember { mutableStateOf(Offset.Zero) }
    val radius = 150f  // Radius lingkaran

    // Koordinat Cartesian dinormalisasi ke rentang -1..1
    val normalizedX = handlePosition.x / radius
    val normalizedY = handlePosition.y / radius

    // Kirim posisi yang telah dinormalisasi melalui callback
    LaunchedEffect(handlePosition) {
        onPositionChanged(normalizedX, normalizedY)
    }

    // Menggambar lingkaran dan handle yang bisa ditarik
    Canvas(modifier = modifier
        .size(300.dp)
        .pointerInput(Unit) {
            detectDragGestures { change, dragAmount ->
                val newOffset = handlePosition + dragAmount
                val distanceFromCenter = newOffset.getDistance()

                // Batasi handle agar tetap di dalam lingkaran
                if (distanceFromCenter <= radius) {
                    handlePosition = newOffset
                } else {
                    // Posisi handle tetap di perimeter lingkaran
                    val scaleFactor = radius / distanceFromCenter
                    handlePosition = newOffset * scaleFactor
                }

                change.consume()
            }
        }
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
                y = center.y + handlePosition.y
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