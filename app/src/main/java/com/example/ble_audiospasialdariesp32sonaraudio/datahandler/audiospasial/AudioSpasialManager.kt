package com.example.ble_audiospasialdariesp32sonaraudio.datahandler.audiospasial

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.ble_audiospasialdariesp32sonaraudio.R
import kotlin.math.PI
import kotlin.math.sin

class AudioSpasialManager(
    context: Context
) {
    private val soundPool: SoundPool
    private var buzzingSound: Int = 0
    private var pingSound: Int = 0
    private var streamIdLeft: Int = 0
    private var streamIdRight: Int = 0

    init {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(2)
            .setAudioAttributes(audioAttributes)
            .build()

        buzzingSound = soundPool.load(context, R.raw.buzzing_sound, 1)
        pingSound = soundPool.load(context, R.raw.sonar_ping_sound, 1)
    }

    private fun getSoundToPlay(soundOption: String): Int {
        return when (soundOption) {
            "ping" -> pingSound
            "buzzing" -> buzzingSound
            else -> buzzingSound
        }
    }

    // Interaural Intensity Difference (IID) and Volume Calculation
    private fun calculateIIDVolume(
        distance: Float,
        radius: Float,
        maxVolume: Float,
        theta: Int
    ): Pair<Float, Float> {
        val normalizedVolume = (1 - (distance / radius)).coerceIn(0f, 1f) * maxVolume
        val leftVolume = (theta / PI.toFloat()) * normalizedVolume
        val rightVolume = (1 - theta / PI.toFloat()) * normalizedVolume
        return Pair(leftVolume, rightVolume)
    }

    // Interaural Time Difference (ITD) Calculation
    private fun calculateITD(theta: Int): Double {
        // Average distance between ears ~20cm; speed of sound ~343 m/s
        return (20 * sin(theta.toDouble())) / 34300 * 1000 // Return ITD in milliseconds
    }

    fun simulate3DAudio(
        distance: Float,
        radius: Float,
        yaw: Int, // Yaw is now an integer
        soundOption: String
    ) {
        val adjustedYaw = yaw.coerceIn(0, 180) // Ensure yaw is within 0 to 180
        val (leftVolume, rightVolume) = calculateIIDVolume(distance, radius, 1f, adjustedYaw)
        val itdDelay = calculateITD(adjustedYaw)

        Log.d(
            "AudioSimulation",
            "Distance: $distance, Radius: $radius, Yaw: $yaw, ITD: $itdDelay ms, Volumes: L=$leftVolume R=$rightVolume"
        )

        val soundToPlay = getSoundToPlay(soundOption)

        if (streamIdLeft == 0 && streamIdRight == 0) {
            when {
                yaw > 90 -> {
                    streamIdLeft = soundPool.play(soundToPlay, leftVolume, 0f, 1, -1, 1f)
                    Handler(Looper.getMainLooper()).postDelayed({
                        streamIdRight = soundPool.play(soundToPlay, 0f, rightVolume, 1, -1, 1f)
                    }, itdDelay.toLong())
                }
                yaw == 90 -> {
                    streamIdLeft = soundPool.play(soundToPlay, leftVolume, 0f, 1, -1, 1f)
                    streamIdRight = soundPool.play(soundToPlay, 0f, rightVolume, 1, -1, 1f)
                }
                yaw < 90 -> {
                    streamIdRight = soundPool.play(soundToPlay, 0f, rightVolume, 1, -1, 1f)
                    Handler(Looper.getMainLooper()).postDelayed({
                        streamIdLeft = soundPool.play(soundToPlay, leftVolume, 0f, 1, -1, 1f)
                    }, itdDelay.toLong())
                }
            }
        } else {
            soundPool.setVolume(streamIdLeft, leftVolume, 0f)
            soundPool.setVolume(streamIdRight, 0f, rightVolume)
        }
    }


    fun stop3DAudio() {
        if (streamIdLeft != 0) {
            soundPool.stop(streamIdLeft)
            streamIdLeft = 0
        }
        if (streamIdRight != 0) {
            soundPool.stop(streamIdRight)
            streamIdRight = 0
        }
    }

    fun release() {
        soundPool.release()
    }
}
