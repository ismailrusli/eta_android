package com.example.ble_audiospasialdariesp32sonaraudio.domain.repo

import android.content.Context
import android.util.Log
import androidx.compose.runtime.*
import com.example.ble_audiospasialdariesp32sonaraudio.domain.model.IMUTerolahDataPosisi
import com.example.ble_audiospasialdariesp32sonaraudio.domain.model.IMUTerolahDataSudut
import kotlin.math.PI
import kotlin.math.sqrt


class DataHandler {
    var previousTimestamp: Long? = null
    var initialAccelData: DoubleArray? = null
    var initialGyroData: DoubleArray? = null
    var initialYaw: Double? = null
    var isOriginSet = false

    // Reset the origin (accelData, gyroData, and yaw) when starting a new session
    fun resetOrigin() {
        initialAccelData = null
        initialGyroData = null
        initialYaw = null
        isOriginSet = false // The origin will be set again with the first data point of the new session
    }

    fun RollPitchYaw(
        context: Context,
        gyroData: DoubleArray?,
        accelData: DoubleArray?
    ): IMUTerolahDataSudut {
        val deltaTime = 0.2 // Fixed deltaTime (200ms between each data)
        val alpha = 0.98  // Tuning parameter for the filter

        var roll = 0.0
        var pitch = 0.0
        var yaw = 0.0

        // Initialize origin when first data arrives (or when origin is reset)
        if (!isOriginSet) {
            if (accelData != null && gyroData != null) {
                initialAccelData = accelData.clone()
                initialGyroData = gyroData.clone()

                // Calculate and store the initial yaw
                initialYaw = Math.atan2(
                    accelData[1],
                    sqrt(accelData[0] * accelData[0] + accelData[2] * accelData[2])
                )
                isOriginSet = true
            }
        } else {
            // Compute the difference from the origin
            if (accelData != null && initialAccelData != null) {
                val ax = accelData[0] - initialAccelData!![0]
                val ay = accelData[1] - initialAccelData!![1]
                val az = accelData[2] - initialAccelData!![2]

                val accelRoll = Math.atan2(ay, az)
                val accelPitch = Math.atan2(-ax, sqrt(ay * ay + az * az))

                roll = alpha * (roll + gyroData?.get(0)!! * deltaTime) + (1 - alpha) * accelRoll
                pitch = alpha * (pitch + gyroData?.get(1)!! * deltaTime) + (1 - alpha) * accelPitch
            }

            if (gyroData != null) {
                val gyroZ = gyroData[2]
                yaw += gyroZ * deltaTime
            }
        }

        // Adjust yaw relative to the initial yaw
        if (initialYaw != null) {
            yaw -= initialYaw!!
        }

        val rollInDegrees = Math.toDegrees(roll)
        val pitchInDegrees = Math.toDegrees(pitch)
        val yawInDegrees = Math.toDegrees(yaw)

        return IMUTerolahDataSudut(rollInDegrees.toInt(), pitchInDegrees.toInt(), yawInDegrees.toInt())
    }

    fun PosisiXYZ(
        context: Context,
        timestamp: Long?, // Single timestamp value
        accelData: DoubleArray? // Accelerometer data: [ax, ay, az]
    ): IMUTerolahDataPosisi {
        var initialPosition = FloatArray(3) { 0f }
        var initialVelocity = DoubleArray(3) { 0.0 }

        val velocity = DoubleArray(3) { 0.0 }
        val position = FloatArray(3) { 0f }

        val deltaTime = 0.2f

        if (accelData != null) {
            for (i in accelData.indices) {
                velocity[i] = initialVelocity[i] + (accelData?.get(i) ?: 0.0) * deltaTime
                position[i] = (initialPosition[i] + velocity[i] * deltaTime).toFloat()

                initialVelocity = velocity
                initialPosition = position
            }
        }

        return IMUTerolahDataPosisi(position[0], position[1], position[2])
    }
}


