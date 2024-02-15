package com.examaple.myapplication.widget;

// WidgetProvider.java inside your widget package

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.examaple.myapplication.CameraActivity;
import com.examaple.myapplication.R;
import com.examaple.myapplication.Slider.SliderChangeListener;

public class WidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // Perform any initialization if needed

        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        // Construct the RemoteViews object
        @SuppressLint("RemoteViewLayout") RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

        // Set up the Click Image button
        Intent clickImageIntent = new Intent(context, CameraActivity.class);
        views.setOnClickPendingIntent(R.id.btnClickImage, PendingIntent.getActivity(context, 0, clickImageIntent, 0));

        // Set up the SeekBar (Slider) functionality
        Intent sliderIntent = new Intent(context, WidgetService.class);
        sliderIntent.setAction("START"); // Add a unique action string here
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, sliderIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.sliderLiveLocation, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}

