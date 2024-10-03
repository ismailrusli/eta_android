#include <oboe/Oboe.h>
#include <jni.h>
#include <math.h>
#include <memory>  // Menambahkan header untuk std::shared_ptr

class MyAudioStream : public oboe::AudioStreamCallback {
public:
    float leftGain = 1.0f;
    float rightGain = 1.0f;
    std::shared_ptr<oboe::AudioStream> stream; // Menyimpan stream di sini

    oboe::DataCallbackResult onAudioReady(
            oboe::AudioStream *oboeStream,
            void *audioData,
            int32_t numFrames) override {

        float *floatData = static_cast<float *>(audioData);

        for (int i = 0; i < numFrames; ++i) {
            floatData[2 * i] *= leftGain;   // Channel kiri
            floatData[2 * i + 1] *= rightGain; // Channel kanan
        }

        return oboe::DataCallbackResult::Continue;
    }

    void setPosition(float x, float y, float z) {
        // Mengatur panning berdasarkan posisi X, Y, dan Z
        leftGain = fmax(0.0f, 1.0f - fabs(x));
        rightGain = fmax(0.0f, 1.0f - fabs(-x));
        // Anda bisa menambahkan logika untuk Z jika diperlukan
    }
};

MyAudioStream *audioStream = nullptr;

extern "C" JNIEXPORT void JNICALL
Java_com_example_ble_1bluetoothlowenergyesp32_MainActivity_createAudioStream(JNIEnv *env, jobject) {
    audioStream = new MyAudioStream();

    oboe::AudioStreamBuilder builder;
    // Set parameter builder sebelum memanggil openStream
    builder.setChannelCount(oboe::ChannelCount::Stereo)
            ->setSampleRate(48000)
            ->setCallback(audioStream);

    // Membuka stream dan menyimpan di objek MyAudioStream
    oboe::Result result = builder.openStream(audioStream->stream);
    if (result != oboe::Result::OK) {
        // Tangani kesalahan jika tidak bisa membuka stream
        // Misalnya, log kesalahan
    }

    audioStream->setPosition(0.0f, 0.0f, 0.0f);  // Set posisi awal
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_ble_1bluetoothlowenergyesp32_MainActivity_setAudioPosition(JNIEnv *env, jobject, jfloat x, jfloat y, jfloat z) {
    if (audioStream) {
        audioStream->setPosition(x, y, z);  // Update posisi suara
    }
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_ble_1bluetoothlowenergyesp32_MainActivity_play(JNIEnv *env, jobject) {
    if (audioStream && audioStream->stream) {
        audioStream->stream->requestStart(); // Memanggil requestStart pada stream
    }
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_ble_1bluetoothlowenergyesp32_MainActivity_stop(JNIEnv *env, jobject) {
    if (audioStream && audioStream->stream) {
        audioStream->stream->requestStop(); // Memanggil requestStop pada stream
    }
}
