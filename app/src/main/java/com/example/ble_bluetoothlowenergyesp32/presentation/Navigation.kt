package com.example.ble_bluetoothlowenergyesp32.presentation


import SoundControllerViewModel
import android.bluetooth.BluetoothAdapter
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ble_bluetoothlowenergyesp32.presentation.components.DisplayTextAudioAndVibrationScreen
import com.example.ble_bluetoothlowenergyesp32.presentation.components.SpatialAudioScreen
import com.example.ble_bluetoothlowenergyesp32.presentation.components.StartSplashScreen
import com.example.ble_bluetoothlowenergyesp32.presentation.components.TestCaseScreen


@Composable
fun Navigation(
    onBluetoothStateChanged:()->Unit,
    viewModelBLE: BluetoothLEViewModel,
    bluetoothAdapter: BluetoothAdapter,
    textToSpeechViewModel: TextToSpeechViewModel,
    onPlay:()->Unit,
    onStop:()->Unit,
    onPositionChanged: (Float, Float, Float) -> Unit,
    soundManager:SoundControllerViewModel,
    configViewModel: ConfigViewModel
){
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.StartSplashScreen.route){
        composable(Screen.StartSplashScreen.route){
            StartSplashScreen(
                navController,
                onBluetoothStateChanged,
                bluetoothAdapter
            )
        }

        composable(Screen.ScanAndPairedDeviceScreen.route){

        }

        composable(Screen.DisplayTextAudioAndVibrationScreen.route){
            DisplayTextAudioAndVibrationScreen(
                onBluetoothStateChanged = onBluetoothStateChanged,
                viewModelBLE,
                textToSpeechViewModel
            )
        }

        composable(Screen.TestCaseScreen.route){
            TestCaseScreen(
                textToSpeechViewModel, navController
            )
        }

        composable(Screen.SpatialAudioScreen.route){
            SpatialAudioScreen(
                soundManager,
                onPlay,
                onStop,
                onPositionChanged,
                configViewModel
            )
        }

    }

}

sealed class Screen (val route:String){
    object StartSplashScreen:Screen("start_splash_screen")
    object ScanAndPairedDeviceScreen:Screen ("scan_and_paired_device_screen")
    object DisplayTextAudioAndVibrationScreen:Screen("display_text_audio_and_vibration_screen")
    object TestCaseScreen:Screen("test_case_screen")
    object SpatialAudioScreen:Screen("spatial_audio_screen")
    object ConfigScreen:Screen("config_screen")
}