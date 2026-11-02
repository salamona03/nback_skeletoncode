package mobappdev.example.nback_cimpl.util

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

/**
 * Text-to-Speech helper class for speaking auditory N-Back stimuli.
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

    fun speak(letter: String) {
        if (isInitialized && letter.isNotBlank()) {
            tts?.speak(letter, TextToSpeech.QUEUE_FLUSH, null, "NBackSpeechId")
        }
    }

    fun stop() {
        tts?.stop()
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        isInitialized = false
    }
}
