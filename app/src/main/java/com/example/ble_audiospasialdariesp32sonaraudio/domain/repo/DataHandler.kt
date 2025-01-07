package com.example.ble_audiospasialdariesp32sonaraudio.domain.repo

import android.content.Context
import android.util.Log
import androidx.compose.runtime.*
import com.example.ble_audiospasialdariesp32sonaraudio.domain.model.IMUTerolahDataPosisi
import com.example.ble_audiospasialdariesp32sonaraudio.domain.model.IMUTerolahDataSudut
import kotlin.math.PI
import kotlin.math.sqrt

class DataHandler(
){
    var previousTimestamp: Long? = null

    @Composable
    fun DeltaTime(
        timestamp: Long?
    ):Long{

        val deltaTime = if (previousTimestamp != null) {
            if (timestamp != null) {
                return (timestamp - previousTimestamp!!)
            } else {
                return 0
            } // Konversi ms ke detik
        } else {
            previousTimestamp = timestamp
            return 0
        }
        previousTimestamp = timestamp
    }



//    @Composable
//    fun RollPitchYaw(
//        context: Context,
//        timestamp: Long?,
//        gyroData: DoubleArray?,
//        accelData: DoubleArray?
//    ): IMUTerolahDataSudut {
//        var q0 = 1.0
//        var q1 = 0.0
//        var q2 = 0.0
//        var q3 = 0.0
//
//        val beta = 0.1 // Tuning parameter for Madgwick filter
//        val deltaTime = 0.2// Convert ms to seconds
//
//        Log.d("RollPitchYaw", "DeltaTime: $deltaTime")
//        if (accelData != null && gyroData != null && deltaTime!=null) {
//            // Normalize accelerometer data
//            val ax = accelData[0]
//            val ay = accelData[1]
//            val az = accelData[2]
//            val accelNorm = sqrt(ax * ax + ay * ay + az * az)
//
//            if (accelNorm != 0.0) {
//                val normAx = ax / accelNorm
//                val normAy = ay / accelNorm
//                val normAz = az / accelNorm
//
//                // Log normalized accelerometer data
//                Log.d("RollPitchYaw", "Normalized Accel: ax=$normAx, ay=$normAy, az=$normAz")
//
//                // Gyroscope values
//                val gx = gyroData[0]
//                val gy = gyroData[1]
//                val gz = gyroData[2]
//
//                // Log gyroscope data
//                Log.d("RollPitchYaw", "Gyroscope Data: gx=$gx, gy=$gy, gz=$gz")
//
//                // Madgwick filter core algorithm
//                val f1 = 2 * (q1 * q3 - q0 * q2) - normAx
//                val f2 = 2 * (q0 * q1 + q2 * q3) - normAy
//                val f3 = 1 - 2 * (q1 * q1 + q2 * q2) - normAz
//
//                // Log error function values
//                Log.d("RollPitchYaw", "Error Functions: f1=$f1, f2=$f2, f3=$f3")
//
//                val j11 = -2 * q2
//                val j12 = 2 * q3
//                val j13 = -2 * q0
//                val j14 = 2 * q1
//                val j21 = 2 * q1
//                val j22 = 2 * q0
//                val j23 = 2 * q3
//                val j24 = 2 * q2
//                val j31 = 0.0
//                val j32 = -4 * q1
//                val j33 = -4 * q2
//                val j34 = 0.0
//
//                val grad0 = j11 * f1 + j21 * f2 + j31 * f3
//                val grad1 = j12 * f1 + j22 * f2 + j32 * f3
//                val grad2 = j13 * f1 + j23 * f2 + j33 * f3
//                val grad3 = j14 * f1 + j24 * f2 + j34 * f3
//
//                // Log gradient values
//                Log.d("RollPitchYaw", "Gradient: grad0=$grad0, grad1=$grad1, grad2=$grad2, grad3=$grad3")
//
//                val gradNorm = sqrt(grad0 * grad0 + grad1 * grad1 + grad2 * grad2 + grad3 * grad3)
//                val normGrad0 = grad0 / gradNorm
//                val normGrad1 = grad1 / gradNorm
//                val normGrad2 = grad2 / gradNorm
//                val normGrad3 = grad3 / gradNorm
//
//                // Update quaternion
//                q0 += (-beta * normGrad0 + 0.5 * (-q1 * gx - q2 * gy - q3 * gz)) * deltaTime
//                q1 += (-beta * normGrad1 + 0.5 * (q0 * gx + q2 * gz - q3 * gy)) * deltaTime
//                q2 += (-beta * normGrad2 + 0.5 * (q0 * gy - q1 * gz + q3 * gx)) * deltaTime
//                q3 += (-beta * normGrad3 + 0.5 * (q0 * gz + q1 * gy - q2 * gx)) * deltaTime
//
//                // Normalize quaternion
//                val qNorm = sqrt(q0 * q0 + q1 * q1 + q2 * q2 + q3 * q3)
//                q0 /= qNorm
//                q1 /= qNorm
//                q2 /= qNorm
//                q3 /= qNorm
//
//                // Log quaternion values
//                Log.d("RollPitchYaw", "Quaternion: q0=$q0, q1=$q1, q2=$q2, q3=$q3")
//            }
//        }
//
//        // Calculate Roll, Pitch, and Yaw
//        val roll = Math.toDegrees(Math.atan2(2 * (q0 * q1 + q2 * q3), 1 - 2 * (q1 * q1 + q2 * q2)))
//        val pitch = Math.toDegrees(Math.asin(2 * (q0 * q2 - q3 * q1)))
//        val yaw = Math.toDegrees(Math.atan2(2 * (q0 * q3 + q1 * q2), 1 - 2 * (q2 * q2 + q3 * q3)))
//
//        // Log computed Roll, Pitch, and Yaw
//        Log.d("RollPitchYaw", "Roll=$roll, Pitch=$pitch, Yaw=$yaw")
//
//        return IMUTerolahDataSudut(roll.toInt(), pitch.toInt(), yaw.toInt())
//    }



    @Composable
    fun RollPitchYaw(
        context: Context,
        timestamp: Long?,
        gyroData: DoubleArray?,
        accelData: DoubleArray?
    ): IMUTerolahDataSudut {
        if (gyroData != null) {
            // Gyroscope values
            val gx = gyroData[0] // Angular velocity around X-axis
            val gy = gyroData[1] // Angular velocity around Y-axis
            val gz = gyroData[2] // Angular velocity around Z-axis

            // Calculate Roll, Pitch, and Yaw using atan2
            val roll = Math.toDegrees(Math.atan2(gy, gz)) // Roll
            val pitch = Math.toDegrees(Math.atan2(gx, gz)) // Pitch
            val yaw = Math.toDegrees(Math.atan2(gx, gy)) // Yaw

            // Log the computed values
            Log.d("RollPitchYaw", "Roll=$roll, Pitch=$pitch, Yaw=$yaw")

            // Return the computed angles
            return IMUTerolahDataSudut(roll.toInt(), pitch.toInt(), yaw.toInt())
        }

        // Default return in case of null data
        return IMUTerolahDataSudut(0, 0, 0)
    }


    @Composable
    public fun PosisiXYZ( //Posisi User
        context: Context,
        timestamp: Long?, // Single timestamp value
        accelData: DoubleArray? // Accelerometer data: [ax, ay, az]
    ):IMUTerolahDataPosisi {
        var initialPosition = remember { mutableStateOf(FloatArray(3){0f})  }
        var initialVelocity = remember { mutableStateOf(DoubleArray(3){0.0})  }

        val velocity = remember { mutableStateOf(DoubleArray(3){0.0})  }
        val position = remember { mutableStateOf(FloatArray(3){0f}) }

        val deltaTime = DeltaTime(timestamp)

        if (accelData != null) {
            for (i in accelData.indices){
                velocity.value[i] = initialVelocity.value[i] + (accelData?.get(i) ?: 0.0) * deltaTime

                position.value[i] = (initialPosition.value[i] + velocity.value[i] * deltaTime).toFloat()

                initialVelocity = velocity
                initialPosition = position
            }
        }

        return IMUTerolahDataPosisi(position.value[0], position.value[1], position.value[2] )
    }
}

