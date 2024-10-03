package com.example.ble_bluetoothlowenergyesp32.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ble_bluetoothlowenergyesp32.data.ConnectionState
import com.example.ble_bluetoothlowenergyesp32.data.JarakReceiveManager
import com.example.ble_bluetoothlowenergyesp32.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BluetoothLEViewModel @Inject constructor(
    private val jarakReceiveManager: JarakReceiveManager
):ViewModel() {

    var initialzingMessage by mutableStateOf<String?>(value = null)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private  set

    var jarak by mutableStateOf(0f)
        private set

    var connectionState by mutableStateOf<ConnectionState>(ConnectionState.Uninitialized)


    private var hasSubscribed = false

    private fun subscribedToChanges(){
        viewModelScope.launch{
            jarakReceiveManager.dataFlow.collect{ result ->
                when(result){
                    is Resource.Success ->{
                        connectionState = result.data.connectionState
                        jarak = result.data.jarak
                    }
                    is Resource.Loading ->{
                        initialzingMessage = result.message
                        connectionState = ConnectionState.CurrentlyInitializing
                    }
                    is Resource.Error ->{
                        errorMessage = result.errorMessage
                        connectionState = ConnectionState.Uninitialized
                    }
                }
            }
        }
    }


    fun reconnect(){
        jarakReceiveManager.reconnect()
    }

    fun disconnect(){
        jarakReceiveManager.disconnect()
    }

    fun initializeConnection(){
        errorMessage = null
        subscribedToChanges()
        jarakReceiveManager.startReceiving()
    }

    override fun onCleared(){
        super.onCleared()
        jarakReceiveManager.closeConnection()
    }

}