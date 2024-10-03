package com.example.ble_bluetoothlowenergyesp32.data

//Data koneksi lagi ngapain
sealed interface ConnectionState {
    object Connected: ConnectionState
    object Disconnected: ConnectionState
    object Uninitialized: ConnectionState
    object CurrentlyInitializing: ConnectionState
}