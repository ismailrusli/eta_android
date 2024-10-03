package com.example.ble_bluetoothlowenergyesp32.presentation.components

import android.bluetooth.BluetoothAdapter
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ble_bluetoothlowenergyesp32.R
import com.example.ble_bluetoothlowenergyesp32.permissions.PermissionUtils
import com.example.ble_bluetoothlowenergyesp32.presentation.Screen
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.delay

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun StartSplashScreen(
    navController: NavController,
    onBluetoothStateChanged:()->Unit,
    bluetoothAdapter: BluetoothAdapter
) {

    Log.d("DisplayTextAudioAndVibrationScreen","Ini halaman start")


    //Nih Screen buat permission ama Bluetooth doang nanti dijadiin Splash Screen aja,
    //Kalau animasi dah habis baru lu perm check ama pastiin bluetooth nyala (looping)


    //WOI INGAT BIKIN SPLASH SCREEN (Udah Ges)


    val permissionState = rememberMultiplePermissionsState(permissions = PermissionUtils.permissions)

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column (
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.Blue, CircleShape)
                    .clickable {
                        if (permissionState.allPermissionsGranted){
                            if (bluetoothAdapter.isEnabled){
                                navController.navigate(Screen.DisplayTextAudioAndVibrationScreen.route) {
                                    popUpTo(Screen.StartSplashScreen.route) {
                                        inclusive = true
                                    }
                                }
                            }else{
                                onBluetoothStateChanged()
                            }
                        }else{
                            permissionState.launchMultiplePermissionRequest()
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Mulai",
                    fontSize = 35.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            Row{
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.Blue, CircleShape)
                        .clickable {
                            if (permissionState.allPermissionsGranted){
                                if (bluetoothAdapter.isEnabled){
                                    navController.navigate(Screen.TestCaseScreen.route) {
                                        popUpTo(Screen.StartSplashScreen.route) {
                                            inclusive = true
                                        }
                                    }
                                }else{
                                    onBluetoothStateChanged()
                                }
                            }else{
                                permissionState.launchMultiplePermissionRequest()
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Vibration and TTS Test",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }


                Spacer(modifier = Modifier.height(30.dp))

                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.Blue, CircleShape)
                        .clickable {
                            if (permissionState.allPermissionsGranted){
                                if (bluetoothAdapter.isEnabled){
                                    navController.navigate(Screen.SpatialAudioScreen.route) {
                                        popUpTo(Screen.StartSplashScreen.route) {
                                            inclusive = true
                                        }
                                    }
                                }else{
                                    onBluetoothStateChanged()
                                }
                            }else{
                                permissionState.launchMultiplePermissionRequest()
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Audio Spatial Screen",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

        }






    }
}
