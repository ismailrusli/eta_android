package com.example.ble_bluetoothlowenergyesp32

import SoundController
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ble_bluetoothlowenergyesp32.presentation.BluetoothLEViewModel
import com.example.ble_bluetoothlowenergyesp32.presentation.ConfigViewModel
import com.example.ble_bluetoothlowenergyesp32.presentation.Navigation
import com.example.ble_bluetoothlowenergyesp32.presentation.TextToSpeechViewModel
import com.example.ble_bluetoothlowenergyesp32.ui.theme.BLE_BluetoothLowEnergyESP32Theme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var bluetoothAdapter: BluetoothAdapter


    init{
        System.loadLibrary("native-lib")
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BLE_BluetoothLowEnergyESP32Theme {
                val  bluetoothViewModel = hiltViewModel<BluetoothLEViewModel>()

                val ttsViewModel: TextToSpeechViewModel = viewModel()
                val configViewModel:ConfigViewModel = viewModel()

                Navigation(
                    onBluetoothStateChanged = {
                        showBluetoothDialog()
                    },
                    bluetoothViewModel,
                    bluetoothAdapter,
                    ttsViewModel,
                    // Pass audio control functions
                    onPlay = { play() },
                    onStop = { stop() },
                    onPositionChanged = { x, y, z -> setAudioPosition(x, y, z) },
                    soundManager = SoundController(this),
                    configViewModel = configViewModel
                )
            }
        }
        createAudioStream()
    }


    // JNI declarations
    external fun createAudioStream()
    external fun setAudioPosition(xPos: Float, yPos: Float, zPos: Float)
    external fun play()
    external fun stop()

    //Memaksa Nyalakan Bluetooth
    private var isBluetootDialogAlreadyShown = false
    private fun showBluetoothDialog(){
        if(!bluetoothAdapter.isEnabled){
            if(!isBluetootDialogAlreadyShown){
                val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startBluetoothIntentForResult.launch(enableBluetoothIntent)
                isBluetootDialogAlreadyShown = true
            }
        }
    }

    private val startBluetoothIntentForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            isBluetootDialogAlreadyShown = false
            if(result.resultCode != Activity.RESULT_OK){
                showBluetoothDialog()
            }
        }

}
