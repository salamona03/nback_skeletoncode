package mobappdev.example.nback_cimpl.util

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

/**
 * Helper class wrapping Android's TextToSpeech engine for auditory N-Back spoken letters.
 */
class TextToSpeechHelper(
    context: Context,
    private val onInitComplete: (Boolean) -> Unit = {}
) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = TextToSpeech(context.applicationContext, this)
    private var isInitialized = false

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TextToSpeechHelper", "Language US is not supported or missing data")
                isInitialized = false
                onInitComplete(false)
            } else {
                isInitialized = true
                onInitComplete(true)
            }
        } else {
            Log.e("TextToSpeechHelper", "TextToSpeech initialization failed with status $status")
            isInitialized = false
            onInitComplete(false)
        }
    }

    /**
     * Speaks the given letter aloud.
     * QUEUE_FLUSH interrupts previous audio to ensure immediate feedback.
     */
    fun speak(letter: String) {
        if (isInitialized && letter.isNotBlank()) {
            tts?.speak(letter, TextToSpeech.QUEUE_FLUSH, null, "NBackSpeechId")
        }
    }

    /**
     * Instantly stops any active spoken audio (e.g., when leaving screen or pausing).
     */
    fun stop() {
        tts?.stop()
    }

    /**
     * Stops audio and releases TTS resources when ViewModel is cleared.
     */
    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        isInitialized = false
    }
}
