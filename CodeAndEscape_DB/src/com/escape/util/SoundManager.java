package com.escape.util;

import javax.sound.sampled.*;
import java.net.URL;

public class SoundManager {

    // Keeping a reference to the background clip so we could pause/stop it later if needed
    private static Clip backgroundClip;

    // Plays a short sound effect once (for clicks, puzzles, etc.)
    public static void playSound(String soundFilePath) {
        try {
            new Thread(() -> {
                try {
                    URL soundURL = SoundManager.class.getResource(soundFilePath);
                    if (soundURL != null) {
                        AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundURL);
                        Clip clip = AudioSystem.getClip();
                        clip.open(audioIn);
                        clip.start();
                    } else {
                        System.err.println("Could not find sound: " + soundFilePath);
                    }
                } catch (Exception e) {
                    System.err.println("Error playing sound: " + e.getMessage());
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // NEW: Plays a longer audio track and loops it endlessly
    public static void playBackgroundMusic(String musicFilePath) {
        try {
            new Thread(() -> {
                try {
                    URL musicURL = SoundManager.class.getResource(musicFilePath);
                    if (musicURL != null) {
                        AudioInputStream audioIn = AudioSystem.getAudioInputStream(musicURL);
                        backgroundClip = AudioSystem.getClip();
                        backgroundClip.open(audioIn);

                        // Tell the clip to loop forever
                        backgroundClip.loop(Clip.LOOP_CONTINUOUSLY);
                        backgroundClip.start();
                    } else {
                        System.err.println("Could not find background music: " + musicFilePath);
                    }
                } catch (Exception e) {
                    System.err.println("Error playing background music: " + e.getMessage());
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}