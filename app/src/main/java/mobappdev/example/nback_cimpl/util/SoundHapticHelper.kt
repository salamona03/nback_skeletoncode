package mobappdev.example.nback_cimpl.util

import android.media.AudioManager
import android.media.ToneGenerator
import android.view.HapticFeedbackConstants
import android.view.View

/**
 * Singleton helper providing instant tactile (haptic vibration) and audio beep feedback on user button interactions.
 */
object SoundHapticHelper {
    private var toneGenerator: ToneGenerator? = null

    /**
     * Triggers physical haptic vibration and a short audio beep sound for a button tap.
     * @param view Current Composable's LocalView context.
     */
    fun playTapFeedback(view: View) {
        // Trigger device haptic vibration feedback
        try {
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
        } catch (_: Exception) {
            // Gracefully ignore if haptics are disabled or unavailable
        }

        // Play short audio confirmation beep using ToneGenerator
        try {
            if (toneGenerator == null) {
                toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 75)
            }
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 60)
        } catch (_: Exception) {
            // Gracefully ignore if audio stream is unavailable
        }
    }
}
