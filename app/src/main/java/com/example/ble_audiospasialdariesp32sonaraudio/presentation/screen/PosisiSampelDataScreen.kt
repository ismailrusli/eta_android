package com.example.ble_audiospasialdariesp32sonaraudio.presentation.screen

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ble_audiospasialdariesp32sonaraudio.datahandler.audiospasial.AudioSpasialManager
import com.example.ble_audiospasialdariesp32sonaraudio.domain.repo.ConnectionState
import com.example.ble_audiospasialdariesp32sonaraudio.presentation.BluetoothLEViewModel
import com.example.ble_audiospasialdariesp32sonaraudio.presentation.ConfigViewModel
import com.example.ble_audiospasialdariesp32sonaraudio.presentation.IMUCalcViewModel
import com.example.ble_audiospasialdariesp32sonaraudio.presentation.OccupiedGridViewModel
import com.example.ble_audiospasialdariesp32sonaraudio.presentation.TextToSpeechViewModel
import com.example.ble_audiospasialdariesp32sonaraudio.presentation.components.GridUI

@Composable
fun PosisiSampelDataScreen(
    bluetoothLEViewModel: BluetoothLEViewModel = hiltViewModel(),
    audioSpasialManager: AudioSpasialManager,
    gridViewModel: OccupiedGridViewModel,
    ttsViewModel: TextToSpeechViewModel,
    configViewModel: ConfigViewModel,
    imuCalcViewModel: IMUCalcViewModel,
    onBluetoothStateChanged: () -> Unit
) {
    val context = LocalContext.current

    var previousTimestamp by remember { mutableStateOf<Long?>(null) }

    // Calculate deltaTime
    val timestamp = System.currentTimeMillis()
    val deltaTime = if (previousTimestamp != null) {
        timestamp - previousTimestamp!!
    } else {
        0L
    }
    previousTimestamp = timestamp

    // Get states from ViewModel
    val jarak by bluetoothLEViewModel.jarak.collectAsState()
    val kecepatanAngular by bluetoothLEViewModel.kecepatanAngular.collectAsState()
    val kecepatanAkselerasi by bluetoothLEViewModel.kecepatanAkselerasi.collectAsState()
    val connectionState by bluetoothLEViewModel.connectionState.collectAsState()

    // Pass deltaTime to DataHandler functions

    val imuSudut by imuCalcViewModel.imuSudut.collectAsState()
    val imuPosisi by imuCalcViewModel.imuPosisi.collectAsState()

    Log.d("YawPitch", "Yaw: ${imuSudut.yaw}, Pitch: ${imuSudut.pitch}")

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            when (connectionState) {
                ConnectionState.Connected -> {
                    if (jarak == null) {
                        gridViewModel.resetGrid()
                    } else {
                        imuCalcViewModel.updateIMUData(
                            context = context,
                            timestamp = System.currentTimeMillis(),
                            gyroData = kecepatanAngular,
                            accelData = kecepatanAkselerasi
                        )

                        gridViewModel.updateGrid(
                            configViewModel.maxJarak,
                            imuDataPosisi = imuPosisi,
                            imuDataSudut = imuSudut,
                            sensorDistance = jarak!!
                        )

                        //GridUI(gridViewModel)

                        val text = "Jarak Objek: $jarak cm, Sebelah ${if ( imuSudut.yaw > 0) "Kanan" else "Kiri"} ${if (imuSudut.pitch > 0) "Atas" else "Bawah"}"

                        Text(text = "Yaw: ${imuSudut.yaw}")
                        Text(text = text)

                        ttsViewModel.updateText(text)

                        Button(
                            onClick = { ttsViewModel.toggleTTS() },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text(text = if (ttsViewModel.data.isTTSEnabled) "Stop Baca Jarak" else "Baca Jarak")
                        }

                        jarak?.let {
                            audioSpasialManager.simulate3DAudio(
                                it,
                                500f,
                                imuSudut.yaw,
                                "ping"
                            )
                        }
                    }
                }
                ConnectionState.Uninitialized -> {
                    Text(text = "Koneksi belum diinisialisasi")
                    bluetoothLEViewModel.startConnection()
                }
                ConnectionState.Disconnected -> {
                    gridViewModel.resetGrid()
                    Text(text = "Koneksi terputus")
                }
                else -> {
                    Text(text = "Menghubungkan...")
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            bluetoothLEViewModel.closeConnection()
        }
    }
}





