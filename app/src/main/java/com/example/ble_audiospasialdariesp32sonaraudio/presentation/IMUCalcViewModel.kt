package com.example.ble_audiospasialdariesp32sonaraudio.presentation

import androidx.lifecycle.ViewModel
import android.content.Context
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.ble_audiospasialdariesp32sonaraudio.domain.model.IMUTerolahDataPosisi
import com.example.ble_audiospasialdariesp32sonaraudio.domain.model.IMUTerolahDataSudut
import com.example.ble_audiospasialdariesp32sonaraudio.domain.repo.DataHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class IMUCalcViewModel() : ViewModel() {

    private val dataHandler = DataHandler()

    // State flows for exposing calculated data
    private val _imuSudut = MutableStateFlow(IMUTerolahDataSudut(0, 0, 0))
    val imuSudut: StateFlow<IMUTerolahDataSudut> = _imuSudut

    private val _imuPosisi = MutableStateFlow(IMUTerolahDataPosisi(0f, 0f, 0f))
    val imuPosisi: StateFlow<IMUTerolahDataPosisi> = _imuPosisi

    // Track the current session count
    private var currentSessionCount: Int = -1

    fun updateIMUData(
        context: Context,
        sessionCount: Int,  // Session count passed as a parameter
        timestamp: Long?,
        gyroData: DoubleArray?,
        accelData: DoubleArray?
    ) {
        viewModelScope.launch {
            // Check if the session count has changed
            if (sessionCount != currentSessionCount) {
                // Update the current session count
                currentSessionCount = sessionCount

                // Reset the origin in the DataHandler
                dataHandler.resetOrigin()
                Log.d("imuvm", "Session changed to $sessionCount. Origin reset.")
            }

            // Update Roll, Pitch, Yaw
            val imuSudutData = dataHandler.RollPitchYaw(context, gyroData, accelData)
            _imuSudut.value = imuSudutData

            Log.d("imuvm", "Calculated RollPitchYaw VM : Roll=${imuSudutData.roll}, Pitch=${imuSudutData.pitch}, Yaw=${imuSudutData.yaw}")

            // Update Position
            val imuPosisiData = dataHandler.PosisiXYZ(context, timestamp, accelData)
            _imuPosisi.value = imuPosisiData
        }
    }
}
