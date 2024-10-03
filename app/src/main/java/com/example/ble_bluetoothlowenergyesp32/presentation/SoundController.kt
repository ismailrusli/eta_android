import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.util.Log
import com.example.ble_bluetoothlowenergyesp32.R
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.sqrt

class SoundController(context: Context) {
    private val soundPool: SoundPool
    private var buzzingSound: Int = 0

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

        // Memutar suara dengan volume yang telah dihitung
        soundPool.play(buzzingSound, leftVolume, rightVolume, 1, 0, 1f)
    }

    fun release() {
        soundPool.release()
    }
}
