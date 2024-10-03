package com.example.ble_bluetoothlowenergyesp32.util

//Data Hasil Koneksi, dia nanti ngambil flow dari controller masukkan ke sini
sealed class Resource <out T: Any> {
    data class Success<out T:Any> (val data:T):Resource<T>()
    data class Error(val errorMessage:String):Resource<Nothing>()
    data class Loading<out T:Any>(val data:T? = null, val message:String? = null):Resource<T>()
}