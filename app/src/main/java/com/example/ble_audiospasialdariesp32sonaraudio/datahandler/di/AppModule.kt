package com.example.ble_audiospasialdariesp32sonaraudio.datahandler.di

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import com.example.ble_audiospasialdariesp32sonaraudio.domain.repo.ESP32DataReceiveManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideBluetoothAdapter(@ApplicationContext context: Context): BluetoothAdapter {
        val manager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        return  manager.adapter
    }


    @Provides
    @Singleton
    fun provideESP32DataReceiveManager(
        @ApplicationContext context: Context,
        bluetoothAdapter: BluetoothAdapter
    ):ESP32DataReceiveManager{
        return com.example.ble_audiospasialdariesp32sonaraudio.datahandler.ble.ESP32DataReceiveManager(context, bluetoothAdapter)
    }
}