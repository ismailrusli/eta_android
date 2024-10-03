import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import com.example.ble_bluetoothlowenergyesp32.R
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.sqrt

class SoundControllerViewModel(context: Context) : ViewModel() {
    private val soundPool: SoundPool
    private var buzzingSound: Int = 0
    private var streamId: Int = 0 // Menyimpan streamId untuk kontrol play/stop

    init {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(1)
            .setAudioAttributes(audioAttributes)
            .build()

        // Load the buzzing sound from the raw resource
        buzzingSound = soundPool.load(context, R.raw.buzzing_sound, 1)
    }

    fun playBuzzingSound(xPos: Float, yPos: Float, zPos: Float, jarakMaksimum: Int) {
        // Menghitung jarak dari pusat ke titik (xPos, yPos)
        val jari_jari = sqrt(xPos * xPos + yPos * yPos) // Jarak

        // Menghitung sudut θ dalam radian dan membatasi hanya untuk kuadran 1 dan 2
        var theta = atan2(yPos, xPos)

        // Membatasi theta hanya untuk kuadran 1 dan 2 (0 <= theta <= pi)
        if (theta < 0) {
            theta += PI.toFloat()
        }

        // Normalisasi jarak (semakin jauh jarak, semakin kecil volume)
        val volume = (1 - (jari_jari / jarakMaksimum)).coerceIn(0f, 1f)

        // Hitung panning berdasarkan sudut theta (kuadran 1 dan 2)
        val leftVolume = (theta / PI.toFloat()) * volume
        val rightVolume = (1 - theta / PI.toFloat()) * volume




        Log.d("SoundController", "xPos: $xPos, yPos: $yPos, theta: $theta, volume: $volume, jarak: $jari_jari")

        if (streamId == 0) {
            // Jika belum diputar, mulai putar suara dan simpan streamId
            streamId = soundPool.play(buzzingSound, leftVolume, rightVolume, 1, -1, 1f)
        } else {
            // Jika suara sudah diputar, perbarui volume tanpa menghentikan suara
            soundPool.setVolume(streamId, leftVolume, rightVolume)
        }
    }

    // Fungsi untuk menghentikan suara secara manual
    fun stopBuzzingSound() {
        if (streamId != 0) {
            soundPool.stop(streamId)
            streamId = 0 // Reset streamId setelah dihentikan
        }
    }

    // Fungsi untuk membersihkan resource SoundPool
    fun release() {
        soundPool.release()
    }
}
