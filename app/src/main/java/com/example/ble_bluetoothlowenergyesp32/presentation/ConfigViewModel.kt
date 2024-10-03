package com.example.ble_bluetoothlowenergyesp32.presentation

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import com.example.ble_bluetoothlowenergyesp32.data.ConfigData
import dagger.hilt.android.lifecycle.HiltViewModel


class ConfigViewModel(

) : ViewModel() {

    private val _minJarak = mutableStateOf(0f)
    val minJarak: State<Float> = _minJarak

    private val _maxJarak = mutableStateOf(100f)
    val maxJarak: State<Float> = _maxJarak

    private val _minAmplitudo = mutableStateOf(1)
    val minAmplitudo: State<Int> = _minAmplitudo

    private val _maxAmplitudo = mutableStateOf(100)
    val maxAmplitudo: State<Int> = _maxAmplitudo

    private val _durasiGetar = mutableStateOf(1000L)
    val durasiGetar: State<Long> = _durasiGetar

    private val _opsiUX = mutableStateOf("Option1")
    val opsiUX: State<String> = _opsiUX

    // Function to update the data
    fun updateConfigData(newConfigData: ConfigData) {
        _minJarak.value = newConfigData.minJarak
        _maxJarak.value = newConfigData.maxJarak
        _minAmplitudo.value = newConfigData.minAmplitudo
        _maxAmplitudo.value = newConfigData.maxAmplitudo
        _durasiGetar.value = newConfigData.durasiGetar
        _opsiUX.value = newConfigData.opsiUX
    }
}
