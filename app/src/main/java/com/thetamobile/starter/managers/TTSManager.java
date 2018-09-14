package com.thetamobile.starter.managers;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import com.thetamobile.starter.Application;
import com.thetamobile.starter.R;

import java.util.Locale;


public class TTSManager {

    private static TTSManager TTSManager;
    private static TextToSpeech textToSpeech;

    private TTSManager() {
    }

    public static TTSManager getInstance() {
        if (TTSManager == null) {
            TTSManager = new TTSManager();
            textToSpeech = new TextToSpeech(Application.getContext(), status -> {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.ENGLISH);
                }
            });
        }
        return TTSManager;
    }

    public void play(Context context, String text) {
        if (textToSpeech != null) {
            if (!textToSpeech.isSpeaking()) {
                if (SharedPreferencesManager.getInstance().getBoolean(context.getString(R.string.tts_voice))) {
                    textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        }
    }

    public void stop() {
        if (textToSpeech.isSpeaking()) {
            textToSpeech.stop();
        }
    }
}
