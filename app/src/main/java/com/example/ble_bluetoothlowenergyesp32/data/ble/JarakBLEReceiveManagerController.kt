package com.example.ble_bluetoothlowenergyesp32.data.ble

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.util.Log
import com.example.ble_bluetoothlowenergyesp32.data.ConnectionState
import com.example.ble_bluetoothlowenergyesp32.data.JarakReceiveManager
import com.example.ble_bluetoothlowenergyesp32.data.JarakResult
import com.example.ble_bluetoothlowenergyesp32.util.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

//Bluetooth LE Controller
@SuppressLint("MissingPermission")
//^^ Permission Check di Start Screen, disini controller
class JarakBLEReceiveManagerController @Inject constructor(
    private val bluetoothAdapter: BluetoothAdapter,
    private val context: Context
):JarakReceiveManager {
    private val DEVICE_NAME = "BT-ESP32-Slave"

    //Cek di Dokumentasi Bluetooth LE
    private val JARAK_SERVICE_UUID = "4fafc201-1fb5-459e-8fcc-c5c9c331914b"
    private val JARAK_CHARASTERISTICS_UUID = "beb5483e-36e1-4688-b7f5-ea07361b26a8"


    override val dataFlow: MutableSharedFlow<Resource<JarakResult>> = MutableSharedFlow()

    private val bleScanner by lazy {
        bluetoothAdapter.bluetoothLeScanner
    }

    private val scanSettings = ScanSettings.Builder()
        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
        .build()


    private var gatt:BluetoothGatt ?= null

    private var isScanning = false

    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    private val scanCallback = object:ScanCallback(){
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            if (result.device.name == DEVICE_NAME && isScanning) {
                coroutineScope.launch {
                    try {
                        dataFlow.emit(Resource.Loading(message = "Mencoba Terhubung ke Perangkat"))
                    }catch (e: Exception) {
                        Log.e("JarakReceiveManager", "Error emitting data: ${e.message}")
                    }

                }
                result.device.connectGatt(context, false, gattCallback, BluetoothDevice.TRANSPORT_LE)
                isScanning = false
                bleScanner.stopScan(this)  // Ensure scanning stops after the first match
            }
        }

    }

    private var currentConnectionAttempt = 1
    private var MAX_CONNECTION_ATTEMPT = 5

    private val gattCallback = object :BluetoothGattCallback(){
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    coroutineScope.launch {
                        try {
                            dataFlow.emit(Resource.Loading(message = "Menemukan Perangkat"))
                        }catch (e: Exception) {
                            Log.e("JarakReceiveManager", "Error emitting data: ${e.message}")
                        }

                    }
                    gatt.discoverServices()
                    this@JarakBLEReceiveManagerController.gatt = gatt
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    coroutineScope.launch {
                        try {
                            dataFlow.emit(Resource.Success(data = JarakResult(0f, ConnectionState.Disconnected)))
                        }catch (e: Exception) {
                            Log.e("JarakReceiveManager", "Error emitting data: ${e.message}")
                        }

                    }
                    gatt.close()
                }
            } else {
                Log.e("JarakBLEReceiveManagerController", "GATT connection failed with status $status")
                gatt.close()
                currentConnectionAttempt += 1
                coroutineScope.launch {
                    try {
                        // Show retry attempt message
                        dataFlow.emit(
                            Resource.Loading(
                                message = "Mencoba Menghubungkan ke Perangkat ... $currentConnectionAttempt / $MAX_CONNECTION_ATTEMPT"
                            )
                        )
                    }catch (e: Exception) {
                        Log.e("JarakReceiveManager", "Error emitting data: ${e.message}")
                    }



                    if (currentConnectionAttempt <= MAX_CONNECTION_ATTEMPT) {
                        // Add delay before retrying
                        delay(2000L)  // <-- `delay()` is safe here since it's inside a coroutine

                        // Retry connection after delay
                        reconnect()
                    } else {
                        try {
                            dataFlow.emit(Resource.Error(errorMessage = "Tidak Bisa Terhubung ke Perangkat"))
                        }catch (e: Exception) {
                            Log.e("JarakReceiveManager", "Error emitting data: ${e.message}")
                        }

                    }
                }
            }
        }


        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            with(gatt){
                printGattTable()
                coroutineScope.launch {
                    try {
                        dataFlow.emit(Resource.Loading(message = "Menyesuaikan MTU"))
                    }catch (e: Exception) {
                        Log.e("JarakReceiveManager", "Error emitting data: ${e.message}")
                    }

                }
                gatt.requestMtu(517)
            }
        }

        override fun onMtuChanged(gatt: BluetoothGatt, mtu: Int, status: Int) {
            Log.d("JarakBLEReceiveManagerController", "MTU changed to: $mtu with status: $status")
            if (status == BluetoothGatt.GATT_SUCCESS) {
                val characteristic = findCharacteristic(JARAK_SERVICE_UUID, JARAK_CHARASTERISTICS_UUID)
                if (characteristic == null) {
                    coroutineScope.launch {
                        try {
                            dataFlow.emit(Resource.Error(errorMessage = "Tidak Bisa Menemukan Penerbit Jarak"))
                        }catch (e: Exception) {
                            Log.e("JarakReceiveManager", "Error emitting data: ${e.message}")
                        }

                    }
                    return
                }
                enableNotification(characteristic)
            } else {
                coroutineScope.launch {
                    try {
                        dataFlow.emit(Resource.Error(errorMessage = "MTU negotiation failed."))
                    }catch (e: Exception) {
                        Log.e("JarakReceiveManager", "Error emitting data: ${e.message}")
                    }

                }
            }
        }


        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray,
            status: Int
        ) {
            with(characteristic){
                when(uuid){
                    UUID.fromString(JARAK_CHARASTERISTICS_UUID) -> {
                        //perhatikan byte array nanti
                        //Bagusnya tuh gini
                        //AB CD Centimeter
                        //AB itu byte angka di depan koma, CD angka di belakang koma
                        val jarak = value[0].toInt() + value[1].toInt()/10f
                        val jarakResult = JarakResult(
                            jarak,
                            ConnectionState.Connected
                        )
                        coroutineScope.launch {
                            try {
                                dataFlow.emit(
                                    Resource.Success(
                                        data = jarakResult
                                    )
                                )
                            }catch (e: Exception) {
                                Log.e("JarakReceiveManager", "Error emitting data: ${e.message}")
                            }


                        }
                    }
                    else -> Unit
                }
            }
        }

    }


    //Kalau Jarak Berubah di Notif Nanti
    private fun enableNotification(characteristic: BluetoothGattCharacteristic) {
        val cccdUuid = UUID.fromString(CCCD_DESCRIPTOR_UUID)
        val payload = when {
            characteristic.isIndicatable() -> BluetoothGattDescriptor.ENABLE_INDICATION_VALUE
            characteristic.isNotifiable() -> BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
            else -> return
        }

        characteristic.getDescriptor(cccdUuid)?.let { cccdDescriptor ->
            if (gatt?.setCharacteristicNotification(characteristic, true) == false) {
                coroutineScope.launch {
                    try {
                        dataFlow.emit(
                            Resource.Error(errorMessage = "Gagal mengaktifkan notifikasi untuk karakteristik jarak.")
                        )
                    }catch (e: Exception) {
                        Log.e("JarakReceiveManager", "Error emitting data: ${e.message}")
                    }


                }
                return
            }
            writeDescription(cccdDescriptor, payload)
        } ?: coroutineScope.launch {
            try {
                dataFlow.emit(Resource.Error(errorMessage = "Descriptor tidak ditemukan untuk karakteristik jarak."))
            }catch (e: Exception) {
                Log.e("JarakReceiveManager", "Error emitting data: ${e.message}")
            }


        }
    }


    private fun writeDescription(descriptor: BluetoothGattDescriptor, payload:ByteArray){
        gatt?.let { gatt ->
            descriptor.value = payload
            gatt.writeDescriptor(descriptor)
        }?: error("Tidak Seddang Terhubung ke Perangkat")
    }

    //Jangan Lupa cek dokumentasi Karakteristik Sensor BLE
    private fun findCharacteristic(serviceUUID:String, characteristicsUUID:String): BluetoothGattCharacteristic? {
        return gatt?.services?.find { service ->
            service.uuid.toString() == serviceUUID
        }?.characteristics?.find { characteristics ->
            characteristics.uuid.toString() == characteristicsUUID
        }
    }

    override fun reconnect() {
        gatt?.connect()
    }

    override fun disconnect() {
        gatt?.disconnect()
    }

    override fun startReceiving() {
        coroutineScope.launch {
            try {
                dataFlow.emit(Resource.Loading(message = "Memindai Perangkat"))
                Log.d("JarakReceiveManager", "Emitting loading message: Memindai Perangkat")

                isScanning = true
                bleScanner.startScan(null, scanSettings, scanCallback)
                Log.d("JarakReceiveManager", "Scanning started.")
            } catch (e: Exception) {
                Log.e("JarakReceiveManager", "Error emitting data: ${e.message}")
            }
        }
    }

    override fun closeConnection() {
        bleScanner.stopScan(scanCallback)
        val characteristic = findCharacteristic(JARAK_SERVICE_UUID, JARAK_CHARASTERISTICS_UUID)
        if (characteristic != null){
            disconnectCharacteristics(characteristic)
        }
        gatt?.close()
    }

    private fun disconnectCharacteristics(characteristic: BluetoothGattCharacteristic){
        val cccdUuid = UUID.fromString(CCCD_DESCRIPTOR_UUID)
        characteristic.getDescriptor(cccdUuid)?.let { cccdDescriptor ->
            if(gatt?.setCharacteristicNotification(characteristic,false) == false){
                Log.d("JarakBLEReceiverManagerController","set charateristics notification failed")
                return
            }
            writeDescription(cccdDescriptor, BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE)
        }
    }

}