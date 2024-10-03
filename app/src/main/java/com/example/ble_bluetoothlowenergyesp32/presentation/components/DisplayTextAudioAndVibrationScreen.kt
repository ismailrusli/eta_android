package com.example.ble_bluetoothlowenergyesp32.presentation.components

import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.ble_bluetoothlowenergyesp32.data.ConnectionState
import com.example.ble_bluetoothlowenergyesp32.permissions.PermissionUtils
import com.example.ble_bluetoothlowenergyesp32.permissions.SystemBroadcastReceiver
import com.example.ble_bluetoothlowenergyesp32.presentation.BluetoothLEViewModel
import com.example.ble_bluetoothlowenergyesp32.presentation.TextToSpeechViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun DisplayTextAudioAndVibrationScreen(
    onBluetoothStateChanged: () -> Unit,
    viewModel: BluetoothLEViewModel = hiltViewModel(),
    textToSpeechViewModel: TextToSpeechViewModel
) {
    Log.d("DisplayTextAudioAndVibrationScreen", "Initializing Display Text Audio And Vibration Screen")

    SystemBroadcastReceiver(systemAction = BluetoothAdapter.ACTION_STATE_CHANGED) { bluetoothState ->
        val action = bluetoothState?.action ?: return@SystemBroadcastReceiver
        Log.d("DisplayTextAudioAndVibrationScreen", "Bluetooth State Changed: $action")
        if (action == BluetoothAdapter.ACTION_STATE_CHANGED) {
            onBluetoothStateChanged()
        }
    }

    // Create a mutable state to keep track of spoken messages
    var spokenMessages by remember { mutableStateOf(mutableSetOf<String>()) }
    var messages by remember { mutableStateOf(String) }

    val context = LocalContext.current

    val permissionState = rememberMultiplePermissionsState(permissions = PermissionUtils.permissions)
    val lifecycleOwner = LocalLifecycleOwner.current
    val bleConnectionState = viewModel.connectionState

    Log.d("DisplayTextAudioAndVibrationScreen", "Current Bluetooth Connection State: $bleConnectionState")

    val ttsState = textToSpeechViewModel.state.value

    DisposableEffect(
        key1 = lifecycleOwner,
        effect = {
            val observer = LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_START -> {
                        Log.d("DisplayTextAudioAndVibrationScreen", "Lifecycle Event: ON_START")
                        permissionState.launchMultiplePermissionRequest()
                        Log.d("BLE Permissions", "Permissions granted: ${permissionState.allPermissionsGranted}")
                        if (permissionState.allPermissionsGranted && bleConnectionState == ConnectionState.Disconnected) {
                            Log.d("DisplayTextAudioAndVibrationScreen", "Attempting to reconnect...")
                            viewModel.reconnect()
                        }
                    }
                    Lifecycle.Event.ON_STOP -> {
                        Log.d("DisplayTextAudioAndVibrationScreen", "Lifecycle Event: ON_STOP")
                        if (bleConnectionState == ConnectionState.Connected) {
                            Log.d("DisplayTextAudioAndVibrationScreen", "Disconnecting from BLE device...")
                            viewModel.disconnect()
                        }
                    }
                    else -> Unit
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)

            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
                Log.d("DisplayTextAudioAndVibrationScreen", "Observer disposed.")
            }
        }
    )






    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .aspectRatio(1f)
                .border(
                    BorderStroke(5.dp, Color.Blue),
                    RoundedCornerShape(10.dp)
                ),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (bleConnectionState) {
                ConnectionState.Uninitialized -> {

                    viewModel.initializeConnection()
                    Log.d("DisplayTextAudioAndVibrationScreen", "Koneksi belum dimulai")
                    CircularProgressIndicator()
                    // Tidak perlu tombol
                    var message:String = viewModel.initialzingMessage.toString()

                    Text(text = message)

                    if (!spokenMessages.contains(message)) {
                        TTSOk(context, message, textToSpeechViewModel)
                        spokenMessages.add(message)
                    }

                }

                ConnectionState.CurrentlyInitializing -> {
                    Log.d("DisplayTextAudioAndVibrationScreen", "Koneksi sedang diinisialisasi.")
                    CircularProgressIndicator()
                    var message:String = viewModel.initialzingMessage.toString()

                    Text(text = message)

                    if (!spokenMessages.contains(message)) {
                        TTSOk(context, message, textToSpeechViewModel)
                        spokenMessages.add(message)
                    }
                }

                ConnectionState.Disconnected -> {
                    viewModel.initializeConnection()
                    Log.d("DisplayTextAudioAndVibrationScreen", "Perangkat BLE terputus.")
                    CircularProgressIndicator()
                    // Tidak perlu tombol
                    var message:String = viewModel.initialzingMessage.toString()

                    Text(text = message)

                    if (!spokenMessages.contains(message)) {
                        TTSOk(context, message, textToSpeechViewModel)
                        spokenMessages.add(message)
                    }

                }
                ConnectionState.Connected -> {
                    Log.d("DisplayTextAudioAndVibrationScreen", "Perangkat BLE terhubung. Jarak: ${viewModel.jarak}")
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Jarak: ${viewModel.jarak}",
                            style = MaterialTheme.typography.titleMedium
                        )
                        VibratePhone(jarak = viewModel.jarak, context)
                    }
                }
                else -> {
                    Log.d("DisplayTextAudioAndVibrationScreen", "Status koneksi tidak diketahui: $bleConnectionState")
                }
            }

            if(!permissionState.allPermissionsGranted)  {
                Log.d("DisplayTextAudioAndVibrationScreen", "Izin tidak diberikan, menampilkan pesan.")
                CircularProgressIndicator()
                Text(
                    text = "Pergi ke Pengaturan Smartphone untuk memberikan izin",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(10.dp),
                    textAlign = TextAlign.Center
                )
            }else if(viewModel.errorMessage != null)  {
                Log.d("DisplayTextAudioAndVibrationScreen", "Terjadi kesalahan: ${viewModel.errorMessage}")
                CircularProgressIndicator()
                Text(text = viewModel.errorMessage!!)
            }
        }
    }

}

fun VibratePhone(
    jarak: Float,
    context: Context
) {
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    var jarakMeter: Float = jarak / 100f

    val durasiGetaran: Long = 100L
    val amplitudoGetaran: Int = when {
        jarakMeter <= 0.5f -> 255
        jarakMeter >= 5 -> 100
        else -> ((-34.44f * jarakMeter) + 272.22f).toInt()
    }

    Log.d("DisplayTextAudioAndVibrationScreen", "Vibrating for $durasiGetaran milliseconds with amplitude $amplitudoGetaran.")

    // Vibrate for Android 8 (Oreo) and above
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val vibrationEffect = VibrationEffect.createOneShot(durasiGetaran, amplitudoGetaran)
        vibrator.vibrate(vibrationEffect)
    } else {
        // Vibrate for older versions of Android
        vibrator.vibrate(durasiGetaran)
    }
}

fun TTSOk(
    context: Context,
    stringTTS: String,
    textToSpeechViewModel: TextToSpeechViewModel
) {
    Log.d("DisplayTextAudioAndVibrationScreen", "Preparing to speak: $stringTTS")
    textToSpeechViewModel.onTextFieldValueChange(stringTTS)
    textToSpeechViewModel.textToSpeech(context)
    Log.d("DisplayTextAudioAndVibrationScreen", "Speaking: $stringTTS")
}


