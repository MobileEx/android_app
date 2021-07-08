package com.pilates.app.handler;

import android.app.Activity;
import android.os.CountDownTimer;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pilates.app.handler.listeners.OnTimerCompleteListener;

import java.util.Objects;

public class Timer {
    private Activity context;
    private TextView progressText;
    private ProgressBar progressBar;
    private long startTime;
    private CustomTimer countDownTimer;
    private OnTimerCompleteListener listener;
    private String tag;

    public Timer(Activity context, final ProgressBar progressBar) {
        this.context = context;
        this.progressBar = progressBar;
    }

    public void setListener(OnTimerCompleteListener listener) {
        this.listener = listener;
    }

    public Timer(Activity context, final ProgressBar progressBar, final TextView tvProgressText) {
        this.progressBar = progressBar;
        this.progressText = tvProgressText;
        this.context = context;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    public void start(long millisInFuture, long countDownInterval) {
        countDownTimer= new CustomTimer(millisInFuture, countDownInterval);
        startTime = millisInFuture;
        countDownTimer.start();
    }

    public void stop() {
        if (Objects.nonNull(countDownTimer)) {
            countDownTimer.cancel();
        }
    }

    private class CustomTimer extends CountDownTimer {

        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public CustomTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            System.out.println("TIMER: " + millisUntilFinished);
            progressBar.setProgress((int) ((millisUntilFinished / (double)startTime) * 100.0));

            int seconds = (int)(millisUntilFinished / 1000);
            int mins = seconds / 60;
            int secondsToMin = seconds - mins * 60;

            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(progressText != null)
                        progressText.setText(String.format("%02d", mins) + ":" + String.format("%02d", secondsToMin));
                }
            });
        }

        @Override
        public void onFinish() {
            listener.completed();
        }
    }
}
