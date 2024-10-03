package com.example.ble_bluetoothlowenergyesp32.presentation

data class TextToSpeechState(
    val isTTSEnabled:Boolean = true,
    val text:String = ""
)