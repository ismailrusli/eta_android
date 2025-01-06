package com.example.ble_audiospasialdariesp32sonaraudio.presentation.screen

import android.bluetooth.BluetoothAdapter
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.example.ble_audiospasialdariesp32sonaraudio.datahandler.audiospasial.AudioSpasialManager
import com.example.ble_audiospasialdariesp32sonaraudio.domain.repo.ConnectionState
import com.example.ble_audiospasialdariesp32sonaraudio.domain.repo.DataHandler
import com.example.ble_audiospasialdariesp32sonaraudio.permissions.SystemBroadcastReceiver
import com.example.ble_audiospasialdariesp32sonaraudio.presentation.BluetoothLEViewModel
import com.example.ble_audiospasialdariesp32sonaraudio.presentation.ConfigViewModel
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
    onBluetoothStateChanged: () -> Unit
) {
    val context = LocalContext.current

    // Receiver for Bluetooth state changes
    SystemBroadcastReceiver(systemAction = BluetoothAdapter.ACTION_STATE_CHANGED) { bluetoothState ->
        val action = bluetoothState?.action ?: return@SystemBroadcastReceiver
        Log.d("DisplayTextScreen", "Bluetooth State Changed: $action")
        if (action == BluetoothAdapter.ACTION_STATE_CHANGED) {
            onBluetoothStateChanged()
        }
    }

    val jarak by bluetoothLEViewModel.jarak.collectAsState()
    val timestamp by bluetoothLEViewModel.timestamp.collectAsState()
    val kecepatanAngular by bluetoothLEViewModel.kecepatanAngular.collectAsState()
    val kecepatanAkselerasi by bluetoothLEViewModel.kecepatanAkselerasi.collectAsState()
    val connectionState by bluetoothLEViewModel.connectionState.collectAsState()

    val listDataSudut = DataHandler().RollPitchYaw(context, timestamp, kecepatanAngular, kecepatanAkselerasi)
    val listDataAkselerasi = DataHandler().PosisiXYZ(context, timestamp, kecepatanAkselerasi)

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            when (connectionState) {
                ConnectionState.Connected -> {
                    var pitch by remember { mutableStateOf(listDataSudut.pitch) }
                    var yaw by remember { mutableStateOf(listDataSudut.yaw) }

                    // Cek apakah jarak null atau tidak valid
                    if (jarak == null) {
                        // Jika jarak tidak valid (null atau 0), reset grid
                        gridViewModel.resetGrid()
                        Log.d("PosisiSampelDataScreen", "Data tidak valid, grid di-reset.")
                    } else {
                        // Jika jarak valid, update grid
                        gridViewModel.updateGrid(
                            configViewModel.maxJarak,
                            imuDataPosisi = listDataAkselerasi,
                            imuDataSudut = listDataSudut,
                            sensorDistance = jarak!!
                        )

                        GridUI(gridViewModel)

                        Spacer(modifier = Modifier.size(20.dp))

                        // Create the text to display and speak
                        val text = "Jarak Objek : $jarak centimeter Berada di sebelah ${if (yaw > 0) "Kanan" else "Kiri"} ${if (pitch > 0) "Atas" else "Bawah"}"

                        // Update the text for TTS
                        ttsViewModel.updateText(text)

                        Text(text = text)

                        // Button for Text-to-Speech (TTS)
                        Button(
                            onClick = {
                                ttsViewModel.toggleTTS()
                            },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text(text = if (ttsViewModel.data.isTTSEnabled) "Stop Baca Jarak" else "Baca Jarak")
                        }

                        // Simulasi audio spasial berdasarkan jarak dan yaw
                        jarak?.let {
                            audioSpasialManager.simulate3DAudioUsingITD(
                                it,
                                50,
                                yaw,
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
                    // Jika koneksi terputus, reset grid
                    gridViewModel.resetGrid()
                    Text(text = "Koneksi terputus")
                }
                else -> {
                    Text(text = "Menghubungkan...")
                }
            }
        }
    }

    // Close BLE connection when this screen is removed
    DisposableEffect(Unit) {
        onDispose {
            bluetoothLEViewModel.closeConnection()
        }
    }
}



