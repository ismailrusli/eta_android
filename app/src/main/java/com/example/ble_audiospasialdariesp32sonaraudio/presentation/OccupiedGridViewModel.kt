package com.example.ble_audiospasialdariesp32sonaraudio.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.ble_audiospasialdariesp32sonaraudio.domain.model.IMUTerolahDataPosisi
import com.example.ble_audiospasialdariesp32sonaraudio.domain.model.IMUTerolahDataSudut
import kotlin.math.cos
import kotlin.math.sin

class OccupiedGridViewModel : ViewModel() {

    private val gridSize = 10 // Ukuran grid (10x10x10)
    private val occupiedGrid = Array(gridSize) { Array(gridSize) { Array(gridSize) { false } } }

    // Posisi perangkat (sensor IMU) dalam grid
    private var imuPosition = Triple(gridSize / 2, gridSize / 2, gridSize / 2)

    /**
     * Memperbarui grid berdasarkan posisi perangkat dan sudut objek.
     */
    fun updateGrid(
        radius: Float,
        imuDataPosisi: IMUTerolahDataPosisi, // Posisi perangkat
        sensorDistance: Float, // Jarak objek dari sensor ultrasonik
        imuDataSudut: IMUTerolahDataSudut // Sudut relatif objek
    ) {
        // Perbarui posisi perangkat berdasarkan data akselerometer
        //updateIMUPosition(imuDataPosisi, radius)

        // Tandai posisi objek dalam grid berdasarkan sudut dan jarak
        tagObjectInGrid(sensorDistance, imuDataSudut, radius)
    }

    /**
     * Memperbarui posisi perangkat dalam grid.
     */
    private fun updateIMUPosition(imuDataPosisi: IMUTerolahDataPosisi, radius: Float) {
        val i = ((imuDataPosisi.positionX / radius) * gridSize / 2 + gridSize / 2).toInt().coerceIn(0, gridSize - 1)
        val j = ((imuDataPosisi.positionY / radius) * gridSize / 2 + gridSize / 2).toInt().coerceIn(0, gridSize - 1)
        val k = ((imuDataPosisi.positionZ / radius) * gridSize / 2 + gridSize / 2).toInt().coerceIn(0, gridSize - 1)

        imuPosition = Triple(i, j, k)
        Log.d("OccupiedGridViewModel", "IMU position updated: [$i, $j, $k]")
    }

    /**
     * Menandai posisi objek dalam grid berdasarkan sudut dan jarak.
     */
    private fun tagObjectInGrid(sensorDistance: Float, imuDataSudut: IMUTerolahDataSudut, radius: Float) {
        // Konversi sudut (pitch, yaw) ke koordinat relatif
        val dx = sensorDistance * cos(Math.toRadians(imuDataSudut.pitch.toDouble())) * cos(Math.toRadians(imuDataSudut.yaw.toDouble()))
        val dy = sensorDistance * cos(Math.toRadians(imuDataSudut.pitch.toDouble())) * sin(Math.toRadians(imuDataSudut.yaw.toDouble()))
        val dz = sensorDistance * sin(Math.toRadians(imuDataSudut.pitch.toDouble()))

        // Hitung posisi objek relatif terhadap grid global
        val i = (imuPosition.first + (dx / radius * gridSize / 2)).toInt().coerceIn(0, gridSize - 1)
        val j = (imuPosition.second + (dy / radius * gridSize / 2)).toInt().coerceIn(0, gridSize - 1)
        val k = (imuPosition.third + (dz / radius * gridSize / 2)).toInt().coerceIn(0, gridSize - 1)

        // Tandai grid sebagai "occupied"
        occupiedGrid[i][j][k] = true
        Log.d("OccupiedGridViewModel", "Object tagged at: [$i, $j, $k]")
    }

    /**
     * Getter untuk grid yang telah diperbarui.
     */
    fun getOccupiedGrid(): Array<Array<Array<Boolean>>> {
        return occupiedGrid
    }

    /**
     * Reset semua grid ke keadaan kosong.
     */
    fun resetGrid() {
        for (i in 0 until gridSize) {
            for (j in 0 until gridSize) {
                for (k in 0 until gridSize) {
                    occupiedGrid[i][j][k] = false
                }
            }
        }
        imuPosition = Triple(gridSize / 2, gridSize / 2, gridSize / 2)
        Log.d("OccupiedGridViewModel", "Grid reset.")
    }
}
