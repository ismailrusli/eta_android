package com.example.ble_bluetoothlowenergyesp32.data

import com.example.ble_bluetoothlowenergyesp32.util.Resource
import kotlinx.coroutines.flow.MutableSharedFlow

interface JarakReceiveManager {

    val dataFlow:MutableSharedFlow<Resource<JarakResult>>

    fun reconnect()

    fun disconnect()

    fun startReceiving()

    fun closeConnection()

}