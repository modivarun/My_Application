package com.examaple.myapplication.Slider;

// SliderChangeListener.java inside your widget package

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.SeekBar;

public class SliderChangeListener implements SeekBar.OnSeekBarChangeListener {

    private Context context;
    private int appWidgetId;
    private Intent serviceIntent;

    public SliderChangeListener(Context context, int appWidgetId, Intent serviceIntent) {
        this.context = context;
        this.appWidgetId = appWidgetId;
        this.serviceIntent = serviceIntent;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        // Handle SeekBar changes as needed
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // Handle SeekBar touch start as needed
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // Handle SeekBar touch stop as needed
    }
}

