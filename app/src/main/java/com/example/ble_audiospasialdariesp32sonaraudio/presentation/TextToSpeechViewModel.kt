package com.example.ble_audiospasialdariesp32sonaraudio.presentation

import android.content.Context
import android.speech.tts.TextToSpeech
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ble_audiospasialdariesp32sonaraudio.domain.model.TTSData
import kotlinx.coroutines.launch
import java.util.Locale

class TextToSpeechViewModel(
    context: Context
): ViewModel() {
    private var tts: TextToSpeech? = null
    var data by mutableStateOf(TTSData())
        private set

    init {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.getDefault()
            }
        }
    }

    // Update the text to be spoken
    fun updateText(newText: String) {
        data = data.copy(text = newText)
    }

    // Toggle TTS enabled/disabled
    fun toggleTTS() {
        if (data.isTTSEnabled) {
            data = data.copy(isTTSEnabled = false)
            tts?.stop()
        } else {
            data = data.copy(isTTSEnabled = true)
            speakText()  // Immediately speak the text when TTS is enabled
        }
    }

    // Speak the text
    fun speakText() {
        if (data.isTTSEnabled && data.text.isNotEmpty()) {
            viewModelScope.launch {
                tts?.speak(data.text, TextToSpeech.QUEUE_FLUSH, null, null)
            }
        }
    }

    override fun onCleared() {
        tts?.shutdown()
        tts?.stop()
        super.onCleared()
    }
}
