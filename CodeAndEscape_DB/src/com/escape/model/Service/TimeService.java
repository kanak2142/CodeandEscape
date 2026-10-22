package com.escape.model.Service;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import com.escape.model.GameState;

public class TimeService {

    private static TimeService instance;

    private Timer timer;

    private int timeRemaining;

    private boolean isRunning;


    private Vector<TimerListener> listeners;

    // Private constructor
    private TimeService() {
        this.timeRemaining = 600;       // 10 minutes
        this.listeners = new Vector<>();
        this.isRunning = false;
    }

    // Get single instance
    public static TimeService getInstance() {
        if (instance == null) {
            instance = new TimeService();
        }
        return instance;
    }

    public void start(GameState state) {
        if (isRunning) return;

        isRunning = true;
        timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (timeRemaining > 0) {
                    timeRemaining--;
                    state.setTimeRemaining(timeRemaining);
                    notifyTick(timeRemaining);

                    if (timeRemaining == 0) {
                        state.setGameOver(true);
                        notifyTimeUp();
                        stop();
                    }
                }
            }
        }, 1000, 1000);  // 1 second delay, then every 1 second
    }

    public void pause() {
        if (timer != null) {
            timer.cancel();
            isRunning = false;
        }
    }

    public void resume(GameState state) {
        start(state);
    }


    public void stop() {
        if (timer != null) {
            timer.cancel();
            isRunning = false;
        }
    }

    public void reset(GameState state) {
        stop();
        this.timeRemaining = 600;
        state.setTimeRemaining(600);
    }

    public void setTime(int seconds) {
        this.timeRemaining = seconds;
    }

    public int getTimeRemaining() {
        return timeRemaining;
    }

    public boolean isRunning() {
        return isRunning;
    }

    // ===== LISTENERS =====
    public void addListener(TimerListener listener) {
        listeners.add(listener);
    }

    public void removeListener(TimerListener listener) {
        listeners.remove(listener);
    }

    private void notifyTick(int seconds) {
        for (TimerListener l : listeners) {
            l.onTick(seconds);
        }
    }

    private void notifyTimeUp() {
        for (TimerListener l : listeners) {
            l.onTimeUp();
        }
    }

    public interface TimerListener {
        void onTick(int secondsRemaining);
        void onTimeUp();
    }
}