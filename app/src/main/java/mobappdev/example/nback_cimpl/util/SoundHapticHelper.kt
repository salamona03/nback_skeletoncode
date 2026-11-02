package mobappdev.example.nback_cimpl.util

import android.media.AudioManager
import android.media.ToneGenerator
import android.view.HapticFeedbackConstants
import android.view.View

/**
 * Helper class providing tactile (haptic) and audio feedback on button taps.
 */
object SoundHapticHelper {
    private var toneGenerator: ToneGenerator? = null

    fun playTapFeedback(view: View) {
        // Trigger haptic vibration feedback
        try {
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
        } catch (e: Exception) {
            // Ignore if haptics unavailable
        }

        // Play short pleasant tap beep sound
        try {
            if (toneGenerator == null) {
                toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 75)
            }
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 60)
        } catch (e: Exception) {
            // Ignore if audio unavailable
        }
    }
}
